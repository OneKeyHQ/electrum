//
//  OKUserSettingManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKUserSettingManager.h"
#import "OKProxyServerModel.h"

@implementation OKUserSettingManager

static dispatch_once_t once;

+ (OKUserSettingManager *)sharedInstance {
    static OKUserSettingManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKUserSettingManager alloc] init];
    });
    return _sharedInstance;
}

- (void)setCurrentBtcBrowser:(NSString *)currentBtcBrowser
{
    [OKStorageManager saveToUserDefaults:currentBtcBrowser key:kCurrentBtcBrowser];
}

- (NSString *)currentBtcBrowser
{
    return [OKStorageManager loadFromUserDefaults:kCurrentBtcBrowser];
}

- (void)setCurrentEthBrowser:(NSString *)currentEthBrowser
{
    [OKStorageManager saveToUserDefaults:currentEthBrowser key:kCurrentEthBrowser];
}

- (NSString *)currentEthBrowser
{
    return [OKStorageManager loadFromUserDefaults:kCurrentEthBrowser];
}


- (void)setCurrentMarketSource:(NSString *)currentMarketSource
{
    [OKStorageManager saveToUserDefaults:currentMarketSource key:kCurrentMarketSource];
}

- (NSString *)currentMarketSource
{
    return [OKStorageManager loadFromUserDefaults:kCurrentMarketSource];
}


- (void)setIsLongPwd:(BOOL)isLongPwd
{
    [OKStorageManager saveToUserDefaults:@(isLongPwd) key:KUserPwdType];
    self.currentSelectPwdType = @"";
}

-(BOOL)isLongPwd
{
    return [[OKStorageManager loadFromUserDefaults:KUserPwdType] boolValue];
}

- (void)setCurrentSynchronousServer:(NSString *)currentSynchronousServer
{
    [OKStorageManager saveToUserDefaults:currentSynchronousServer key:kCurrentSynchronousServer];
}


- (NSString *)currentSynchronousServer
{
    NSString *synchronousServer = [OKStorageManager loadFromUserDefaults:kCurrentSynchronousServer];
    if (synchronousServer.length == 0 || synchronousServer == nil) {
        synchronousServer =  [kPyCommandsManager callInterface:kInterfaceget_sync_server_host parameter:@{}];
    }
    return synchronousServer;
}

- (void)setSysServerFlag:(BOOL)sysServerFlag
{
    [OKStorageManager saveToUserDefaults:@(sysServerFlag) key:kSysServerFlag];

}
- (BOOL)sysServerFlag
{
    return [[OKStorageManager loadFromUserDefaults:kSysServerFlag] boolValue];
}

- (void)setRbfFlag:(BOOL)rbfFlag
{
    [OKStorageManager saveToUserDefaults:@(rbfFlag) key:kRbf];
}
- (BOOL)rbfFlag
{
    return [[OKStorageManager loadFromUserDefaults:kRbf] boolValue];
}

- (void)setUnconfFlag:(BOOL)unconfFlag
{
    [OKStorageManager saveToUserDefaults:@(unconfFlag) key:kUnconfFlag];
}

- (BOOL)unconfFlag
{
    return [[OKStorageManager loadFromUserDefaults:kUnconfFlag] boolValue];
}

- (void)setCurrentProxyDict:(NSString *)currentProxyDict
{
    [OKStorageManager saveToUserDefaults:currentProxyDict key:kCurrentProxyDict];
}
- (NSString *)currentProxyDict
{
    return [OKStorageManager loadFromUserDefaults:kCurrentProxyDict];
}


- (NSArray *)btcBrowserList
{
    if (!_btcBrowserList) {
        _btcBrowserList = [NSArray arrayWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"BTCBrowser" ofType:@"plist"]];
    }
    return _btcBrowserList;
}

- (NSArray *)ethBrowserList
{
    if (!_ethBrowserList) {
        _ethBrowserList = [NSArray arrayWithContentsOfFile:[[NSBundle mainBundle]pathForResource:@"ETHBrowser" ofType:@"plist"]];
    }
    return _ethBrowserList;
}


- (void)setPinInputMethod:(OKDevicePINInputMethod)pinInputMethod {
    [OKStorageManager saveToUserDefaults:@(pinInputMethod) key:kPinInputMethod];
}

- (OKDevicePINInputMethod)pinInputMethod {
    return OKDevicePINInputMethodOnApp;
}



- (void)setDefaultSetings
{
    //设置默认法币
    if (kWalletManager.currentFiat == nil || kWalletManager.currentFiat.length == 0) {
        [kWalletManager setCurrentFiat:@"CNY"];
        [kWalletManager setCurrentFiatSymbol:kWalletManager.supportFiatsSymbol[0]];
        [kPyCommandsManager callInterface:kInterfaceSet_currency parameter:@{@"ccy":@"CNY"}];
    }else{
        [kPyCommandsManager callInterface:kInterfaceSet_currency parameter:@{@"ccy":kWalletManager.currentFiat}];
    }

    //设置默认BTC单位
    if (kWalletManager.currentBitcoinUnit == nil || kWalletManager.currentBitcoinUnit.length == 0) {
        [kWalletManager setCurrentBitcoinUnit:@"BTC"];
        [kPyCommandsManager callInterface:kInterfaceSet_base_uint parameter:@{@"base_unit":@"BTC"}];
    }else{
        [kPyCommandsManager callInterface:kInterfaceSet_base_uint parameter:@{@"base_unit":kWalletManager.currentBitcoinUnit}];
    }


    //设置默认的BTC浏览器
    if (kUserSettingManager.currentBtcBrowser == nil || kUserSettingManager.currentBtcBrowser.length == 0) {
        [kUserSettingManager setCurrentBtcBrowser:kUserSettingManager.btcBrowserList.firstObject];
    }

    //设置默认的ETH浏览器
    if (kUserSettingManager.currentEthBrowser == nil || kUserSettingManager.currentEthBrowser.length == 0) {
        [kUserSettingManager setCurrentEthBrowser:kUserSettingManager.ethBrowserList.firstObject];
    }

    [kPyCommandsManager callInterface:kInterfaceset_rbf parameter:@{@"status_rbf":@"1"}];
    [kPyCommandsManager callInterface:kInterfaceset_unconf parameter:@{@"x":@"1"}];
    [kUserSettingManager setUnconfFlag:YES];
    [kUserSettingManager setRbfFlag:YES];


    if (kUserSettingManager.currentMarketSource == nil || kUserSettingManager.currentMarketSource.length == 0) {
        NSArray *marketSource =  [kPyCommandsManager callInterface:kInterfaceget_exchanges parameter:@{}];
        NSString *first =  marketSource.firstObject;
        [kUserSettingManager setCurrentMarketSource:first];
        [kPyCommandsManager callInterface:kInterfaceset_exchange parameter:@{@"exchange":first}];
    }

    if (kUserSettingManager.electrum_server == nil || kUserSettingManager.electrum_server.length == 0) {
       NSDictionary *dict =   [kPyCommandsManager callInterface:kInterfaceget_default_server parameter:@{}];
        if (dict != nil) {
            NSString *electrumNode = [NSString stringWithFormat:@"%@:%@",[dict safeStringForKey:@"host"],[dict safeStringForKey:@"port"]];
            [kUserSettingManager setElectrum_server:electrumNode];
        }
    }
}


@end
