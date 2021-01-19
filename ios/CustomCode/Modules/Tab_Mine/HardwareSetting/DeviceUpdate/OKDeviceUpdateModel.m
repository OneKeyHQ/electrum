//
//  OKDeviceUpdateModel.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceUpdateModel.h"
#import "NSDictionary+OKKeyPath.h"

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

- (NSArray <NSNumber *>*)arrayFromVersionString:(NSString *)version {
    NSMutableArray *versionArray = [[NSMutableArray alloc] init];
    NSArray *strArray = [version componentsSeparatedByString:@"."];
    for (NSString *s in strArray) {
        [versionArray addObject: @(s.integerValue)];
    }
    return versionArray;
}

- (BOOL)needUpdateWithCurrent:(NSString *)cur andLatest:(NSString *)latest {
    NSArray <NSNumber *>* v0 = [self arrayFromVersionString:cur];
    NSArray <NSNumber *>* v1 = [self arrayFromVersionString:latest];
    
    if (v0.count != v1.count) {
        assert(0);
    }

    for (int i = 0; i < v0.count; i++) {
        if (v1[i].integerValue > v0[i].integerValue) {
            return YES;
        }
    }
    return NO;
}


@end
