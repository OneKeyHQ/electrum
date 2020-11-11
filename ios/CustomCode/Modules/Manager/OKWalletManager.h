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
    OKAddTypeCreateHDDerived,   //创建基于HD的派生钱包
    OKAddTypeCreateSolo,        //创建独立钱包
    OKAddTypeImportSeed,        //导入助记词钱包
    OKAddTypeImportPrivkeys,    //导入私钥钱包
    OKAddTypeImportAddresses,    //导入观察钱包
    OKAddTypeImportKeystore,     //导入Keystore钱包
    OKAddTypeImportXpub,          //导入扩展公钥钱包
    OKAddTypeImport  //总的导入类型
}OKAddType;


@interface OKWalletManager : NSObject
@property (nonatomic,copy)NSString *currentWalletName;  //钱包名称
@property (nonatomic,copy)NSString *currentWalletAddress; //当前钱包地址
@property (nonatomic,copy)NSString *currentWalletType;  //当前钱包类型

@property (nonatomic,copy)NSString *currentSelectCoinType; //当前APP选中的币种类型
@property (nonatomic,copy)NSString *currentFiat;    //当前APP选中Fiat
@property (nonatomic,copy)NSString *currentBitcoinUnit; //当前APP选中的BTC单位
@property (nonatomic,strong)NSArray *supportCoinArray; ////当前APP支持的币种类型
@property (nonatomic,assign)BOOL showAsset;
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
