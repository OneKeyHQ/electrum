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
    if ([self.onekey_version length]) {
        return self.onekey_version;
    }
    return [NSString stringWithFormat:@"%li.%li.%li", (long)self.major_version, (long)self.minor_version, (long)self.patch_version];
}

@end
