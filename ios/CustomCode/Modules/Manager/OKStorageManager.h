//
//  OKStorageManager.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKStorageManager : NSObject
+ (NSString *)getHomeDirectoryPath; // 沙盒主目录路径
+ (NSString *)getDocumentDirectoryPath; // 您应该将所有的应用程序数据文件写入到这个目录下。这个目录用于存储用户数据。该路径可通过配置实现iTunes共享文件。可被iTunes备份
+ (NSString *)getLibraryDirectoryPath; // 含两个子目录Preference和Caches。可创建子文件夹，用来放置您希望被备份但不希望被用户看到的数据。除Caches以外，都会被iTunes备份
+ (NSString *)getCacheDirectoryPath; // Caches 目录：用于存放应用程序专用的支持文件，保存应用程序再次启动过程中需要的信息。
+ (NSString *)getTemporaryDirectoryPath; // tmp 目录：这个目录用于存放临时文件，保存应用程序再次启动过程中不需要的信息。该路径下的文件不会被iTunes备份。
+ (BOOL)saveToUserDefaults:(id)value key:(NSString *)key;
+ (id)loadFromUserDefaults:(NSString *)key;
@end

NS_ASSUME_NONNULL_END
