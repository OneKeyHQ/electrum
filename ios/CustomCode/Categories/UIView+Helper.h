//
//  UIView+Helper.h
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum :NSInteger {
    LXShadowPathLeft,
    LXShadowPathRight,
    LXShadowPathTop,
    LXShadowPathBottom,
    LXShadowPathNoTop,
    LXShadowPathAllSide
} LXShadowPathSide;
typedef NS_OPTIONS(NSInteger, OKCornerPathSide){
    OKCornerPathTopLeft  =  1 << 0,
    OKCornerPathTopRight =  1 << 1,
    OKCornerPathBottomLeft =  1 << 2,
    OKCornerPathBottomRight =  1 << 3
};

@interface UIView (Helper)

- (void)setLayerRadius:(CGFloat)radius;
- (void)setLayerDefaultRadius;
- (void)setLayerBoarderColor:(UIColor *)color width:(CGFloat)width radius:(CGFloat)radius;
- (void)setLayerBoarderColor:(UIColor *)color width:(CGFloat)width;
- (void)setLayerDefaultRadiusWithBoarderColor:(UIColor *)color;
- (void)setLayerDefaultRadiusAndBoarderColor;
- (void)addBottedlineWidth:(CGFloat)lineWidth lineColor:(UIColor *)lineColor;
- (void)setLayerBorderWithColor:(UIColor *)color andWidth:(CGFloat)width;
- (UIImage *)normalSnapshotImage;
- (UIImage *)openGlSnapshotImage;
- (void)wkwebViewSnapshotImage:(void(^)(UIImage * img))block;
- (UIView *)snapshotView;
- (void)setDashLineWithCGPoint:(CGPoint)point;
- (void)removeDashLine;
- (UIImage *)convertImage;
- (UIImage *)convertImage2;
- (UIImage *)convertImage2WithOptions;
- (void)makeRotationWithAngle:(CGFloat)angle;
- (void)transform3DMakeRotationX:(CGFloat)x y:(CGFloat)y z:(CGFloat)z;
- (void)addBackGroundShadow;
- (void)okLayerBorderShadowSettingDefaultWithCornerRadius:(CGFloat)cornerRadius;
- (void)okLayerBorderSettingDefaultWithCornerRadius:(CGFloat)cornerRadius;
- (void)shadowWithLayerCornerRadius:(CGFloat)cornerRadius borderColor:(UIColor *)borderColor borderWidth:(CGFloat)borderWidth shadowColor:(UIColor *)shadowColor shadowOffset:(CGSize)shadowOffset shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius;
- (void)subviewOutBoundsokLayerBorderShadowSettingDefaultWithCornerRadius:(CGFloat)cornerRadius;
- (void)subviewOutBoundsShadowWithLayerCornerRadius:(CGFloat)cornerRadius borderColor:(UIColor *)borderColor borderWidth:(CGFloat)borderWidth shadowColor:(UIColor *)shadowColor shadowOffset:(CGSize)shadowOffset shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius;

- (void)LX_SetShadowPathWith:(UIColor *)shadowColor shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius shadowSide:(LXShadowPathSide)shadowPathSide shadowPathWidth:(CGFloat)shadowPathWidth;
- (void)setCornerWith:(CGFloat)radius side:(OKCornerPathSide)side withSize:(CGSize)size;

// 加载loading
- (void)loadingWithEnabled:(BOOL)userInteractionEnabled bgAlpha:(CGFloat)bgAlpha;
- (void)hideLoading;

// 视图左右晃动
- (void)horizontalQuake;

- (BOOL)rotateZ;
- (BOOL)rotateY;
- (void)stopAnimation;

- (void)enabled:(BOOL)enabled alpha:(CGFloat)alpha;

@end
