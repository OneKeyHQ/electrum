//
//  OKChangeWalletController.h
//  OneKey
//
//  Created by zj on 2021/3/10.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKChangeWalletController : UIViewController
@property (nonatomic, assign)OKWalletChainType chianType;
@property (nonatomic, copy) void(^walletChangedCallback)(OKWalletInfoModel *wallet);
@property (nonatomic, copy) void(^cancelCallback)(BOOL selected);

+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
