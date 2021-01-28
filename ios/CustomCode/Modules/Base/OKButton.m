//
//  OKButton.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKButton.h"

@implementation OKButton

- (void)status:(OKButtonStatusType)type{
    if (type == OKButtonStatusEnabled) {
        self.userInteractionEnabled = YES;
        self.alpha = 1.0;
    }else if (type == OKButtonStatusDisabled) {
        self.userInteractionEnabled = NO;
        self.alpha = 0.5;
    }
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self setTitle:MyLocalizedString(self.titleLabel.text, nil) forState:UIControlStateNormal];
}

@end
