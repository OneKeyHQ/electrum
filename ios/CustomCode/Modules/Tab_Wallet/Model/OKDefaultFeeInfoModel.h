//
//  OKDefaultFeeInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKSendFeeModel.h"

NS_ASSUME_NONNULL_BEGIN
@class OKDefaultFeeInfoSubModel;
@interface OKDefaultFeeInfoModel : NSObject
@property (nonatomic,strong) OKSendFeeModel* slow;
@property (nonatomic,strong) OKSendFeeModel* normal;
@property (nonatomic,strong) OKSendFeeModel* fast;
@property (nonatomic,strong) OKSendFeeModel* slowest;
@end

NS_ASSUME_NONNULL_END
