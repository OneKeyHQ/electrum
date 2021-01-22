//
//  OKDeviceModel.h
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKDeviceInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceModel : NSObject
@property (nonatomic, strong) OKDeviceInfoModel *deviceInfo;
@property (nonatomic, strong) NSDictionary *json;

- (instancetype)initWithJson:(NSDictionary *)json;
@end

NS_ASSUME_NONNULL_END
