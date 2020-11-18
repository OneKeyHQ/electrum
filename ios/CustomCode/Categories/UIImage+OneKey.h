//
//  UIImage+OneKey.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (OneKey)
// 创建Image
+ (UIImage *)imageSizeWithScreenImage:(UIImage *)image;
+ (UIImage *)createImageWithColor:(UIColor *)color size:(CGSize)size;
// 改变图片颜色
- (UIImage *)imageWithColor:(UIColor *)color;
/**
 将矩形图片剪切成圆形图片 可设置边框
 */
+ (UIImage *)circleImageWithImage:(UIImage *)sourceImage borderWidth:(CGFloat)borderWidth borderColor:(UIColor *)borderColor;
@end

NS_ASSUME_NONNULL_END
