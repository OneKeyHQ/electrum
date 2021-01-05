//
//  NSDictionary+OneKey.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "NSDictionary+OneKey.h"

@implementation NSDictionary (OneKey)

- (NSString *)safeStringForKey:(id)key {
    return checkNull([self objectForKey:key]);
}

- (void)setSafeValue:(id)value forKey:(NSString *)key {
    if (value == [NSNull null] || value == nil) {
        [self setValue:@"" forKey:key];
    } else {
        [self setValue:value forKey:key];
    }
}

- (void)setNoNullValue:(id)value forKey:(NSString *)key {
    if (value && (value != [NSNull null])) {
        [self setValue:value forKey:key];
    }
}
@end
