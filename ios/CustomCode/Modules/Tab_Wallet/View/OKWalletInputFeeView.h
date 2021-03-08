//
//  OKWalletInputFeeView.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//
#import <UIKit/UIKit.h>
#import "OKWalletInputFeeViewCallBackModel.h"

@class OKDefaultFeeInfoModel;
typedef void(^CancelBlock)(void);
typedef void(^SureBlock)(OKWalletInputFeeViewCallBackModel *callBackModel);

@interface OKWalletInputFeeView : UIView
@property (nonatomic, copy) CancelBlock cancelBlock;
@property (nonatomic, copy) SureBlock sureBlock;

+ (void)showWalletCustomFeeModel:(OKDefaultFeeInfoModel *)model feetype:(OKFeeType)feetype coinType:(NSString *)coinType sure:(SureBlock)sureBlock  Cancel:(CancelBlock)cancelBlock;

@end
