//
//  KeyChainSaveUUID.h
//  TruckDispatcher
//
//  Created by zhonghua on 2018/8/28.
//  Copyright © 2018年 zhonghua. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Security/Security.h>
@interface KeyChainSaveUUID : NSObject
+ (NSString *)getDeviceIDInKeychain;
+ (void)save:(NSString *)service data:(id)data;
+ (id)load:(NSString *)service;
+ (void)delete_:(NSString *)service;
@end
