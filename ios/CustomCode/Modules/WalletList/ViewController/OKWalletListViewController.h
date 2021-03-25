//
//  OKWalletListViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"
typedef void(^OKDismisVcComplete)();
NS_ASSUME_NONNULL_BEGIN

@interface OKWalletListViewController : BaseViewController
+ (instancetype)walletListViewController:(OKDismisVcComplete)block;
@end

NS_ASSUME_NONNULL_END
