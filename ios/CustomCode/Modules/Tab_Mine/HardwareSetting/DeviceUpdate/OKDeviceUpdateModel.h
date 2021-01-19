//
//  OKDeviceUpdateModel.h
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceUpdateModel : NSObject
@property (nonatomic, copy) NSString* systemFirmwareVersion;
@property (nonatomic, copy) NSString* systemFirmwareUrl;
@property (nonatomic, copy) NSString* systemFirmwareChangeLog;
@property (nonatomic, copy) NSString* bluetoothFirmwareVersion;
@property (nonatomic, copy) NSString* bluetoothFirmwareUrl;
@property (nonatomic, copy) NSString* bluetoothFirmwareChangeLogCN;
@property (nonatomic, copy) NSString* bluetoothFirmwareChangeLogEN;
@property (nonatomic, copy) NSString* systemFirmwareChangeLogCN;
@property (nonatomic, copy) NSString* systemFirmwareChangeLogEN;

- (instancetype)initWithDict:(NSDictionary *)json;
- (BOOL)bluetoothFirmwareNeedUpdate:(NSString *)currentVer;
- (BOOL)systemFirmwareNeedUpdate:(NSString *)currentVer;
@end

NS_ASSUME_NONNULL_END
