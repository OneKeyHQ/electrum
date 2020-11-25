//
//  UIButton+OneKey.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "UIButton+OneKey.h"

@implementation UIButton (OneKey)
- (void)checkBtnStatus:(BOOL)status
{
    if (!status) {
        self.enabled = NO;
        self.alpha = 0.5;
    }else{
        self.enabled = YES;
        self.alpha = 1.0;
    }
}
@end
