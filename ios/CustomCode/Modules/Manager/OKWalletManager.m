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

- (void)setCurrentWalletInfo:(OKWalletInfoModel *)currentWalletInfo
{
    NSArray *typeArray = [currentWalletInfo.type componentsSeparatedByString:@"-"];
    currentWalletInfo.coinType = [typeArray firstObject];
    [OKStorageManager saveToUserDefaults:[currentWalletInfo mj_JSONObject] key:kCurrentWalletInfo];
}
- (OKWalletInfoModel *)currentWalletInfo
{
    NSDictionary *dict = [OKStorageManager loadFromUserDefaults:kCurrentWalletInfo];
    if (dict != nil) {
        return [OKWalletInfoModel mj_objectWithKeyValues:dict];
    }
    return nil;
}

- (NSString *)currentSelectCoinType
{
    return [OKStorageManager loadFromUserDefaults:kSelectedWalletListType];
}
- (void)setCurrentSelectCoinType:(NSString *)currentSelectCoinType
{
    [OKStorageManager saveToUserDefaults:currentSelectCoinType key:kSelectedWalletListType];
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
        _supportCoinArray = @[COIN_BTC,COIN_ETH,COIN_HECO,COIN_BSC];
    }
    return _supportCoinArray;
}

- (NSArray *)ethClassification
{
    if (!_ethClassification) {
        _ethClassification = @[COIN_ETH,COIN_HECO,COIN_BSC];
    }
    return _ethClassification;
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
    if (isOpenAuthBiological == NO) {
        [kOneKeyPwdManager deleteOneKeyPwd];
    }
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
    return self.currentWalletInfo.walletType;

//    NSString *type = self.currentWalletInfo.type;
//    NSString *coinType = self.currentWalletInfo.coinType;
//    if ([type isEqualToString:[NSString stringWithFormat:@"%@-hd-standard",coinType]]) {
//        return OKWalletTypeHD;
//    }else if ([type isEqualToString:[NSString stringWithFormat:@"%@-standard",coinType]]){
//        return OKWalletTypeIndependent;
//    }else if ([type isEqualToString:[NSString stringWithFormat:@"%@-derived-standard",coinType]]){
//        return OKWalletTypeHD;
//    }else if ([type isEqualToString:[NSString stringWithFormat:@"%@-hw-m-n",coinType]]){
//        return OKWalletTypeMultipleSignature;
//    }else if ([type isEqualToString:[NSString stringWithFormat:@"%@-hd-hw-1-1",coinType]]){
//        return OKWalletTypeHardware;
//    }else if([type isEqualToString:[NSString stringWithFormat:@"%@-hw-derived-1-1",coinType]]){
//        return OKWalletTypeHardware;
//    }else if([type isEqualToString:[NSString stringWithFormat:@"%@-watch-standard",coinType]]){
//        return OKWalletTypeObserve;
//    }else if([type isEqualToString:[NSString stringWithFormat:@"%@-private-standard",coinType]]){
//        return OKWalletTypeIndependent;
//    }else{
//        return OKWalletTypeHD;
//    }
}

- (NSString *)getFeeBaseWithSat:(NSString *)sat
{
    if (sat.length == 0 || sat == nil) {
        return @"";
    }

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
    NSRange range = [type rangeOfString:@"-"];
    NSString *walletTypeStr = [type substringFromIndex:range.location + range.length];
    if ([walletTypeStr isEqualToString:@"hd-standard"]) {
        return @"HD".localized;
    }else if ([walletTypeStr isEqualToString:@"standard"]){
        return @"";
    }else if ([walletTypeStr isEqualToString:@"derived-standard"]){
        return @"HD".localized;
    }else if ([walletTypeStr isEqualToString:@"hw-1-1"]){
        return MyLocalizedString(@"hardware", nil);
    }else if ([walletTypeStr isEqualToString:@"hd-hw-1-1"]){
        return MyLocalizedString(@"hardware", nil);
    }else if([walletTypeStr isEqualToString:@"hw-derived-1-1"]){
        return MyLocalizedString(@"hardware", nil);
    }else if ([walletTypeStr isEqualToString:@"watch-standard"]){
        return MyLocalizedString(@"Observation", nil);
    }else{
        return @"";
    }
}

- (void)clearCurrentWalletInfo
{
    OKWalletInfoModel *model = [OKWalletInfoModel new];
    [self setCurrentWalletInfo:model];
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

- (BOOL)isETHClassification:(NSString *)coinType
{
    if (coinType == nil || coinType.length == 0) {
        return NO;
    }
    NSString *coin = [coinType uppercaseString];
    if ([self.ethClassification containsObject:coin]) {
        return YES;
    }else{
        return NO;
    }
}


- (BOOL)checkWalletName:(NSString *)name
{
    if (name == nil || name.length == 0 || name.length > 15) {
        return  NO;
    }
    return YES;
}

- (BOOL)haveHDWallet
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    NSInteger count = 0;
    for (int i = 0;i < listDictArray.count; i++) {
        NSDictionary *dict = listDictArray[i];
        NSString *key = [dict.allKeys firstObject];
        NSDictionary *subDict = dict[key];
        NSString *type = [subDict safeStringForKey:@"type"];
        if ([type containsString:@"hd"]||[type containsString:@"derived-standard"]) {
            count ++;
        }
    }
    if (count == 0) {
        return NO;
    }else{
        return YES;
    }
}


- (BOOL)checkIsHavePwd
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    NSInteger count = 0;
    for (int i = 0;i < listDictArray.count; i++) {
        NSDictionary *dict = listDictArray[i];
        NSString *key = [dict.allKeys firstObject];
        NSDictionary *subDict = dict[key];
        NSString *type = [subDict safeStringForKey:@"type"];
        if ([type containsString:@"watch"]||[type containsString:@"-hw-"]) {
            continue;
        }
        count ++;
    }
    if (count == 0) {
        return NO;
    }else{
        return YES;
    }
}

- (OKWalletInfoModel *)getCurrentWalletAddress:(NSString *)wallletName
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    for (int i = 0;i < listDictArray.count; i++) {
        NSDictionary *dict = listDictArray[i];
        NSString *key = [dict.allKeys firstObject];
        if ([key isEqualToString:wallletName]) {
            NSDictionary *subDict = dict[key];
            if (subDict != nil) {
                return [OKWalletInfoModel mj_objectWithKeyValues:subDict];
            }else{
                return nil;
            }
        }
    }
    return nil;
}


- (NSString *)getUnitForCoinType
{
    NSString *coinType = [kWalletManager.currentWalletInfo.coinType uppercaseString];
    if ([coinType isEqualToString:COIN_BTC]) {
        return kWalletManager.currentBitcoinUnit;
    }else if ([kWalletManager isETHClassification:kWalletManager.currentWalletInfo.coinType]){
        return [kWalletManager.currentWalletInfo.coinType uppercaseString];
    }else{
        return @"";
    }
}

- (NSArray<OKWalletInfoModel *> *)listWallets {
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:nil];
    NSMutableArray *mutArray = [[NSMutableArray alloc] init];
    for (NSDictionary *dict in listDictArray) {
        for (NSString *key in dict) {
            OKWalletInfoModel *model = [OKWalletInfoModel mj_objectWithKeyValues:dict[key]];
            model.walletKey = key;
            [mutArray addObject:model];
        }
    }
    return mutArray;
}

- (NSArray <OKWalletInfoModel *>*)listWalletsChainType:(OKWalletChainType)type {
    return [self.listWallets ok_filter:^BOOL(id obj) {
        return ((OKWalletInfoModel *)obj).chainType == type;
    }];
}

- (NSDictionary *)precisionDict
{
    if (!_precisionDict) {
        _precisionDict = @{@"fiat":@"2",@"btc":@"8",@"eth":@"6",@"token_eth":@"4",@"heco":@"6",@"token_heco":@"4",@"token_bsc":@"4",@"bsc":@"6"};
    }
    return _precisionDict;
}

-  (NSString *)getBrowseAddressUrlTxHash:(NSString *)txHash
{
    NSString *coinType = [kWalletManager.currentWalletInfo.coinType uppercaseString];
    if ([coinType isEqualToString:COIN_BTC]) {
        return [NSString stringWithFormat:@"%@%@",kUserSettingManager.currentBtcBrowser,txHash];;
    }else if ([coinType isEqualToString:COIN_ETH]){
        return [NSString stringWithFormat:@"%@%@",kUserSettingManager.currentEthBrowser,txHash];;
    }else if ([coinType isEqualToString:COIN_HECO]){
        return [NSString stringWithFormat:@"https://hecoinfo.com/tx/%@",txHash];
    }else if ([coinType isEqualToString:COIN_BSC]){
        return [NSString stringWithFormat:@"https://bscscan.com/tx/%@",txHash];
    }else{
        return @"";
    }
}

- (NSDictionary *)unitIsDifferentDict
{
    if (!_unitIsDifferentDict) {
        _unitIsDifferentDict = @{@"heco":@"HT",@"bsc":@"BNB"};
    }
    return _unitIsDifferentDict;
}

- (NSString *)getShowUICoinType:(NSString *)coinType
{
    NSString *coin = coinType;
    if (coinType.length == 0 || coinType == nil) {
        coin = self.currentWalletInfo.coinType;
    }
    NSString *showCoin = [self.unitIsDifferentDict safeStringForKey:coin];
    return showCoin.length>0?[showCoin uppercaseString]:[coinType uppercaseString];
}

/// key
/// @param key    币种类型     如果是token   key = token_coinType   eg: token_eth
- (NSInteger)getPrecision:(NSString *)key
{
    NSString *p = [self.precisionDict safeStringForKey:[key lowercaseString]];
    if (p.length == 0) {
        return 8;
    }
    return [p integerValue];
}

@end
