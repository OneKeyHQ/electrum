//
//  OKWalletInfoModel.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKWalletInfoModel.h"

@implementation OKWalletInfoModel

- (void)setType:(NSString *)type {
    _type = type;
    if ([type ignoreCaseCointain:@"eth"]) {
        self.chainType = OKWalletChainTypeETHLike;
    } else {
        self.chainType = OKWalletChainTypeBTC;
    }

    OKWalletType walletType = OKWalletTypeHD;
    #define OK_WALLET_TYPE_CASE(argTypeStr,argType) \
        else if([type ignoreCaseCointain:(argTypeStr)]) {walletType = (argType);}
    if ([type ignoreCaseCointain:@"hd-standard"]) {
        walletType = OKWalletTypeHD;
    }
    OK_WALLET_TYPE_CASE(@"derived-standard",    OKWalletTypeHD)
    OK_WALLET_TYPE_CASE(@"private-standard",    OKWalletTypeIndependent)
    OK_WALLET_TYPE_CASE(@"watch-standard",      OKWalletTypeObserve)
    OK_WALLET_TYPE_CASE(@"hw-derived-1-1",      OKWalletTypeHardware)
    OK_WALLET_TYPE_CASE(@"hd-hw-1-1",           OKWalletTypeHardware)
    OK_WALLET_TYPE_CASE(@"hw-m-n",              OKWalletTypeMultipleSignature)
    OK_WALLET_TYPE_CASE(@"standard",            OKWalletTypeIndependent)
    self.walletType = walletType;
}

- (NSString *)walletTypeDesc {
    if (!_walletTypeDesc) {
        switch (self.walletType) {
            case OKWalletTypeHD: _walletTypeDesc = @"HD".localized; break;
            case OKWalletTypeHardware: _walletTypeDesc = @"hardware".localized; break;
            case OKWalletTypeObserve: _walletTypeDesc = @"Observation".localized; break;
            default: _walletTypeDesc = @""; break;
        }
    }
    return _walletTypeDesc;
}

- (OKWalletCoinType)walletCoinType {
    NSString *coinType = self.coinType;
    OKWalletCoinType type = OKWalletCoinTypeUnknown;
    if ([coinType ignoreCaseCointain:@"btc"]) {
        type = OKWalletCoinTypeBTC;
    } else if ([coinType ignoreCaseCointain:@"eth"]) {
        type = OKWalletCoinTypeETH;
    }
    return type;
}

@end
