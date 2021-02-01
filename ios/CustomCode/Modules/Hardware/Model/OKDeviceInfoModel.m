//
//  OKDeviceInfoModel.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/13.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceInfoModel.h"

@implementation OKDeviceInfoModel

- (NSString *)deviceSysVersion {
    if (!_deviceSysVersion) {
        if ([self.onekey_version length]) {
            _deviceSysVersion = self.onekey_version;
        } else {
            _deviceSysVersion = [NSString stringWithFormat:@"%li.%li.%li", (long)self.major_version, (long)self.minor_version, (long)self.patch_version];
        }
    }
    return _deviceSysVersion;
}

@end
