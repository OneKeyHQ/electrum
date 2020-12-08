//
//  OKWalletManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/3.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>
#import "OKWalletInfoModel.h"

#define COIN_BTC             @"BTC"
#define COIN_ETH             @"ETH"


#define kCNY                 @"CNY"  //人民币
#define kUSD                 @"USD"  //美元
#define kJPY                 @"JPY"  //日元
#define kKRW                 @"KRW"  //韩元
#define kGBP                 @"GBP"  //英镑
#define kEUR                 @"EUR"  //欧元
#define kHKD                 @"HKD"  //马来西亚林吉特
#define kMYR                 @"MYR"  //港币
#define kAUD                 @"AUD"  //澳元
#define kINR                 @"INR"  //卢布


#define kCNYSymbol                 @"¥"  //人民币
#define kUSDSymbol                 @"$"  //美元
#define kJPYSymbol                 @"¥"  //日元
#define kKRWSymbol                 @"₩"  //韩元
#define kGBPSymbol                 @"£"  //英镑
#define kEURSymbol                 @"€"  //欧元
#define kHKDSymbol                 @"RM"  //马来西亚林吉特
#define kMYRSymbol                 @"$"  //港币
#define kAUDSymbol                 @"$"  //澳元
#define kINRSymbol                 @"₽"  //卢布




#define kCurrentWalletInfo              @"kCurrentWalletInfo"

#define kCurrentFiat                    @"kCurrentFiat"
#define kCurrentFiatSymbol              @"kCurrentFiatSymbol"
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
    OKWalletTypeMultipleSignature,  //多签钱包
    OKWalletTypeObserve             //观察钱包
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

//创建钱包跳转类型
typedef enum {
    OKWhereToSelectTypeWalletList,
    OKWhereToSelectTypeHDMag
}OKWhereToSelectType;


//删除钱包的跳转类型
typedef enum {
    OKWhereToDeleteTypeDetail,
    OKWhereToDeleteTypeMine
}OKWhereToDeleteType;





@interface OKWalletManager : NSObject

@property (nonatomic,copy)NSString *currentSelectCoinType;
@property (nonatomic,strong)OKWalletInfoModel *currentWalletInfo;

@property (nonatomic,copy)NSString *currentFiat;
@property (nonatomic,copy)NSString *currentFiatSymbol;
@property (nonatomic,copy)NSString *currentBitcoinUnit;
@property (nonatomic,strong)NSArray *supportCoinArray;
@property (nonatomic,strong) NSArray *supportFiatArray;
@property (nonatomic,strong) NSArray *supportFiatsSymbol;
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
- (NSString *)getFeeBaseWithSat:(NSString *)sat;
- (BOOL)checkWalletName:(NSString *)name;
- (BOOL)checkIsHavePwd;
- (OKWalletInfoModel *)getCurrentWalletAddress:(NSString *)wallletName;
@end

NS_ASSUME_NONNULL_END
