//
//  OKMoreBtn.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKMoreBtn.h"

@implementation OKMoreBtn
- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.imageView.contentMode = UIViewContentModeCenter;
        self.titleLabel.textAlignment = NSTextAlignmentRight;
    }
    return self;
}

- (CGRect)titleRectForContentRect:(CGRect)contentRect
{
    return CGRectMake(0, 0, contentRect.size.width * 0.5, contentRect.size.height);
}

- (CGRect)imageRectForContentRect:(CGRect)contentRect
{
    CGFloat H = 16;
    return CGRectMake(contentRect.size.width * 0.5,(contentRect.size.height - H)*0.5, H, H);
}

@end
