//
//  OKAllAssetsCellModel.m
//  OneKey
//
//  Created by zj on 2021/3/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKAllAssetsCellModel.h"

@implementation OKAllAssetsCellModel

@end

@implementation OKAllAssetsSectionModel
- (void)setWallets:(NSArray<OKAllAssetsCellModel *> *)wallets {
    _wallets = [OKAllAssetsCellModel mj_objectArrayWithKeyValuesArray:wallets];
}

- (id)copyWithZone:(nullable NSZone *)zone {
    OKAllAssetsSectionModel *model = [[OKAllAssetsSectionModel alloc] init];
    model.btc = [self.btc copyWithZone:zone];
    model.fiat = [self.fiat copyWithZone:zone];
    model.label = [self.label copyWithZone:zone];
    model.name = [self.name copyWithZone:zone];
    model.wallets = [self.wallets copyWithZone:zone];
    return model;
}
@end
