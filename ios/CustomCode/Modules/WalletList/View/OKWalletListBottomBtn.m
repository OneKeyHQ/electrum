//
//  OKWalletListBottomBtn.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWalletListBottomBtn.h"

@implementation OKWalletListBottomBtn

- (CGRect)titleRectForContentRect:(CGRect)contentRect
{
    return CGRectMake(0,18, contentRect.size.width, contentRect.size.height - 16);
}

- (CGRect)imageRectForContentRect:(CGRect)contentRect
{
    return CGRectMake((contentRect.size.width - 22 ) * 0.5, 0, 22, 22);
}

@end
