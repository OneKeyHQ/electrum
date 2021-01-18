//
//  OKChangeMnemonicLenController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"

typedef void(^BtnClickBlock)(OKMnemonicLengthType type);

NS_ASSUME_NONNULL_BEGIN

@interface OKChangeMnemonicLenController : BaseViewController
+ (instancetype)changeMnemonicLenController;
- (void)showOnWindowWithParentViewController:(UIViewController *)viewController block:(BtnClickBlock)block;
@end

NS_ASSUME_NONNULL_END
