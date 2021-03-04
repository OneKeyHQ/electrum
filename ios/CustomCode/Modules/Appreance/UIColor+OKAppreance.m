//
//  UIColor+OKAppreance.m
//  OneKey
//
//  Created by zj on 2021/2/24.
//  Copyright © 2021 Onekey. All rights reserved.
//

#define OKAppreanceColor(name,color,alpha)  +(UIColor *)name {return HexColorA(0x##color,alpha);}

#import "UIColor+OKAppreance.h"

@implementation UIColor (OKAppreance)
OKAppreanceColor(BG_W00, FFFFFF, 1)
OKAppreanceColor(BG_W01, F9F9FB, 1)
OKAppreanceColor(BG_W02, F2F2F7, 1)

OKAppreanceColor(FG_B01, 1A1A1A, 1)
OKAppreanceColor(FG_B02, 3C3C43, .6)
OKAppreanceColor(FG_B03, 3C3C43, .3)
OKAppreanceColor(FG_B04, 3C3C43, .2)
OKAppreanceColor(FG_W01, FFFFFF, 1)
OKAppreanceColor(FG_W02, FFFFFF, .6)
OKAppreanceColor(FG_W03, FFFFFF, .3)

OKAppreanceColor(TintBrand, 00B812, 1)
OKAppreanceColor(TintGreen, 34C759, 1)
OKAppreanceColor(TintRed, FF3B30, 1)
OKAppreanceColor(TintYellow, FFCC00, 1)
OKAppreanceColor(TintBlue, 007AFF, 1)
@end
