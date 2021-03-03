//
//  OKSendFeeModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/2/25.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface OKSendFeeModel : NSObject
//general
@property (nonatomic,copy)NSString *fee;
@property (nonatomic,copy)NSString *feerate;
@property (nonatomic,copy)NSString *fiat;
@property (nonatomic,copy)NSString *size;
@property (nonatomic,copy)NSString *time;
//ETH
@property (nonatomic,copy)NSString *gas_limit;
@property (nonatomic,copy)NSString *gas_price;
@end

NS_ASSUME_NONNULL_END
