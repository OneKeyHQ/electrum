//
//  OKWalletListTableViewCellModel.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletListTableViewCellModel.h"

@implementation OKWalletListTableViewCellModel
+ (UIColor *)getBackColor:(NSString *)type
{
    NSString *str = [type lowercaseString];
    if ([str hasPrefix:@"btc"]) {
        return [UIColor CG_BTC];
    }else if ([type hasPrefix:@"eth"]){
        return [UIColor CG_ETH];
    }else if ([type hasPrefix:@"bsc"]){
        return [UIColor CG_BSC];
    }else if ([type hasPrefix:@"heco"]){
        return [UIColor CG_HECO];
    }else{
        return HexColor(0x546370);
    }
}

@end
