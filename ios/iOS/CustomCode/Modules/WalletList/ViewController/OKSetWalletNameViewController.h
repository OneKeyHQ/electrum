//
//  OKSetWalletNameViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKSetWalletNameViewController : BaseViewController
//添加类型
@property (nonatomic,assign)OKAddType addType;
@property (nonatomic,assign)OKWhereToSelectType where;
@property (nonatomic,copy)NSString *coinType;
@property (nonatomic,copy)NSString *privkeys;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *seed;
+ (instancetype)setWalletNameViewController;
@end

NS_ASSUME_NONNULL_END
