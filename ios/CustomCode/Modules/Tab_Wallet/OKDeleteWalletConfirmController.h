//
//  OKDeleteWalletConfirmController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"
//OKBlock
typedef void (^ConfirmBtnClick)(void);
NS_ASSUME_NONNULL_BEGIN

@interface OKDeleteWalletConfirmController : BaseViewController
+ (instancetype)deleteWalletConfirmController:(ConfirmBtnClick)btnClick;
@end

NS_ASSUME_NONNULL_END
