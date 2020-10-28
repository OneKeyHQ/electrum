//
//  OKLabel.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKLabel.h"

@implementation OKLabel

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.text = MyLocalizedString(self.text, nil);
}

@end
