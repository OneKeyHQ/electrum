//
//  OKHelperUtils.h
//  OneKey
//
//  Created by xuxiwen on 2021/3/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKDefaultFeeInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKHelperUtils : NSObject

+ (void)presentPasswordPage:(UIViewController *)viewController
                   callback:(void(^)(NSString *password))callback;

+ (void)getDefaultFeeInfoCoinType:(NSString *)coinType
                        toAddress:(NSString *)toAddress
                           amount:(NSString *)amount
                             data:(nullable NSString *)data
                  contractAddress:(nullable NSString *)contractAddress
                         callback:(void(^)(OKDefaultFeeInfoModel* __nullable value))callback;


@end

NS_ASSUME_NONNULL_END
