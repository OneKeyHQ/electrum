//
//  OKTools.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>
#import "AppDelegate.h"
#define kTools (OKTools.sharedInstance)

#define kAppdelegate ((AppDelegate *)[[UIApplication sharedApplication] delegate])

NS_ASSUME_NONNULL_BEGIN

@interface OKTools : NSObject
@property (copy, nonatomic) NSString *immutableUUID;
+ (OKTools *)sharedInstance;
- (void)showIndicatorView;
- (void)hideIndicatorView;
- (void)pasteboardCopyString:(NSString *)string msg:(NSString *)msg;
- (void)tipMessage:(NSString *)msg;
- (BOOL)okJumpOpenURL:(NSString *)urlStr;
- (NSString *)getAppVersionString;
- (NSString *)getAppDisplayName;
- (NSString *)getAppBundleID;
- (int)findNumFromStr:(NSString *)string;
- (BOOL)isJailBreak;
- (BOOL)isNotchScreen;
- (NSDecimalNumber *)decimalNumberHandlerWithValue:(NSDecimalNumber *)value roundingMode:(NSRoundingMode)mode scale:(NSInteger)scale;

- (void)alertTips:(NSString *)title desc:(NSString *)desc confirm:(void(^)(void))cblock cancel:(void(^)(void))cancel vc:(UIViewController *)vc conLabel:(NSString *)conLabel isOneBtn:(BOOL)oneBtn;
- (BOOL)clearDataWithFilePath:(NSString *)path;
@end

NS_ASSUME_NONNULL_END
