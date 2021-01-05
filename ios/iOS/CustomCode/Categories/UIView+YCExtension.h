//
//  UIView+YCExtension.h
//  YCPublicDemo
//
//  Created by zhangxiaoliang on 2017/5/17.
//  Copyright © 2017年 xinhuanwangluo. All rights reserved.
//

#import <UIKit/UIKit.h>
#pragma mark - UIView坐标分类
@interface UIView (YCExtension)

/**
 *  x值
 */
@property (nonatomic,assign) CGFloat x;

/**
 *  y值
 */
@property (nonatomic,assign) CGFloat y;

/**
 *  中点x
 */
@property (nonatomic, assign) CGFloat centerX;

/**
 *  中点Y
 */
@property (nonatomic, assign) CGFloat centerY;
/**
 *  宽度
 */
@property (nonatomic,assign) CGFloat width;
/**
 *  高度
 */
@property (nonatomic,assign) CGFloat height;
/**
 *  尺寸大小
 */
@property (nonatomic,assign) CGSize size;

/**
 *  origin 值
 */
@property (nonatomic,assign) CGPoint origin;


/**
 top
 */
@property (nonatomic, assign) CGFloat top;

/**
 left
 */
@property (nonatomic, assign) CGFloat left;

/**
 right
 */
@property (nonatomic, assign) CGFloat right;

/**
 bottom
 */
@property (nonatomic, assign) CGFloat bottom;

@end



