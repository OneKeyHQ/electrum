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
@end
