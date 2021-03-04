//
//  OKTransferCompleteController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"

typedef void (^ViewTxDetailsBlock) (void);

NS_ASSUME_NONNULL_BEGIN

@interface OKTransferCompleteController : BaseViewController
+ (instancetype)transferCompleteController:(NSString *)amount block:(ViewTxDetailsBlock)block;
@end

NS_ASSUME_NONNULL_END
