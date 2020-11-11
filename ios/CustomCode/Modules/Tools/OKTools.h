//
//  OKTools.h
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>
#define kTools (OKTools.sharedInstance)
NS_ASSUME_NONNULL_BEGIN

@interface OKTools : NSObject
@property (copy, nonatomic) NSString *immutableUUID;
+ (OKTools *)sharedInstance;
- (void)pasteboardCopyString:(NSString *)string msg:(NSString *)msg;
- (void)tipMessage:(NSString *)msg;
- (BOOL)okJumpOpenURL:(NSString *)urlStr;
- (NSString *)getAppVersionString;
- (NSString *)getAppDisplayName;
- (NSString *)getAppBundleID;
@end

NS_ASSUME_NONNULL_END
