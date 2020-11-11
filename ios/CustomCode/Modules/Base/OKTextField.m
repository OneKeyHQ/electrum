//
//  OKTextField.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTextField.h"

@implementation OKTextField

- (void)awakeFromNib
{
    [super awakeFromNib];
    self.placeholder = MyLocalizedString(self.placeholder, nil);
}


@end
