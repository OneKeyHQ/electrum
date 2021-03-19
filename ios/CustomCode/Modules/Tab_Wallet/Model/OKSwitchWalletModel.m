//
//  OKSwitchWalletModel.m
//  OneKey
//
//  Created by xiaoliang on 2021/3/19.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKSwitchWalletModel.h"
#import "OKTokenAssetModel.h"
@class OKTokenAssetModel;
@implementation OKSwitchWalletModel
+(NSDictionary *)mj_objectClassInArray
{
    return @{@"wallets":[OKTokenAssetModel class]};
}
@end
