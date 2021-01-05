//
//  OKStorageManager.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKStorageManager.h"

@implementation OKStorageManager
+ (NSString *)getHomeDirectoryPath {
    return NSHomeDirectory();
}
+ (NSString *)getDocumentDirectoryPath {
    return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
}
+ (NSString *)getLibraryDirectoryPath {
    return [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES) lastObject];
}
+ (NSString *)getCacheDirectoryPath {
    return [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
}
+ (NSString *)getTemporaryDirectoryPath {
    return NSTemporaryDirectory();
}
+ (BOOL)saveToUserDefaults:(id)value key:(NSString *)key {
    [[NSUserDefaults standardUserDefaults] setObject:value forKey:key];
    return [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (id)loadFromUserDefaults:(NSString *)key {
    return [[NSUserDefaults standardUserDefaults] objectForKey:key];
}
@end
