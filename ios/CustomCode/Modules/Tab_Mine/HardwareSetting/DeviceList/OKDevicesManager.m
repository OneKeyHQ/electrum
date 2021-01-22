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
    [self reloadDevicesInfoFromLocalStorage];
}

- (void)reloadDevicesInfoFromLocalStorage {
    NSError *error;
    NSFileManager *manager = [NSFileManager defaultManager];

    NSArray *names = [manager contentsOfDirectoryAtPath:self.devicesInfoPath error:&error];
    if (error) {
        NSLog(@"%@", error);
        assert(0);
    }
    
    for (NSString *name in names) {
        NSData *data = [NSData dataWithContentsOfFile:[self.devicesInfoPath stringByAppendingPathComponent:name]];
        id json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:nil];
        
        OKDeviceModel *deviceModel = [[OKDeviceModel alloc] initWithJson:json];
        [self.devices setObject:deviceModel forKey:deviceModel.deviceInfo.device_id];
    }
}

- (void)addDevices:(OKDeviceModel *)deviceModel {
    OKDeviceModel *oldDeviceModel = [self.devices objectForKey:deviceModel.deviceInfo.device_id];
    if (!oldDeviceModel || ![oldDeviceModel.json isEqualToDictionary:deviceModel.json]) {
        [self.devices setObject:deviceModel forKey:deviceModel.deviceInfo.device_id];
        [self saveToLocalStorage:deviceModel];
    }
}

- (void)removeDevice:(NSString *)deviceID {
    if (![self.devices objectForKey:deviceID]) {
        NSLog(@"OKDevicesManager: Device id %@ not found.", deviceID);
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
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:deviceModel.json options:NSJSONWritingPrettyPrinted error:nil];
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
        NSLog(@"OKDevicesManager: Device id %@ removal failed.", deviceID);
    }
}

- (nullable OKDeviceModel *)getDeviceModelWithID:(NSString *)ID {
    return [self.devices objectForKey:ID];
}

@end
