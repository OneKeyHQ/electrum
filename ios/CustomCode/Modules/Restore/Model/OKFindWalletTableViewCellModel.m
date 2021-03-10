//
//  OKFindWalletTableViewCellModel.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFindWalletTableViewCellModel.h"

@implementation OKFindWalletTableViewCellModel
- (void)setBlance:(NSString *)blance
{
    if ([blance containsString:@"("]) {
        NSArray *array = [blance componentsSeparatedByString:@"("];
        _blance = [array firstObject];
    }else{
        _blance = blance;
    }
}
@end
