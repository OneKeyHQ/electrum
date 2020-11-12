//
//  OKWalletManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/3.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

#define COIN_BTC             @"BTC"
#define COIN_ETH             @"ETH"


#define kCurrentWalletName              @"kCurrentWalletName"
#define kCurrentWalletAddress           @"kCurrentWalletAddress"
#define kCurrentWalletType              @"kCurrentWalletType"
#define kCurrentFiat                    @"kCurrentFiat"
#define kCurrentBitcoinUnit             @"kCurrentBitcoinUnit"

#define kSelectedWalletListType         @"kSelectedWalletListType"
#define kShowAssetKey                   @"kShowAssetKey"

#define kIsOpenAuthBiologicalKey        @"kIsOpenAuthBiologicalKey"

#define kWalletManager (OKWalletManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN

//展示的钱包类型
typedef enum {
    OKWalletTypeHD,                 //HD钱包
    OKWalletTypeIndependent,        //独立钱包
    OKWalletTypeHardware,           //硬件钱包
    OKWalletTypeMultipleSignature   //多签钱包
}OKWalletType;


//创建和导入的钱包类型
typedef enum {
    OKAddTypeCreateHDDerived,
    OKAddTypeCreateSolo,
    OKAddTypeImportSeed,
    OKAddTypeImportPrivkeys,
    OKAddTypeImportAddresses,
    OKAddTypeImportKeystore,
    OKAddTypeImportXpub,
    OKAddTypeImport
}OKAddType;


@interface OKWalletManager : NSObject
@property (nonatomic,copy)NSString *currentWalletName;
@property (nonatomic,copy)NSString *currentWalletAddress;
@property (nonatomic,copy)NSString *currentWalletType;

@property (nonatomic,copy)NSString *currentSelectCoinType;
@property (nonatomic,copy)NSString *currentFiat;
@property (nonatomic,copy)NSString *currentBitcoinUnit;
@property (nonatomic,strong)NSArray *supportCoinArray;
@property (nonatomic,assign)BOOL showAsset;
@property (nonatomic,assign)BOOL isOpenAuthBiological;
- (BOOL)showAsset;
- (void)setShowAsset:(BOOL)showAsset;
- (void)setCurrentFiat:(NSString *)currentFiat;
- (NSString *)getWalletTypeShowStr:(NSString *)type;
- (OKWalletType)getWalletDetailType;
+ (OKWalletManager *)sharedInstance;

- (void)clearCurrentWalletInfo;
- (BOOL)checkEveryWordInPlist:(NSArray *)wordsArr;
@end

NS_ASSUME_NONNULL_END
