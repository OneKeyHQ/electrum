//
//  OKWalletManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/3.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletManager.h"

@interface OKWalletManager()
@property (strong, nonatomic) NSArray *englishWords;
@end


@implementation OKWalletManager

static dispatch_once_t once;
+ (OKWalletManager *)sharedInstance {
    static OKWalletManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKWalletManager alloc] init];
    });
    return _sharedInstance;
}

- (NSString *)currentWalletName
{
    return [OKStorageManager loadFromUserDefaults:kCurrentWalletName];
}
- (void)setCurrentWalletName:(NSString *)currentWalletName
{
    [OKStorageManager saveToUserDefaults:currentWalletName key:kCurrentWalletName];
}

- (NSString *)currentWalletAddress
{
    return [OKStorageManager loadFromUserDefaults:kCurrentWalletAddress];
}
- (void)setCurrentWalletAddress:(NSString *)currentWalletAddress
{
    [OKStorageManager saveToUserDefaults:currentWalletAddress key:kCurrentWalletAddress];
}

//设置当前钱包类型
- (NSString *)currentWalletType
{
    return [OKStorageManager loadFromUserDefaults:kCurrentWalletType];
}
- (void)setCurrentWalletType:(NSString *)currentWalletType
{
    [OKStorageManager saveToUserDefaults:currentWalletType key:kCurrentWalletType];
}


- (NSString *)currentSelectCoinType
{
    return [OKStorageManager loadFromUserDefaults:kSelectedWalletListType];
}


- (NSString *)currentFiatSymbol
{
    return [OKStorageManager loadFromUserDefaults:kCurrentFiatSymbol];
}
- (void)setCurrentFiatSymbol:(NSString *)currentFiatSymbol
{
    [OKStorageManager saveToUserDefaults:currentFiatSymbol key:kCurrentFiatSymbol];
}
- (NSString *)currentFiat
{
    return [OKStorageManager loadFromUserDefaults:kCurrentFiat];
}
- (void)setCurrentFiat:(NSString *)currentFiat
{
    [OKStorageManager saveToUserDefaults:currentFiat key:kCurrentFiat];
}

- (NSString *)currentBitcoinUnit
{
    return [OKStorageManager loadFromUserDefaults:kCurrentBitcoinUnit];
}


- (void)setCurrentBitcoinUnit:(NSString *)bitcoinUnit
{
    [OKStorageManager saveToUserDefaults:bitcoinUnit key:kCurrentBitcoinUnit];
}

- (NSArray *)supportCoinArray
{
    if (!_supportCoinArray) {
        _supportCoinArray = @[COIN_BTC];
    }
    return _supportCoinArray;
}

- (BOOL)showAsset
{
    return [[OKStorageManager loadFromUserDefaults:kShowAssetKey] boolValue];
}
- (void)setShowAsset:(BOOL)showAsset
{
    [OKStorageManager saveToUserDefaults:@(showAsset) key:kShowAssetKey];
}

- (BOOL)isOpenAuthBiological
{
    return [[OKStorageManager loadFromUserDefaults:kIsOpenAuthBiologicalKey] boolValue];
}
- (void)setIsOpenAuthBiological:(BOOL)isOpenAuthBiological
{
    [OKStorageManager saveToUserDefaults:@(isOpenAuthBiological) key:kIsOpenAuthBiologicalKey];
}


/**
 OKWalletTypeHD,                 //HD钱包
 OKWalletTypeIndependent,        //独立钱包
 OKWalletTypeHardware,           //硬件钱包
 OKWalletTypeMultipleSignature   //多签钱包
 */
- (OKWalletType)getWalletDetailType
{
    NSString *type = self.currentWalletType;
    if ([type isEqualToString:@"btc-hd-standard"]) {
        return OKWalletTypeHD;
    }else if ([type isEqualToString:@"btc-standard"]){
        return OKWalletTypeIndependent;
    }else if ([type isEqualToString:@"btc-derived-standard"]){
        return OKWalletTypeHD;
    }else if ([type isEqualToString:@"btc-hw-m-n"]){
        return OKWalletTypeMultipleSignature;
    }else if ([type isEqualToString:@"btc-hd-hw-1-1"]){
        return OKWalletTypeHardware;
    }else if([type isEqualToString:@"btc-hw-derived-m-n"]){
        return OKWalletTypeHardware;
    }else{
        return OKWalletTypeHD;
    }
}

- (NSString *)getFeeBaseWithSat:(NSString *)sat
{
    NSString *cuUnit = kWalletManager.currentBitcoinUnit;
    NSDecimalNumber *num = [NSDecimalNumber decimalNumberWithString:sat];
    if ([cuUnit isEqualToString:@"sat"]) {
        return sat;
    }else if ([cuUnit isEqualToString:@"bits"]) {
        return [num decimalNumberByDividingBy:[NSDecimalNumber decimalNumberWithString:@"100"]].stringValue;
    }else if ([cuUnit isEqualToString:@"mBTC"]) {
        return [num decimalNumberByDividingBy:[NSDecimalNumber decimalNumberWithString:@"100000"]].stringValue;
    }else if ([cuUnit isEqualToString:@"BTC"]) {
        return [num decimalNumberByDividingBy:[NSDecimalNumber decimalNumberWithString:@"100000000"]].stringValue;
    }
    return sat;
}

/**
 btc-hd-standard      HD钱包
 btc-standard         标准钱包(区分是否时独立钱包，需要调用has_seed接口)
 btc-derived-standard HD派生钱包
 btc-hw-m-n           通过HD创建的独立钱包
 btc-hd-hw-1-1        通过硬件恢复的HD钱包
 btc-hw-derived-m-n   通过硬件派生的钱包， m/n表示签名人和联署人
 btc-hw-m-n
 */
- (NSString *)getWalletTypeShowStr:(NSString *)type
{
    if ([type isEqualToString:@"btc-hd-standard"]) {
        return @"HD";
    }else if ([type isEqualToString:@"btc-standard"]){
        return @"";
    }else if ([type isEqualToString:@"btc-derived-standard"]){
        return @"HD";
    }else if ([type isEqualToString:@"btc-hw-m-n"]){
        return @"";
    }else if ([type isEqualToString:@"btc-hd-hw-1-1"]){
        return @"硬件恢复的HD钱包";
    }else if([type isEqualToString:@"btc-hw-derived-m-n"]){
        return @"硬件派生";
    }else{
        return @"";
    }
}

- (void)clearCurrentWalletInfo
{
    [self setCurrentWalletType:@""];
    [self setCurrentWalletName:@""];
    [self setCurrentWalletAddress:@""];
}


- (BOOL)checkEveryWordInPlist:(NSArray *)wordsArr {
    int count = 0;
    for (NSString *word in wordsArr) {
        if ([self containsInAllWords:word] == NO) {
            break;
        }else{
            ++count;
        }
    }
    if (count == wordsArr.count) {
        return YES;
    }
    return NO;
}
- (BOOL)containsInAllWords:(NSString *)word {
    if (!word || !word.length) {
        return NO;
    }
    return [self.englishWords containsObject:word];
    return NO;
}

- (NSArray *)englishWords {
    if (!_englishWords) {
        _englishWords =  [kPyCommandsManager callInterface:kInterfaceget_all_mnemonic parameter:@{}];
    }
    return _englishWords;
}
- (NSArray *)supportFiatArray
{
    if (!_supportFiatArray) {
        _supportFiatArray = @[kCNY,kUSD,kJPY,kKRW,kGBP,kEUR,kHKD,kMYR,kAUD,kINR];
    }
    return _supportFiatArray;
}

- (NSArray *)supportFiatsSymbol
{
    if (!_supportFiatsSymbol) {
        _supportFiatsSymbol = @[kCNYSymbol,kUSDSymbol,kJPYSymbol,kKRWSymbol,kGBPSymbol,kEURSymbol,kHKDSymbol,kMYRSymbol,kAUDSymbol,kINRSymbol];
    }
    return _supportFiatsSymbol;
}

- (BOOL)checkWalletName:(NSString *)name
{
    if (name == nil || name.length == 0 || name.length > 15) {
        return  NO;
    }
    return YES;
}

- (BOOL)checkIsHavePwd
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    if (listDictArray.count == 0) {
        return NO;
    }else{
        return YES;
    }
}

@end
