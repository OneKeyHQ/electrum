//
//  NSDictionary+OKKeyPath.m
//  OneKey
//
//  Created by liuzj on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "NSDictionary+OKKeyPath.h"
#import "NSArray+OKKeyPath.h"

const NSString *OKKeyPathQueryPattern = @"^((\\[(\\d+|'(\\w|\\s)+')\\])|\\.\\w+)+$";

@implementation NSDictionary (OKKeyPath)

- (id)ok_objectForKeyPath:(NSString *)path {
    if (!path.length) {
        return nil;
    }
    
    if (![path hasPrefix:@"."] && ![path hasPrefix:@"["]) {
        path = [NSString stringWithFormat:@".%@", path];
    }
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", OKKeyPathQueryPattern];
    if (![pred evaluateWithObject:path]) {
        return nil;
    }
    return [self ok_queryObj:path];
}

#pragma mark - 私有工具方法
- (id)ok_queryObj:(NSString *)path {
    if (path.length == 0) {
        return self;
    }
    NSInteger offset = [path hasPrefix:@"."] ? 1 : 4;
    NSString *key = [self getStringKey:path];
    id obj = [self objectForKey:key];
    if ([obj respondsToSelector:@selector(ok_queryObj:)]) {
        return [obj ok_queryObj:[path substringFromIndex:key.length + offset]];
    }
    
    return obj;
}

- (NSString *)getStringKey:(NSString *)path {
    if ([path hasPrefix:@"."]) {
        path = [path substringFromIndex:1];
        NSInteger dotIndex = [path rangeOfString:@"."].location;
        NSInteger bracketIndex = [path rangeOfString:@"["].location;
        NSInteger index = dotIndex < bracketIndex ? dotIndex : bracketIndex;
        if (index == NSNotFound) {
            return path;
        }
        return [path substringToIndex:index];
    }
    return [path substringWithRange:NSMakeRange(2, [path rangeOfString:@"']"].location - 2)];
}
@end
