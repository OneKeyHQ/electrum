//
//  OKDevicesManager.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDevicesManager.h"


@interface OKDevicesManager ()
@property(nonatomic, strong) NSString *devicesInfoPath;
@end

@implementation OKDevicesManager

+ (OKDevicesManager *)sharedInstance {
    static OKDevicesManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[OKDevicesManager alloc] init];
        [instance setupForinit];
    });
    return instance;
}

- (void)setupForinit {
    self.devices = [[NSMutableDictionary alloc] init];
    self.devicesInfoPath = [[OKStorageManager getDocumentDirectoryPath] stringByAppendingPathComponent:@"OK_DEVICES_INFO"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:self.devicesInfoPath]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:self.devicesInfoPath withIntermediateDirectories:YES attributes:nil error:nil];
    } else {
        [self reloadDevicesInfoFromLocalStorage];
    }
}

- (void)reloadDevicesInfoFromLocalStorage {
    NSError *error;
    NSFileManager *manager = [NSFileManager defaultManager];

    NSArray *names = [manager contentsOfDirectoryAtPath:self.devicesInfoPath error:&error];
    if (error) {
        NSLog(@"__ERROR__: reloadDevicesInfoFromLocalStorage %@", error);
        assert(0);
        return;
    }

    for (NSString *name in names) {
        NSData *data = [NSData dataWithContentsOfFile:[self.devicesInfoPath stringByAppendingPathComponent:name]];
        id json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:nil];

        OKDeviceModel *deviceModel = [[OKDeviceModel alloc] initWithJson:json];
        [self.devices setObject:deviceModel forKey:deviceModel.deviceInfo.device_id];
    }
}

- (void)addDevices:(OKDeviceModel *)deviceModel {
    if (deviceModel.bootloaderMode) {
        // Bootloader mode 不写入磁盘
        return;
    }

    if (!deviceModel.deviceInfo.device_id) {
        NSAssert(0, @"OKDeviceModel addDevices error");
        return;
    }
    self.recentDeviceId = deviceModel.deviceInfo.device_id;
    OKDeviceModel *oldDeviceModel = [self.devices objectForKey:deviceModel.deviceInfo.device_id];
    if (!oldDeviceModel) {
        [self.devices setObject:deviceModel forKey:deviceModel.deviceInfo.device_id];
        [self saveToLocalStorage:deviceModel];
    } else {
        [self updateDevices:deviceModel];
    }
}

- (void)updateDevices:(OKDeviceModel *)deviceModel {
    if (!deviceModel.deviceInfo.device_id) {
        NSAssert(0, @"OKDeviceModel updateDevices error");
        return;
    }
    self.recentDeviceId = deviceModel.deviceInfo.device_id;
    OKDeviceModel *oldDeviceModel = [self.devices objectForKey:deviceModel.deviceInfo.device_id];
    if (!oldDeviceModel) { return; }
    [oldDeviceModel updateWithDict:deviceModel.json];
    [self saveToLocalStorage:oldDeviceModel];
}

- (void)removeDevice:(NSString *)deviceID {
    if (![self.devices objectForKey:deviceID]) {
        NSLog(@"__ERROR__: OKDevicesManager: Device id %@ not found.", deviceID);
        return;
    }
    [self.devices removeObjectForKey:deviceID];
    [self removeFromLocalStorage:deviceID];
}

- (void)saveToLocalStorage:(OKDeviceModel *)deviceModel {

    if (![[NSFileManager defaultManager] fileExistsAtPath:self.devicesInfoPath]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:self.devicesInfoPath withIntermediateDirectories:YES attributes:nil error:nil];
    }

    NSString *filePath = [self.devicesInfoPath stringByAppendingPathComponent:deviceModel.deviceInfo.device_id];
    NSError *serializationError;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:deviceModel.json options:NSJSONWritingPrettyPrinted error:&serializationError];
    if (serializationError) {
        NSLog(@"__ERROR__: saveToLocalStorage serializationError: %@", serializationError);
        return;
    }
    [jsonData writeToFile:filePath atomically:YES];
}

- (void)removeFromLocalStorage:(NSString *)deviceID {

    if (![[NSFileManager defaultManager] fileExistsAtPath:self.devicesInfoPath]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:self.devicesInfoPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *filePath = [self.devicesInfoPath stringByAppendingPathComponent:deviceID];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDelete = [fileManager removeItemAtPath:filePath error:nil];
    if (!isDelete) {
        NSLog(@"__ERROR__: OKDevicesManager: Device id %@ removal failed.", deviceID);
    }
}

- (nullable OKDeviceModel *)getDeviceModelWithID:(NSString *)ID {
    return [self.devices objectForKey:ID];
}

@end
