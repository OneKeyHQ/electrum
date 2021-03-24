//
//  OKWalletInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, OKWalletChainType) {
    OKWalletChainTypeBTC = 0,
    OKWalletChainTypeETHLike, // 类以太坊，包括 以太坊/BSC/HECO 等
};

typedef NS_ENUM(NSInteger, OKWalletCoinType) {
    OKWalletCoinTypeUnknown = 0,
    OKWalletCoinTypeBTC,
    OKWalletCoinTypeETH,
    OKWalletCoinTypeBSC,
    OKWalletCoinTypeHECO,
};

// 展示的钱包类型
typedef NS_ENUM(NSInteger, OKWalletType) {
    OKWalletTypeHD,                 //HD钱包
    OKWalletTypeIndependent,        //独立钱包
    OKWalletTypeHardware,           //硬件钱包
    OKWalletTypeMultipleSignature,  //多签钱包
    OKWalletTypeObserve             //观察钱包
};

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletInfoModel : NSObject
@property (nonatomic,copy) NSString* label;
@property (nonatomic,copy) NSString* device_id;
@property (nonatomic,copy) NSString* type;
@property (nonatomic,copy) NSString* addr;
@property (nonatomic,copy) NSString* name;
@property (nonatomic,copy) NSString* coinType;

// additional
@property (nonatomic,copy) NSString* walletKey;
@property (nonatomic,assign) OKWalletChainType chainType;
@property (nonatomic,assign) OKWalletCoinType walletCoinType;
@property (nonatomic,assign) OKWalletType walletType;
@property (nonatomic,copy) NSDictionary *additionalData;

- (NSString *)walletTypeDesc;
+ (OKWalletType)walletTypeWithStr:(NSString *)type;
@end

NS_ASSUME_NONNULL_END
