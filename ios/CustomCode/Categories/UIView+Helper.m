//
//  UIView+Helper.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "UIView+Helper.h"
#import <WebKit/WebKit.h>

@implementation UIView (Helper)

- (void)setLayerRadius:(CGFloat)radius {
    self.clipsToBounds = YES;
    self.layer.cornerRadius = radius;
}

- (void)setLayerDefaultRadius {
    [self setLayerRadius:20];
}

- (void)setLayerBoarderColor:(UIColor *)color width:(CGFloat)width radius:(CGFloat)radius {
    self.layer.borderColor = color.CGColor;
    self.layer.borderWidth = width;
    self.layer.cornerRadius = radius;
    self.clipsToBounds = YES;
}

- (void)setLayerBoarderColor:(UIColor *)color width:(CGFloat)width {
    self.layer.borderColor = color.CGColor;
    self.layer.borderWidth = width;
}

- (void)setLayerDefaultRadiusWithBoarderColor:(UIColor *)color {
    [self setLayerDefaultRadius];
    self.layer.borderColor = color.CGColor;
    self.layer.borderWidth = 1.f;
}

- (void)setLayerDefaultRadiusAndBoarderColor {
    [self setLayerDefaultRadius];
    self.layer.borderColor = HexColor(0x000000).CGColor;
    self.layer.borderWidth = 1.f;
}
- (void)setLayerBorderWithColor:(UIColor *)color andWidth:(CGFloat)width
{
    self.layer.borderColor = color.CGColor;
    self.layer.borderWidth = width;
}

-(void)addBottedlineWidth:(CGFloat)lineWidth lineColor:(UIColor *)lineColor{
    CAShapeLayer *border = [CAShapeLayer layer];
    
    border.strokeColor = lineColor.CGColor;
    
    border.fillColor = nil;
    
    border.path = [UIBezierPath bezierPathWithRect:self.bounds].CGPath;
    
    border.frame = self.bounds;
    
    border.lineWidth = lineWidth;
    
    border.lineCap = @"square";
    //设置线宽和线间距
    border.lineDashPattern = @[@4, @5];
    
    [self.layer addSublayer:border];
}

- (UIImage *)normalSnapshotImage { // 该API仅可以在未使用layer和OpenGL渲染的视图上使用
    UIGraphicsBeginImageContextWithOptions(self.frame.size, NO, [UIScreen mainScreen].scale);
    [self.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *snapshotImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return snapshotImage;
}

- (UIImage *)openGlSnapshotImage { // 针对有用过OpenGL渲染过的视图截图
    CGSize size = self.bounds.size;
    UIGraphicsBeginImageContextWithOptions(size, NO, [UIScreen mainScreen].scale);
    CGRect rect = self.frame;
    [self drawViewHierarchyInRect:rect afterScreenUpdates:YES];
    UIImage *snapshotImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return snapshotImage;
}

- (void)wkwebViewSnapshotImage:(void(^)(UIImage * img))block {
    UIScrollView *scrollView = ((WKWebView *)self).scrollView;
    CGFloat scale = [UIScreen mainScreen].scale;
    CGSize boundsSize = self.bounds.size;
    CGFloat boundsWidth = boundsSize.width;
    CGFloat boundsHeight = boundsSize.height;
    
    CGSize contentSize = scrollView.contentSize;
    CGFloat contentHeight = contentSize.height;
    
    CGPoint offset = scrollView.contentOffset;
    
    NSMutableArray *images = [NSMutableArray array];
    //创建一个view覆盖在上面
    [self createPlaceholder:images];
    
    
    [scrollView setContentOffset:CGPointMake(0, 0)];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self createImg:images height:contentHeight block:^{
            [scrollView setContentOffset:offset];
            
            CGSize imageSize = CGSizeMake(contentSize.width * scale,
                                          contentSize.height * scale);
            UIGraphicsBeginImageContext(imageSize);
            [images enumerateObjectsUsingBlock:^(UIImage *image, NSUInteger idx, BOOL *stop) {
                [image drawInRect:CGRectMake(0,
                                             scale * boundsHeight * idx,
                                             scale * boundsWidth,
                                             scale * boundsHeight)];
            }];
            UIImage *fullImage = UIGraphicsGetImageFromCurrentImageContext();
            UIGraphicsEndImageContext();
            block(fullImage);
        }];
    });
}

- (void)createImg:(NSMutableArray*)images height:(CGFloat)contentHeight block:(void(^)(void))block{
    UIScrollView *scrollView = ((WKWebView *)self).scrollView;
    UIGraphicsBeginImageContextWithOptions(self.bounds.size, NO, 0.0);
    [self.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    [images addObject:image];
    CGFloat offsetY = scrollView.contentOffset.y;
    [scrollView setContentOffset:CGPointMake(0, offsetY + self.bounds.size.height)];
    contentHeight -= self.bounds.size.height;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if (contentHeight>0) {
            [self createImg:images height:contentHeight block:block];
        }else{
            for (UIView * v in self.superview.subviews) {
                if ([v isKindOfClass:[UIImageView class]]) {
                    UIImageView * imgChild=(UIImageView*)v;
                    if (imgChild.image==images[0]) {
                        [imgChild removeFromSuperview];
                        [images removeObjectAtIndex:0];
                        break;
                    }
                }
            }
            block();
        }
    });
}
- (void)createPlaceholder:(NSMutableArray*)imgs{
    UIGraphicsBeginImageContextWithOptions(self.bounds.size, NO, 0.0);
    [self.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    UIImageView * imgViewPlaceholder=[[UIImageView alloc] initWithImage:image];
    imgViewPlaceholder.tag=100;
    imgViewPlaceholder.frame=self.frame;
    [self.superview addSubview:imgViewPlaceholder];
    [imgs addObject:image];
}

- (UIView *)snapshotView { // 以UIView 的形式返回(_UIReplicantView)
    UIView *snapView = [self snapshotViewAfterScreenUpdates:YES];
    return snapView;
}

- (void)setDashLineWithCGPoint:(CGPoint)point {
    CAShapeLayer *border = [CAShapeLayer layer];
    //虚线的颜色
    border.strokeColor = HexColor(0xDDDDDD).CGColor;
    //填充的颜色
    border.fillColor = [UIColor clearColor].CGColor;
    //设置路径
    border.path = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(point.x, point.y, self.bounds.size.width - point.x*2, self.bounds.size.height - point.y*2) byRoundingCorners:UIRectCornerAllCorners cornerRadii:CGSizeMake(2.5, 2.5)].CGPath;
    //虚线的宽度
    border.lineWidth = 1.f;
    //设置线条的样式
    border.lineCap = @"square";
    //虚线的间隔
    border.lineDashPattern = @[@4, @2];
    [self.layer addSublayer:border];
}

- (void)removeDashLine
{
    for (CALayer *layer in self.layer.sublayers) {
        if ([layer isKindOfClass:[CAShapeLayer class]]) {
            [layer removeFromSuperlayer];
        }
    }
}


- (UIImage *)convertImage {
    UIGraphicsBeginImageContext(self.bounds.size);
    [self drawViewHierarchyInRect:self.bounds afterScreenUpdates:YES];
    UIImage *screenshot = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return screenshot;
}

- (UIImage *)convertImage2 {
    // 创建一个bitmap的context
    // 并把它设置成为当前正在使用的context
    UIGraphicsBeginImageContext(self.bounds.size);
    CGContextRef currnetContext = UIGraphicsGetCurrentContext();
    [self.layer renderInContext:currnetContext];
    // 从当前context中创建一个改变大小后的图片
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();
    return image;
}

- (UIImage *)convertImage2WithOptions { // 图片保真
    // 下面方法，第一个参数表示区域大小。第二个参数表示是否是非透明的。如果需要显示半透明效果，需要传NO，否则传YES。第三个参数就是屏幕密度了
    UIGraphicsBeginImageContextWithOptions(self.bounds.size, NO, 8);
    [self.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage*image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

- (void)makeRotationWithAngle:(CGFloat)angle {
    CGFloat theAngle = fabs(angle);
    if (theAngle == 0) {
        return;
    }
    CGAffineTransform transform = CGAffineTransformMakeRotation(angle * M_PI / 180.0);
    [self setTransform:transform];
}

- (void)transform3DMakeRotationX:(CGFloat)x y:(CGFloat)y z:(CGFloat)z {
    self.layer.transform = CATransform3DMakeRotation(M_PI, x, y, z);
}

- (void)addBackGroundShadow
{
    self.layer.shadowOpacity = 1;
    self.layer.shadowRadius = 10;//大小为6px
    self.layer.shadowColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.05].CGColor;
    self.layer.shadowOffset = CGSizeMake(0,3);
    self.layer.cornerRadius = 5;
}

- (void)okLayerBorderShadowSettingDefaultWithCornerRadius:(CGFloat)cornerRadius { // begin: 3.0.0
    [self shadowWithLayerCornerRadius:cornerRadius borderColor:UIColorFromRGBALPHA(0xF2F2F2, 1) borderWidth:0.5 shadowColor:UIColorFromRGBALPHA(0x000000, 0.05) shadowOffset:CGSizeMake(0, 3) shadowOpacity:1 shadowRadius:10];
}

- (void)okLayerBorderSettingDefaultWithCornerRadius:(CGFloat)cornerRadius { // begin: 3.3.40
    self.clipsToBounds = NO;
    self.layer.cornerRadius = cornerRadius;
    self.layer.borderColor = UIColorFromRGBALPHA(0xF2F2F2, 1).CGColor;
    self.layer.borderColor = [UIColor redColor].CGColor;
    self.layer.borderWidth = 2;
}

- (void)shadowWithLayerCornerRadius:(CGFloat)cornerRadius borderColor:(UIColor *)borderColor borderWidth:(CGFloat)borderWidth shadowColor:(UIColor *)shadowColor shadowOffset:(CGSize)shadowOffset shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius {
    self.clipsToBounds = NO;
    self.layer.cornerRadius = cornerRadius;
    self.layer.borderColor = borderColor.CGColor;
    self.layer.borderWidth = borderWidth;
    self.layer.shadowColor = shadowColor.CGColor;
    self.layer.shadowOffset = shadowOffset;
    self.layer.shadowOpacity = shadowOpacity;
    self.layer.shadowRadius = shadowRadius;
}

- (void)subviewOutBoundsokLayerBorderShadowSettingDefaultWithCornerRadius:(CGFloat)cornerRadius {
    [self subviewOutBoundsShadowWithLayerCornerRadius:cornerRadius borderColor:UIColorFromRGBALPHA(0xF2F2F2, 1) borderWidth:0.5 shadowColor:UIColorFromRGBALPHA(0x000000, 0.05) shadowOffset:CGSizeMake(0, 3) shadowOpacity:1 shadowRadius:10];
}

- (void)subviewOutBoundsShadowWithLayerCornerRadius:(CGFloat)cornerRadius borderColor:(UIColor *)borderColor borderWidth:(CGFloat)borderWidth shadowColor:(UIColor *)shadowColor shadowOffset:(CGSize)shadowOffset shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius {
    @weakify(self);
    dispatch_async(dispatch_get_main_queue(), ^{
        @strongify(self);
        CALayer *layer = [[CALayer alloc] initWithLayer:self.layer];
        layer.bounds = self.bounds;
        layer.position = CGPointMake(self.bounds.size.width/2, self.bounds.size.height/2);
        layer.cornerRadius = cornerRadius;
        layer.borderColor = borderColor.CGColor;
        layer.borderWidth = borderWidth;
        [self.layer insertSublayer:layer atIndex:0];
    });
    self.clipsToBounds = NO;
    self.layer.cornerRadius = cornerRadius;
    self.layer.shadowColor = shadowColor.CGColor;
    self.layer.shadowOffset = shadowOffset;
    self.layer.shadowOpacity = shadowOpacity;
    self.layer.shadowRadius = shadowRadius;
}

/*
 * shadowColor 阴影颜色
 * shadowOpacity 阴影透明度，默认0
 * shadowRadius  阴影半径，默认3
 * shadowPathSide 设置哪一侧的阴影，
 * shadowPathWidth 阴影的宽度，
 */
- (void)LX_SetShadowPathWith:(UIColor *)shadowColor shadowOpacity:(CGFloat)shadowOpacity shadowRadius:(CGFloat)shadowRadius shadowSide:(LXShadowPathSide)shadowPathSide shadowPathWidth:(CGFloat)shadowPathWidth {
    self.layer.masksToBounds = NO;
    self.layer.shadowColor = shadowColor.CGColor;
    self.layer.shadowOpacity = shadowOpacity;
    self.layer.shadowRadius =  shadowRadius;
    self.layer.shadowOffset = CGSizeZero;
    CGRect shadowRect;
    CGFloat originX = 0;
    CGFloat originY = 0;
    CGFloat originW = self.bounds.size.width;
    CGFloat originH = self.bounds.size.height;
    switch (shadowPathSide) {
        case LXShadowPathTop:
            shadowRect  = CGRectMake(originX, originY - shadowPathWidth/2, originW,  shadowPathWidth);
            break;
        case LXShadowPathBottom:
            shadowRect  = CGRectMake(originX, originH -shadowPathWidth/2, originW, shadowPathWidth);
            break;
        case LXShadowPathLeft:
            shadowRect  = CGRectMake(originX - shadowPathWidth/2, originY, shadowPathWidth, originH);
            break;
        case LXShadowPathRight:
            shadowRect  = CGRectMake(originW - shadowPathWidth/2, originY, shadowPathWidth, originH);
            break;
        case LXShadowPathNoTop:
            shadowRect  = CGRectMake(originX -shadowPathWidth/2, originY +1, originW +shadowPathWidth,originH + shadowPathWidth/2 );
            break;
        case LXShadowPathAllSide:
            shadowRect  = CGRectMake(originX - shadowPathWidth/2, originY - shadowPathWidth/2, originW +  shadowPathWidth, originH + shadowPathWidth);
            break;
    }
    UIBezierPath *path =[UIBezierPath bezierPathWithRect:shadowRect];
    self.layer.shadowPath = path.CGPath;
}

- (void)loadingWithEnabled:(BOOL)userInteractionEnabled bgAlpha:(CGFloat)bgAlpha {
    UIActivityIndicatorView *indicatorView = [[UIActivityIndicatorView alloc] initWithFrame:self.bounds];
    indicatorView.tag = 100000001;
    indicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:bgAlpha];
    if (bgAlpha > 0) {
        indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhite;
    } else {
        indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    }
    [indicatorView startAnimating];
    [self addSubview:indicatorView];
    self.userInteractionEnabled = userInteractionEnabled;
}

- (void)hideLoading {
    self.userInteractionEnabled = YES;
    UIActivityIndicatorView *indicatorView = [self viewWithTag:100000001];
    if ([indicatorView isKindOfClass:[UIActivityIndicatorView class]]) {
        [indicatorView stopAnimating];
        [indicatorView removeFromSuperview];
    }
}

- (void)horizontalQuake {
    CGFloat t = 2.0;
    CGAffineTransform leftQuake  = CGAffineTransformTranslate(CGAffineTransformIdentity, t, 0);
    CGAffineTransform rightQuake = CGAffineTransformTranslate(CGAffineTransformIdentity, -t, 0); //水平晃动
    self.transform = leftQuake;  // starting point
    [UIView beginAnimations:@"earthquake" context:(__bridge void *)(self)];
    [UIView setAnimationRepeatAutoreverses:YES]; // 如果不加这一句 整个动画感觉不连贯
    [UIView setAnimationRepeatCount:4];
    [UIView setAnimationDuration:0.07];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(quakeEnded:finished:context:)];
    self.transform = rightQuake; // end here & auto-reverse
    [UIView commitAnimations];
}

- (void)quakeEnded:(NSString *)animationID finished:(NSNumber *)finished context:(void *)context {
    if ([finished boolValue]) {
        UIView *item = (__bridge UIView *)context;
        item.transform = CGAffineTransformIdentity;
    }
}

- (BOOL)rotateZ {
    if (self.layer.animationKeys && self.layer.animationKeys.count) {
        return NO;
    }
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    animation.toValue = [NSNumber numberWithFloat:2 *M_PI];
    animation.duration = 0.4;
    animation.repeatCount = HUGE_VALF;
    [self.layer addAnimation:animation forKey:@"rotation"];
    return YES;
}

- (BOOL)rotateY {
    if (self.layer.animationKeys && self.layer.animationKeys.count) {
        return NO;
    }
    CABasicAnimation *transformAnima = [CABasicAnimation animationWithKeyPath:@"transform.rotation.y"];
    transformAnima.toValue = [NSNumber numberWithFloat: M_PI];
    transformAnima.duration = 0.5;
    transformAnima.cumulative = YES;
    transformAnima.autoreverses = NO;
    transformAnima.repeatCount = HUGE_VALF;
    transformAnima.fillMode = kCAFillModeForwards;
    transformAnima.removedOnCompletion = NO;
    transformAnima.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionLinear];
    self.layer.zPosition = self.layer.frame.size.width/2.f;
    [self.layer addAnimation:transformAnima forKey:@"rotationAnimationY"];
    return YES;
}

- (void)stopAnimation {
    [self.layer removeAllAnimations];
}

- (void)enabled:(BOOL)enabled alpha:(CGFloat)alpha {
    self.userInteractionEnabled = enabled;
    self.alpha = alpha;
    if ([self isMemberOfClass:[UIButton class]]) {
        UIButton *btn = (UIButton *)self;
        btn.enabled = YES;
    }
}
- (void)setCornerWith:(CGFloat)radius side:(OKCornerPathSide)side withSize:(CGSize)size
{
    CGFloat cornerTopLeft = 0;
    CGFloat cornerTopRight = 0;
    CGFloat cornerBottomLeft = 0;
    CGFloat cornerBottomRight = 0;
    if (side & OKCornerPathTopLeft) {
        cornerTopLeft = radius;
    }
    if (side & OKCornerPathTopRight) {
        cornerTopRight = radius;
    }
    if (side & OKCornerPathBottomLeft) {
        cornerBottomLeft = radius;
    }
    if (side & OKCornerPathBottomRight) {
        cornerBottomRight = radius;
    }
    UIBezierPath * path = [UIBezierPath new];
    UIBezierPath * path1 = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0,0, size.width * 0.5, size.height * 0.5) byRoundingCorners:UIRectCornerTopLeft cornerRadii:CGSizeMake(cornerTopLeft, cornerTopLeft)];
    [path appendPath:path1];
    UIBezierPath * path2 = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(size.width * 0.5,0, size.width * 0.5, size.height * 0.5) byRoundingCorners:UIRectCornerTopRight cornerRadii:CGSizeMake(cornerTopRight, cornerTopRight)];
    [path appendPath:path2];
    UIBezierPath * path3 = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0,size.height * 0.5, size.width * 0.5, size.height * 0.5) byRoundingCorners:UIRectCornerBottomLeft cornerRadii:CGSizeMake(cornerBottomLeft, cornerBottomLeft)];
    [path appendPath:path3];
    UIBezierPath * path4 = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(size.width * 0.5, size.height * 0.5, size.width * 0.5, size.height * 0.5) byRoundingCorners:UIRectCornerBottomRight cornerRadii:CGSizeMake(cornerBottomRight, cornerBottomRight)];
    [path appendPath:path4];
    CAShapeLayer * shape = [[CAShapeLayer alloc]init];
    shape.path = path.CGPath;
    self.layer.mask = shape;
}
@end
