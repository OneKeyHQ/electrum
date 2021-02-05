//
//  OKDevicesManager.h
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKDeviceModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDevicesManager : NSObject
@property(nonatomic, strong) NSMutableDictionary <NSString *, OKDeviceModel *>* devices;
@property(nonatomic, copy) NSString *recentDeviceId;

+ (OKDevicesManager *)sharedInstance;

- (void)addDevices:(OKDeviceModel *)device;
- (void)updateDevices:(OKDeviceModel *)device;
- (void)removeDevice:(NSString *)deviceID;
- (nullable OKDeviceModel *)getDeviceModelWithID:(NSString *)ID;
@end

NS_ASSUME_NONNULL_END
