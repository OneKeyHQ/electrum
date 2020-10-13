//
//  UIView+YCExtension.m
//  YCPublicDemo
//
//  Created by zhangxiaoliang on 2017/5/17.
//  Copyright © 2017年 xinhuanwangluo. All rights reserved.
//

#import "UIView+YCExtension.h"

@implementation UIView (YCExtension)
#pragma mark - 设置UIView的X,Y,W,H

-(void)setX:(CGFloat)x
{
    CGRect frame = self.frame;
    frame.origin.x=x;
    self.frame = frame;
    
}


-(void)setY:(CGFloat)y
{
    
    CGRect frame = self.frame;
    frame.origin.y=y;
    self.frame = frame;
}

-(CGFloat)x{
    return self.frame.origin.x;
    
}

-(CGFloat)y
{
    
    return self.frame.origin.y;
}
- (void)setCenterX:(CGFloat)centerX{
    CGPoint point = self.center;
    point.x = centerX;
    self.center = point;
}


- (CGFloat)centerX{
    return self.center.x;
}

- (void)setCenterY:(CGFloat)centerY{
    CGPoint point = self.center;
    point.y = centerY;
    self.center = point;
}


- (CGFloat)centerY{
    return self.center.y;
}

-(void)setWidth:(CGFloat)width
{
    CGRect frame = self.frame;
    frame.size.width = width;
    self.frame = frame;
}

-(void)setHeight:(CGFloat)height
{
    
    CGRect frame = self.frame;
    frame.size.height =height;
    self.frame = frame;
    
    
}
-(CGFloat)width
{
    
    return self.frame.size.width;
}

-(CGFloat)height
{
    return self.frame.size.height;
}

-(void)setSize:(CGSize)size
{
    CGRect frame = self.frame;
    frame.size = size;
    self.frame = frame;
    
}

-(CGSize)size
{
    
    return self.frame.size;
}
-(void)setOrigin:(CGPoint)origin
{
    CGRect frame = self.frame;
    frame.origin = origin;
    self.frame = frame;
    
}

-(CGPoint)origin
{
    return self.frame.origin;
}

- (void)setTop:(CGFloat)top {

    CGRect frame = self.frame;
    frame.origin.y = top;
    self.frame = frame;

}

- (CGFloat)top {
    
    return self.frame.origin.y;
}

- (void)setLeft:(CGFloat)left {

    CGRect frame = self.frame;
    frame.origin.x = left;
    self.frame = frame;

}

- (CGFloat)left {

    return self.frame.origin.x;
}

- (void)setBottom:(CGFloat)bottom {
    
    CGRect frame = self.frame;
    frame.origin.y = bottom =  frame.size.height;
    self.frame = frame;
    
}

- (CGFloat)bottom {

    return self.frame.origin.y + self.frame.size.height;
}

- (void)setRight:(CGFloat)right {

    CGRect frame = self.frame;
    frame.origin.x = right - frame.size.width;
    self.frame = frame;

}

- (CGFloat)right {

    return self.frame.origin.x + self.frame.size.width;
}
@end

