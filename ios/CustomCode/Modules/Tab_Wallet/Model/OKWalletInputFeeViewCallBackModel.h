//
//  OKWalletInputFeeViewCallBackModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/3/8.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletInputFeeViewCallBackModel : NSObject
@property (nonatomic,strong)NSDictionary *customFeeDict;
@property (nonatomic,copy)NSString *fiat;
@property (nonatomic,copy)NSString *feeTfStr;
@property (nonatomic,copy)NSString *sizeTfStr;
@end

NS_ASSUME_NONNULL_END
