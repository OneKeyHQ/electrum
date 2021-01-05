//
//  OKDeleteWalletConfirmController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

typedef enum {
    OKDeleteTipsTypeWallet,
    OKDeleteTipsTypeAPP
}OKDeleteTipsType;

typedef void (^ConfirmBtnClick)(void);
NS_ASSUME_NONNULL_BEGIN

@interface OKDeleteWalletConfirmController : BaseViewController
+ (instancetype)deleteWalletConfirmController:(ConfirmBtnClick)btnClick type:(OKDeleteTipsType)type;
@end

NS_ASSUME_NONNULL_END
