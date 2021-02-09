//
//  OKVersion.h
//  OneKey
//
//  Created by zj on 2021/2/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKVersion : NSObject
@property (nonatomic, strong, readonly) NSString *versionString; // e.g. @"1.2.3"
@property (nonatomic, strong, readonly) NSArray<NSNumber *> *versionArray;

+ (instancetype)versionWithString:(NSString *)versionString;
+ (instancetype)versionWithArray:(NSArray *)versionArray;

- (BOOL)versionEqualTo:(OKVersion *)version; // Attention! : @"1.2.3" equal to @"1.2.3.0"
- (BOOL)versionGreaterThen:(OKVersion *)version;
- (BOOL)versionLessThen:(OKVersion *)version;

+ (BOOL)versionString:(NSString *)A lessThen:(NSString *)B;
@end

NS_ASSUME_NONNULL_END
