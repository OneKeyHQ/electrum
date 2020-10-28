//
//  OKReceiveCoinViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

typedef enum{
    OKWalletTypePhone = 0, //手机钱包
    OKWalletTypeHardware //硬件钱包
}OKWalletType;

NS_ASSUME_NONNULL_BEGIN

@interface OKReceiveCoinViewController : BaseViewController
+ (instancetype)receiveCoinViewController;
@property (nonatomic,copy)NSString *coinType;
@property (nonatomic,assign)OKWalletType walletType;
@end

NS_ASSUME_NONNULL_END
