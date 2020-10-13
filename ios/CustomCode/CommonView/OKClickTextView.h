//
//  OKClickTextView.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^clickTextViewPartBlock)(NSString *clickText);

/** 可以设置部分内容的下划线，注意事项，该textView不能滑动并且不能编辑 */

@interface OKClickTextView : UITextView
@property (nonatomic,assign)BOOL isShowUnderline;
/**
 *  设置textView的部分为下划线，并且使之可以点击
 *
 *  @param underlineTextRange 需要下划线的文字范围，如果NSRange范围超出总的内容，将过滤掉
 *  @param color              下划线的颜色，以及下划线上面文字的颜色
 *  @param coverColor         是否有点击的背景，如果设置相关颜色的话，将会有点击效果，如果为nil将没有点击效果
 *  @param block              点击文字的时候的回调
 */
- (void)setUnderlineTextWithRange:(NSRange)underlineTextRange withUnderlineColor:(UIColor *)color withClickCoverColor:(UIColor *)coverColor withBlock:(clickTextViewPartBlock)block;

@end
