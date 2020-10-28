//
//  OKClickTextView.m
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKClickTextView.h"
@interface OKClickTextView()
#define  kCoverViewTag 111

@property (nonatomic, strong)NSMutableArray *rectsArray;
@property (nonatomic, strong)NSMutableAttributedString *content;

@end

@implementation OKClickTextView

/*
 // Only override drawRect: if you perform custom drawing.
 // An empty implementation adversely affects performance during animation.
 - (void)drawRect:(CGRect)rect {
 // Drawing code
 }
 */

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // 如果说 UITextView 设置了不能编辑，并且又设置上了文字，直接运行第一次会发现文字加载不出来
        [self setEditable:NO];
        // 必须实现 ScrollView 禁止滚动才行
        [self setScrollEnabled:NO];
        [self setIsShowUnderline:NO];
    }
    return self;
}

- (void)setText:(NSString *)text
{
    [super setText:text];
    // 设置的text的相关属性，都添加到 content 中
    self.content = [[NSMutableAttributedString alloc] initWithString:text];
    [self.content addAttribute:NSFontAttributeName value:self.font range:NSMakeRange(0, text.length)];
    if(self.textColor){
        [self.content addAttribute:NSForegroundColorAttributeName value:self.textColor range:NSMakeRange(0, text.length)];
    }
}


/**
 *  设置textView的部分为下划线，并且使之可以点击
 *
 *  @param underlineTextRange 需要下划线的文字范围，如果NSRange范围超出总的内容，将过滤掉
 *  @param color              下划线的颜色，以及下划线上面文字的颜色
 *  @param coverColor         是否有点击的背景，如果设置相关颜色的话，将会有点击效果，如果为nil将没有点击效果
 *  @param block              点击文字的时候的回调
 */

- (void)setUnderlineTextWithRange:(NSRange)underlineTextRange withUnderlineColor:(UIColor *)color withClickCoverColor:(UIColor *)coverColor withBlock:(clickTextViewPartBlock)block
{
    if (self.text.length < underlineTextRange.location+underlineTextRange.length) {
        return;
    }
    
    // 设置下划线
    if (self.isShowUnderline) {
        [self.content addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:underlineTextRange];
    }
    //设置文字颜色
    if (color) {
        [self.content addAttribute:NSForegroundColorAttributeName value:color range:underlineTextRange];
    }
    self.attributedText = self.content;
    
    // 设置下划线文字的点击事件
    // self.selectedRange  影响  self.selectedTextRange
    self.selectedRange = underlineTextRange;
    
    // 获取选中范围内的矩形框
    NSArray *selectionRects = [self selectionRectsForRange:self.selectedTextRange];
    // 清空选中范围
    self.selectedRange = NSMakeRange(0, 0);
    // 可能会点击的范围的数组
    NSMutableArray *selectedArray = [[NSMutableArray alloc] init];
    for (UITextSelectionRect *selectionRect in selectionRects) {
        CGRect rect = selectionRect.rect;
        if (rect.size.width == 0 || rect.size.height == 0) {
            continue;
        }
        // 将有用的信息打包<存放到字典中>存储到数组中
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        // 存储文字对应的frame，一段文字可能会有两个甚至多个frame，考虑到文字换行问题
        [dic setObject:[NSValue valueWithCGRect:rect] forKey:@"rect"];
        // 存储下划线对应的文字
        [dic setObject:[self.text substringWithRange:underlineTextRange] forKey:@"content"];
        // 存储相应的回调的block
        [dic setObject:block forKey:@"block"];
        // 存储对应的点击效果背景颜色
        [dic setValue:coverColor forKey:@"coverColor"];
        [selectedArray addObject:dic];
    }
    // 将可能点击的范围的数组存储到总的数组中
    [self.rectsArray addObject:selectedArray];
    
}

// 点击textView的 touchesBegan 方法
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    // 获取触摸对象
    UITouch *touch = [touches anyObject];
    
    // 触摸点
    CGPoint point = [touch locationInView:self];
    // 通过一个触摸点，查询点击的是不是在下划线对应的文字的frame
    NSArray *selectedArray = [self touchingSpecialWithPoint:point];
    for (NSDictionary *dic in selectedArray) {
        if(dic && dic[@"coverColor"]){
            UIView *cover = [[UIView alloc] init];
            cover.backgroundColor = dic[@"coverColor"];
            cover.frame = [dic[@"rect"] CGRectValue];
            cover.layer.cornerRadius = 5;
            cover.tag = kCoverViewTag;
            [self insertSubview:cover atIndex:0];
        }
    }
    if (selectedArray.count) {
        // 如果说有点击效果的话，加个延时，展示下点击效果,如果没有点击效果的话，直接回调
        NSDictionary *dic = [selectedArray firstObject];
        clickTextViewPartBlock block = dic[@"block"];
        if (dic[@"coverColor"]) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                block(dic[@"content"]);
            });
        }else{
            block(dic[@"content"]);
        }
    }
}

- (NSArray *)touchingSpecialWithPoint:(CGPoint)point
{
    // 从所有的特殊的范围中找到点击的那个点
    for (NSArray *selecedArray in self.rectsArray) {
        for (NSDictionary *dic in selecedArray) {
            CGRect myRect = [dic[@"rect"] CGRectValue];
            if(CGRectContainsPoint(myRect, point) ){
                return selecedArray;
            }
        }
    }
    return nil;
}
/** 点击结束的时候 */
- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        for (UIView *subView in self.subviews) {
            if (subView.tag == kCoverViewTag) {
                [subView removeFromSuperview];
            }
        }
    });
}

/**
 *  取消点击的时候,清除相关的阴影
 */
- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    for (UIView *subView in self.subviews) {
        if (subView.tag == kCoverViewTag) {
            [subView removeFromSuperview];
        }
    }
}


- (NSMutableArray *)rectsArray
{
    if (_rectsArray == nil) {
        NSMutableArray *array = [[NSMutableArray alloc] init];
        _rectsArray = array;
    }
    return _rectsArray;
}


@end
