//
//  OKAppAuthorityManager.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import <Foundation/Foundation.h>

#define kAuthorityManager (OKAppAuthorityManager.sharedInstance)

NS_ASSUME_NONNULL_BEGIN

@interface OKAppAuthorityManager : NSObject

+ (OKAppAuthorityManager *)sharedInstance;
+ (void)clear;

+ (BOOL)canUseCamera;
+ (BOOL)canReadPhotos;

@end

NS_ASSUME_NONNULL_END
