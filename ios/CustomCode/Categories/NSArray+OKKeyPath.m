//
//  NSArray+OKKeyPath.m
//  OneKey
//
//  Created by liuzj on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "NSArray+OKKeyPath.h"
#import "NSDictionary+OKKeyPath.h"

@implementation NSArray (OKKeyPath)

- (id)ok_objectForKeyPath:(NSString *)path {
    if (!path.length) {
        return nil;
    }
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", OKKeyPathQueryPattern];
    if (![pred evaluateWithObject:path]) {
        return nil;
    }
    return [self ok_queryObj:path];;
}

#pragma mark - 私有工具方法
- (id)ok_queryObj:(NSString *)path {
    if (path.length == 0) {
        return self;
    }
    
    if (![path hasPrefix:@"["]) {
        NSString *errorMsg = [NSString stringWithFormat:@"对数组使用非法查询路径: '%@'", path];
        NSAssert(0, errorMsg);
        return nil;
    }
    
    NSInteger index = [self getIndexKey:path];
    if (index >= self.count) {
        return nil;
    }
    id obj = [self objectAtIndex:index];
    if ([obj respondsToSelector:@selector(ok_queryObj:)]) {
        return [obj ok_queryObj:[path substringFromIndex:[path rangeOfString:@"]"].location + 1]];
    }
    return obj;
}

- (NSInteger)getIndexKey:(NSString *)path {
    return [[path substringWithRange:NSMakeRange(1, [path rangeOfString:@"]"].location - 1)] integerValue];
}
@end
