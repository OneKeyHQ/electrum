//
//  OKHWRestoreViewController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"

typedef enum {
    OKRestoreRefreshUISearch,
    OKRestoreRefreshUIZeroWallet,
    OKRestoreRefreshUIHaveWallet
}OKRestoreRefreshUI;


NS_ASSUME_NONNULL_BEGIN

@interface OKHWRestoreViewController : BaseViewController
+ (instancetype)restoreViewController;
@end

NS_ASSUME_NONNULL_END
