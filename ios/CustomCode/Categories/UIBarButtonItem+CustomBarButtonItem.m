//
//  UIBarButtonItem+CustomBarButtonItem.m
//  GuiJinShu
//
//  Created by LRF on 16/2/19.
//  Copyright © 2016年 CaiMao. All rights reserved.
//

#import "UIBarButtonItem+CustomBarButtonItem.h"

@implementation UIBarButtonItem (CustomBarButtonItem)

// 创建导航栏返回按钮
+ (UIBarButtonItem *)backBarButtonItemWithTitle:(NSString *)title target:(UIViewController *)target selector:(SEL)action {
    UIButton *backButton = [[UIButton alloc] initWithFrame: CGRectMake(0, 0, 30, 30)];
    UIImage *image = [UIImage imageNamed:@"Common_Back"];
    [backButton setImage:image forState:UIControlStateNormal];
    [backButton setImage:image forState:UIControlStateHighlighted];
    [backButton setContentHorizontalAlignment:UIControlContentHorizontalAlignmentLeft];
    [backButton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem =  [[UIBarButtonItem alloc] initWithCustomView:backButton];
    // 解决右滑返回失效问题
    target.navigationController.interactivePopGestureRecognizer.delegate=(id)target;
    return backButtonItem;
}

+ (UIBarButtonItem *)closeBarButtonItemWithTitleColor:(UIColor *)titleColor target:(id)target selector:(SEL)action {
    UIButton *closeBtn = [[UIButton alloc] initWithFrame: CGRectMake(0, 0, 30, 30)];
    [closeBtn setTitle:MyLocalizedString(@"Close", nil) forState:UIControlStateNormal];
    [closeBtn setTitleColor:titleColor?titleColor:HexColor(RGB_PARTING_LINE_BACK) forState:UIControlStateNormal];
    closeBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [closeBtn addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    return [[UIBarButtonItem alloc] initWithCustomView:closeBtn];
}

+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds size:(CGFloat)size titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action {
    UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    rightBtn.frame = CGRectMake(0, 0, bounds.width, bounds.height);
    [rightBtn setTitle:title forState:UIControlStateNormal];
    [rightBtn setTitleColor:titleColor forState:UIControlStateNormal];
    [rightBtn setContentMode:UIViewContentModeRight];
    [rightBtn setBackgroundColor:bgColor];
    [rightBtn.titleLabel setFont:[UIFont systemFontOfSize:size]];
    [rightBtn addTarget:object action:action forControlEvents:UIControlEventTouchUpInside];
    if (isSetLayer) {
        rightBtn.clipsToBounds = YES;
        rightBtn.layer.cornerRadius = 5.f;
        rightBtn.layer.borderColor = HexColor(RGB_PARTING_LINE_BACK).CGColor;
        rightBtn.layer.borderWidth = 1.f;
    } else {
        [rightBtn sizeToFit];
    }
    UIBarButtonItem *rightBBI = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
    return rightBBI;
}

+ (UIBarButtonItem *)barButtonItemScanBtnWithTarget:(id)target selector:(SEL)action {
    return [self barButtonItemWithImage:[UIImage imageNamed:@"Wallet_Scan"] frame:CGRectMake(0, 0, 20, 20) target:target selector:action];
}

+ (UIBarButtonItem *)barButtonItemWithImage:(UIImage *)image frame:(CGRect)frame target:(id)target selector:(SEL)action {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.frame = frame;
    [button setImage:image forState:UIControlStateNormal];
    [button addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *bbi = [[UIBarButtonItem alloc] initWithCustomView:button];
    return bbi;
}

+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds font:(UIFont *)font titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action {
    UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    rightBtn.frame = CGRectMake(0, 0, bounds.width, bounds.height);
    [rightBtn setTitle:title forState:UIControlStateNormal];
    [rightBtn setTitleColor:titleColor forState:UIControlStateNormal];
    [rightBtn setContentMode:UIViewContentModeRight];
    [rightBtn setBackgroundColor:bgColor];
    [rightBtn.titleLabel setFont:font];
    [rightBtn addTarget:object action:action forControlEvents:UIControlEventTouchUpInside];
    if (isSetLayer) {
        rightBtn.clipsToBounds = YES;
        rightBtn.layer.cornerRadius = 5.f;
        rightBtn.layer.borderColor = HexColor(RGB_PARTING_LINE_BACK).CGColor;
        rightBtn.layer.borderWidth = 1.f;
    } else {
        [rightBtn sizeToFit];
    }
    UIBarButtonItem *rightBBI = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
    return rightBBI;
}

+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds font:(UIFont *)font titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor borderColor:(UIColor *)borderColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action {
    UIButton *rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    rightBtn.frame = CGRectMake(0, 0, bounds.width, bounds.height);
    [rightBtn setTitle:title forState:UIControlStateNormal];
    [rightBtn setTitleColor:titleColor forState:UIControlStateNormal];
    [rightBtn setContentMode:UIViewContentModeRight];
    [rightBtn setBackgroundColor:bgColor];
    [rightBtn.titleLabel setFont:font];
    [rightBtn addTarget:object action:action forControlEvents:UIControlEventTouchUpInside];
    if (isSetLayer) {
        rightBtn.clipsToBounds = YES;
        rightBtn.layer.cornerRadius = 5.f;
        rightBtn.layer.borderColor = borderColor.CGColor;
        rightBtn.layer.borderWidth = .5f;
    } else {
        [rightBtn sizeToFit];
    }
    UIBarButtonItem *rightBBI = [[UIBarButtonItem alloc] initWithCustomView:rightBtn];
    return rightBBI;
}
@end
