//
//  OKChangeWalletController.h
//  OneKey
//
//  Created by zj on 2021/3/10.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_OPTIONS(NSUInteger, OKChangeWalletChainType) {
    OKChangeWalletChainTypeBTC  = 1 << 0,
    OKChangeWalletChainTypeETH  = 1 << 1,
    OKChangeWalletChainTypeBSC  = 1 << 2,
    OKChangeWalletChainTypeHECO = 1 << 3,
    OKChangeWalletChainTypeETHLike = OKChangeWalletChainTypeETH | OKChangeWalletChainTypeBSC | OKChangeWalletChainTypeHECO,
    OKChangeWalletChainTypeALL = OKChangeWalletChainTypeBTC | OKChangeWalletChainTypeETHLike
};

NS_ASSUME_NONNULL_BEGIN

@interface OKChangeWalletController : BaseViewController
@property (nonatomic, assign)OKChangeWalletChainType chianType;
@property (nonatomic, copy) void(^walletChangedCallback)(OKWalletInfoModel *wallet);
@property (nonatomic, copy) void(^cancelCallback)(BOOL selected);

+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
