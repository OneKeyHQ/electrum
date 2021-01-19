//
//  OKDeviceModel.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceModel.h"

@implementation OKDeviceModel

- (instancetype)initWithJson:(NSDictionary *)json {
    self = [super init];
    if (self) {
        _deviceInfo = [OKDeviceInfoModel mj_objectWithKeyValues:json];
        _json = json;
    }
    return self;
}


@end
