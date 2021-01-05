//
//  OKDefaultFeeInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@class OKDefaultFeeInfoSubModel;
@interface OKDefaultFeeInfoModel : NSObject
@property (nonatomic,strong) NSDictionary* slow;
@property (nonatomic,strong) NSDictionary* normal;
@property (nonatomic,strong) NSDictionary* fast;
@property (nonatomic,strong) NSDictionary* slowest;
@end

NS_ASSUME_NONNULL_END
