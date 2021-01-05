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
        self.enabled = true;
        [self setBackgroundColor:HexColor(0x01BA12)];
    }
    else if (type == OKButtonStatusDisabled) {
        self.enabled = false;
        self.backgroundColor = UIColorFromRGBALPHA(0x01BA12, 0.3);
    }
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self setTitle:MyLocalizedString(self.titleLabel.text, nil) forState:UIControlStateNormal];
}

@end
