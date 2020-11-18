//
//  OKScanView.m
//  OneKey
//
//  Created by xiaoliang on 2020/9/28.
//
#import <Foundation/Foundation.h>

@interface OKQRCodeGenerateManager : NSObject

/// 生成一张普通的二维码
+ (UIImage *)generateWithDefaultQRCodeData:(NSString *)data imageViewWidth:(CGFloat)imageViewWidth;
/// 生成一张带有logo的二维码（logoScaleToSuperView：相对于父视图的缩放比取值范围0-1；0，不显示，1，代表与父视图大小相同）
+ (UIImage *)generateWithLogoQRCodeData:(NSString *)data logoImageName:(NSString *)logoImageName logoScaleToSuperView:(CGFloat)logoScaleToSuperView;
/// 生成一张彩色的二维码
+ (UIImage *)generateWithColorQRCodeData:(NSString *)data backgroundColor:(CIColor *)backgroundColor mainColor:(CIColor *)mainColor;

@end
