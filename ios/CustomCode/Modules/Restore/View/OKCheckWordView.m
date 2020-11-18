//
//  OKCheckWordView.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKCheckWordView.h"

#define kWordNum    3
#define kWordMarginX 15
#define kWordMarginY 20
@interface OKCheckWordView ()
{
    NSMutableArray<UIButton *> *_bs;
}
@property (nonatomic,copy)NSString *currentWord;
@property (nonatomic,weak)id delegate;
@end

@implementation OKCheckWordView

- (instancetype)init {
    if (self = [super init]) {
        [self configSubviews];
    }
    return self;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    [self configSubviews];
}



- (void)configSubviews
{
    _currentWord = @"";
    _bs = [NSMutableArray arrayWithCapacity:kWordNum];
    for (int i = 0; i < kWordNum; i++) {
        UIButton *btn = [[UIButton alloc]init];
        [btn setLayerRadius:20];
        [btn setBackgroundImage:[UIImage createImageWithColor:HexColor(0x00B812) size:CGSizeMake(90, 44)] forState:UIControlStateSelected];
        [btn setBackgroundImage:[UIImage createImageWithColor:HexColor(0xFFFFFF) size:CGSizeMake(90, 44)] forState:UIControlStateNormal];
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
        [btn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        btn.titleLabel.font = [UIFont boldSystemFontOfSize:14];
        btn.titleLabel.textAlignment = NSTextAlignmentCenter;
        btn.tag = i + 100;
        [btn addTarget:self action:@selector(btnClick:) forControlEvents:UIControlEventTouchUpInside];
        [_bs addObject:btn];
        [self addSubview:btn];
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat width = (self.frame.size.width - kWordMarginX * (kWordNum + 1)) / kWordNum;
    int i = 0;
    for (UIButton *b in _bs) {
        CGFloat x = kWordMarginX + (kWordMarginX + width) * i;
        b.frame = CGRectMake(x, kWordMarginY, width, self.size.height - 2 * kWordMarginY);
        ++i;
    }
}

- (void)configureData:(NSArray *)data  delegate:(nonnull id)delegate{
    if (!data || !data.count || data.count != kWordNum) {
        return;
    }
    
    self.delegate = delegate;
    // 顺序赋值
    for (NSInteger i = 0; i < kWordNum; ++i) {
        UIButton *btn = _bs[i];
        [btn setTitle:data[i] forState:UIControlStateNormal];
        [btn setTitle:data[i] forState:UIControlStateSelected];
    }
}

- (void)btnClick:(UIButton *)btn
{
    for (UIButton *b in _bs) {
        if (btn.tag == b.tag) {
            b.selected = YES;
            self.currentWord = [b currentTitle];
            if ([self.delegate respondsToSelector:@selector(checkWordView:delegateBtnClick:)]) {
                [self.delegate checkWordView:self delegateBtnClick:self.currentWord];
            }
        }else{
            b.selected = NO;
        }
    }
}

- (void)clearStatus
{
    for (UIButton *b in _bs) {
        b.selected = NO;
    }
    self.currentWord = @"";
}

@end
