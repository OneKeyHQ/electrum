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
    if ([type hasPrefix:@"btc"] || [type hasPrefix:@"BTC"]) {
        return HexColor(0xF7931B);
    }else if ([type hasPrefix:@"eth"] || [type hasPrefix:@"ETH"]){
        return HexColor(0x3E5BF2);
    }else{
        return HexColor(0x546370);
    }
}

+ (NSString *)getBgImageName:(NSString *)type
{
    if ([type hasPrefix:@"btc"] || [type hasPrefix:@"BTC"]) {
        return @"token_trans_btc";
    }else if ([type hasPrefix:@"eth"] || [type hasPrefix:@"ETH"]){
        return @"token_trans_eth";
    }else{
        return nil;
    }
}
@end
