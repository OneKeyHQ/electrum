//
//  OKSwiftHelper.h
//  OneKey
//
//  Created by xuxiwen on 2021/3/15.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKSwiftHelper : NSObject

+ (void)pyCallMethod:(NSString *)method
           parameter:(nullable NSDictionary *)parameter
            callback:(void(^)(__nullable id result))callback;

// "dapp_eth_sign_tx"
+ (NSString *)dapp_eth_sign_tx;

// "dapp_eth_send_tx"
+ (NSString *)dapp_eth_send_tx;

//bluetooth_ios
+ (NSString *)bluetooth_ios;

//sign_eth_tx
+ (NSString *)sign_eth_tx;

// dapp_eth_keccak
+ (NSString *)dapp_eth_keccak;

// sign_message
+ (NSString *)sign_message;

// dapp_eth_rpc_info
+ (NSString *)dapp_eth_rpc_info;

@end

NS_ASSUME_NONNULL_END
