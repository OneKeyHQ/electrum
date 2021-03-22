//
//  OKSwiftHelper.m
//  OneKey
//
//  Created by xuxiwen on 2021/3/15.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKSwiftHelper.h"

@implementation OKSwiftHelper

+ (void)pyCallMethod:(NSString *)method
           parameter:(NSDictionary *)parameter
            callback:(void(^)(id result))callback {
    [kPyCommandsManager asyncCall:method parameter:parameter callback:callback];
}

// dapp_eth_sign_tx
+ (NSString *)dapp_eth_sign_tx {
    return kInterface_dapp_eth_sign_tx;
}

// dapp_eth_send_tx
+ (NSString *)dapp_eth_send_tx {
    return kInterface_dapp_eth_send_tx;
}

+ (NSString *)bluetooth_ios {
    return kBluetooth_iOS;
}

//sign_eth_tx
+ (NSString *)sign_eth_tx {
    return kInterfacesign_eth_tx;
}

// dapp_eth_keccak
+ (NSString *)dapp_eth_keccak {
    return kInterface_dapp_eth_keccak;
}

// sign_message
+ (NSString *)sign_message {
    return kInterfacesign_message;
}

// dapp_eth_rpc_info
+ (NSString *)dapp_eth_rpc_info {
    return kInterface_dapp_eth_rpc_info;
}

@end
