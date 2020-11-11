//
//  NSDictionary+OneKey.h
//  OneKey
//
//  Created by bixin on 2020/10/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSDictionary (OneKey)

#define checkNull(__S__)     \
(__S__) == [NSNull null] || (__S__) == nil ? @"" : [NSString stringWithFormat:@"%@", (__S__)]

#define isNull(__S__) \
(__S__) == [NSNull null] ? nil : __S__

- (NSString *)safeStringForKey:(id)key;
- (void)setSafeValue:(id)value forKey:(NSString *)key;
- (void)setNoNullValue:(id)value forKey:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
