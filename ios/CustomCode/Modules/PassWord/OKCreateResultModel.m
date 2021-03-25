//
//  OKCreateResultModel.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKFindWalletTableViewCellModel.h"

@implementation OKCreateResultModel

+(NSDictionary *)mj_objectClassInArray
{
    return @{@"wallet_info":[OKFindWalletTableViewCellModel class],@"derived_info":[OKFindWalletTableViewCellModel class]};
}

@end
