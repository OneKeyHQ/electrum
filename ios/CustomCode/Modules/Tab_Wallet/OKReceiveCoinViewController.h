//
//  OKReceiveCoinViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKReceiveCoinViewController : BaseViewController
+ (instancetype)receiveCoinViewController;
@property (nonatomic,copy)NSString *coinType;
@property (nonatomic,assign)OKWalletType walletType;
@end

NS_ASSUME_NONNULL_END
