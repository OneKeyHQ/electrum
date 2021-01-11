//
//  OKiCloudManager.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^downloadBlock)(id obj);

@interface OKiCloudManager : NSObject

+ (BOOL)iCloudEnable;

+ (void)downloadWithDocumentURL:(NSURL*)url callBack:(downloadBlock)block;

@end

NS_ASSUME_NONNULL_END
