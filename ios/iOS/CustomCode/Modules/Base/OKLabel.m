//
//  OKLabel.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKLabel.h"

@implementation OKLabel

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.text = MyLocalizedString(self.text, nil);
}

@end
