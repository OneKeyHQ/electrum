//
//  OKHelperUtils.m
//  OneKey
//
//  Created by xuxiwen on 2021/3/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKHelperUtils.h"

@implementation OKHelperUtils

+ (void)presentPasswordPage:(UIViewController *)viewController
                   callback:(void(^)(NSString *password))callback  {
    if (kWalletManager.isOpenAuthBiological) {
        [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
            if (state == YZAuthIDStateNotSupport
                || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                [OKValidationPwdController showValidationPwdPageOn:viewController isDis:YES complete:^(NSString * _Nonnull pwd) {
                }];
            } else if (state == YZAuthIDStateSuccess) {
                NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                callback(pwd);
            }
        }];
    }else{
        [OKValidationPwdController showValidationPwdPageOn:viewController isDis:YES complete:^(NSString * _Nonnull pwd) {
            callback(pwd);
        }];
    }
}

+ (void)getDefaultFeeInfoCoinType:(NSString *)coinType
                        toAddress:(NSString *)toAddress
                           amount:(NSString *)amount
                             data:(nullable NSString *)data
                  contractAddress:(nullable NSString *)contractAddress
                         callback:(void(^)(OKDefaultFeeInfoModel* __nullable value))callback
{
    NSString *coin = [coinType lowercaseString];

    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *paramter = @{@"coin": coin};
        if ([kWalletManager isETHClassification:coin] && toAddress.length > 0 && amount.length > 0) {
            NSMutableDictionary *eth_tx_info = [@{@"to_address":toAddress,@"value":amount} mutableCopy];
            if (data.length) {
                [eth_tx_info addEntriesFromDictionary:@{
                    @"data":data
                }];
            }
            if (contractAddress.length) {
                [eth_tx_info addEntriesFromDictionary:@{
                    @"contract_address":contractAddress
                }];
            }
            paramter = @{@"coin":[coinType lowercaseString],@"eth_tx_info":[eth_tx_info mj_JSONString]};
        }
        NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_default_fee_info parameter:paramter];
        if (dict == nil || dict.count == 0) {
            callback(nil);
            return;
        }
        OKDefaultFeeInfoModel *defaultFeeInfoModel = [OKDefaultFeeInfoModel mj_objectWithKeyValues:dict];
        dispatch_async(dispatch_get_main_queue(), ^{
            callback(defaultFeeInfoModel);
        });
    });

}

@end
