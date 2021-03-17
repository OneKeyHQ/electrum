//
//  OKUserSettingManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kCurrentBtcBrowser              @"kCurrentBtcBrowser"
#define kCurrentEthBrowser              @"kCurrentEthBrowser"
#define kCurrentMarketSource            @"kCurrentMarketSource"

#define KUserPwdType                    @"KUserPwdType"


#define kCurrentSynchronousServer       @"kCurrentSynchronousServer"
#define kSysServerFlag                  @"kSysServerFlag"
#define kRbf                            @"kRbf"
#define kUnconfFlag                     @"kUnconfFlag"
#define kElectrum_default_server        @"kElectrum_default_server"
#define kPinInputMethod                 @"kPinInputMethod"

#define kCurrentProxyDict               @"kCurrentProxyDict"


#define kUserSettingManager (OKUserSettingManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, OKDevicePINInputMethod) {
    OKDevicePINInputMethodOnDevice = 0,
    OKDevicePINInputMethodOnApp,
};

@interface OKUserSettingManager : NSObject
+ (OKUserSettingManager *)sharedInstance;
@property (nonatomic,copy)NSString *currentSynchronousServer;
@property (nonatomic,strong)NSArray *btcBrowserList;
@property (nonatomic,strong)NSArray *ethBrowserList;
@property (nonatomic,copy)NSString *currentBtcBrowser;
@property (nonatomic,copy)NSString *currentEthBrowser;
@property (nonatomic,copy)NSString *currentMarketSource;
@property (nonatomic,copy)NSString *electrum_server;
@property (nonatomic,assign)BOOL rbfFlag;
@property (nonatomic,assign)BOOL unconfFlag;
@property (nonatomic,assign)BOOL sysServerFlag;
@property (nonatomic,copy)NSString *currentProxyDict;
@property (nonatomic,assign)OKDevicePINInputMethod pinInputMethod;


@property (nonatomic,assign)BOOL isLongPwd;

@property (nonatomic,copy)NSString* currentSelectPwdType;

@end

NS_ASSUME_NONNULL_END
