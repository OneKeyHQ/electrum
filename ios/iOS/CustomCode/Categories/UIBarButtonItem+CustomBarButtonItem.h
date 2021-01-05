//
//  UIBarButtonItem+CustomBarButtonItem.h
//  GuiJinShu
//
//  Created by LRF on 16/2/19.
//  Copyright © 2016年 CaiMao. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIBarButtonItem (CustomBarButtonItem) 
// 创建导航栏返回按钮
+ (UIBarButtonItem *)backBarButtonItemWithTarget:(UIViewController *)target selector:(SEL)action;

// 自定义的右上角按钮
+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds size:(CGFloat)size titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action;
+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds font:(UIFont *)font titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action;
+ (UIBarButtonItem *)barButtonItemWithTitle:(NSString *)title bounds:(CGSize)bounds font:(UIFont *)font titleColor:(UIColor *)titleColor backgroundColor:(UIColor *)bgColor borderColor:(UIColor *)borderColor isSetLayer:(BOOL)isSetLayer target:(id)object selector:(SEL)action;

+ (UIBarButtonItem *)barButtonItemScanBtnWithTarget:(id)target selector:(SEL)action;
+ (UIBarButtonItem *)barButtonItemWithImage:(UIImage *)image frame:(CGRect)frame target:(id)target selector:(SEL)action;
+ (UIBarButtonItem *)closeBarButtonItemWithTitleColor:(UIColor *)titleColor target:(id)target selector:(SEL)action;

@end
