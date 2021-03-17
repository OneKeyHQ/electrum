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
        if ([[json objectForKey:@"bootloader_mode"] boolValue]) {
            _bootloaderMode = YES;
        }
        _deviceInfo = [OKDeviceInfoModel mj_objectWithKeyValues:json];
        _json = [json mutableCopy];
    }
    return self;
}

- (void)setVerifiedDevice:(BOOL)verifiedDevice {
    [_json setObject:@(verifiedDevice) forKey:@"verifiedDevice"];
    _deviceInfo.verifiedDevice = verifiedDevice;
}

- (BOOL)verifiedDevice {
    return _deviceInfo.verifiedDevice;
}

- (BOOL)updateWithDict:(NSDictionary *)newDict {
    BOOL changed = NO;
    for (NSString *key in newDict) {
        id newValue = newDict[key];
        id oldValue = [_deviceInfo valueForKey:key];

        if (newValue == oldValue || [newValue isEqual:oldValue]) {
            continue;
        }

        [_deviceInfo setValue:newValue forKey:key];
        [_json setValue:newValue forKey:key];
        changed = YES;
    }
    return changed;
}
@end
