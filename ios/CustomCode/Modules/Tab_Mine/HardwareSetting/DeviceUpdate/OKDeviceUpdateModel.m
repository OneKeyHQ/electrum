//
//  OKDeviceUpdateModel.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceUpdateModel.h"
#import "NSDictionary+OKKeyPath.h"
#import "OKVersion.h"

@interface OKDeviceUpdateModel ()

@end


@implementation OKDeviceUpdateModel

- (instancetype)initWithDict:(NSDictionary *)json {
    if (self == [super init]) {
        NSDictionary *stm32 = [json ok_objectForKeyPath:@"stm32"];
        NSArray *stm32Ver = [stm32 ok_objectForKeyPath:@"version"];
        _systemFirmwareVersion = [self versionStringFromArray:stm32Ver];
        _systemFirmwareUrl = [stm32 safeStringForKey:@"url"];
        _systemFirmwareChangeLogEN = [stm32 safeStringForKey:@"changelog_en"];
        _systemFirmwareChangeLogCN = [stm32 safeStringForKey:@"changelog_cn"];

        NSDictionary *nrf = [json ok_objectForKeyPath:@"nrf"];
        _bluetoothFirmwareVersion = [nrf safeStringForKey:@"version"];
        _bluetoothFirmwareUrl = [nrf safeStringForKey:@"url"];
        _bluetoothFirmwareChangeLogEN = [nrf safeStringForKey:@"changelog_en"];
        _bluetoothFirmwareChangeLogCN = [nrf safeStringForKey:@"changelog_cn"];
    }
    return self;
}

- (NSArray *)cellModels {
    NSMutableArray *cellModels = [[NSMutableArray alloc] init];

    return cellModels;
}

- (BOOL)bluetoothFirmwareNeedUpdate:(NSString *)currentVer {
    return [self needUpdateWithCurrent:currentVer andLatest:self.bluetoothFirmwareVersion];
}

- (BOOL)systemFirmwareNeedUpdate:(NSString *)currentVer {
    return [self needUpdateWithCurrent:currentVer andLatest:self.systemFirmwareVersion];
}

- (NSString *)versionStringFromArray:(NSArray *)array {
    NSMutableString *version = [[NSMutableString alloc] init];
    for (NSNumber *n in array) {
        [version appendFormat:@"%@.", n];
    }
    if (version.length > 0) {
        [version deleteCharactersInRange:NSMakeRange(version.length - 1, 1)];
    }
    return version;
}

- (BOOL)needUpdateWithCurrent:(NSString *)cur andLatest:(NSString *)latest {
    OKVersion *A = [OKVersion versionWithString:cur];
    OKVersion *B = [OKVersion versionWithString:latest];
    return [B versionGreaterThen:A];
}


@end
