//
//  OKSetWalletNameViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKSetWalletNameViewController : BaseViewController
//添加类型
@property (nonatomic,assign)OKAddType addType;
@property (nonatomic,copy)NSString *coinType;
@property (nonatomic,copy)NSString *privkeys;
@property (nonatomic,copy)NSString *address;
+ (instancetype)setWalletNameViewController;
@end

NS_ASSUME_NONNULL_END
