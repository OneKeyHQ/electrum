//
//  OKWalletInputFeeView.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//
#import <UIKit/UIKit.h>

typedef void(^CancelBlock)(void);
typedef void(^SureBlock)(NSString *fee);

@interface OKWalletInputFeeView : UIView
@property (nonatomic, copy) CancelBlock cancelBlock;
@property (nonatomic, copy) SureBlock sureBlock;
+ (void)showWalletCustomFeeInputSize:(NSString *)size sure:(SureBlock)sureBlock Cancel:(CancelBlock)cancelBlock;

@end
