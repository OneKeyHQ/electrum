//
//  OKSwitchWalletModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/3/19.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKSwitchWalletModel : NSObject
@property (nonatomic,copy)NSString *label;
@property (nonatomic,copy)NSString *name;
@property (nonatomic,strong)NSArray *wallets;
@end

NS_ASSUME_NONNULL_END
