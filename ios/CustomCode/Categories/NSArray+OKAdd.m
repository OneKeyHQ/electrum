//
//  NSArray+OKAdd.m
//  OneKey
//
//  Created by zj on 2021/2/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "NSArray+OKAdd.h"

@implementation NSArray (OKAdd)

- (NSArray *)ok_map:(id(^)(id obj))mapBlock {
    if (!mapBlock) {
        NSAssert(0, @"NSArray+OKAdd.h qypp_map 请传入正确 block");
        return self;
    }

    NSMutableArray *result = [[NSMutableArray alloc] init];
    for (id item in self) {
        [result addObject:mapBlock(item)];
    }
    return [result copy];
}

- (NSArray *)ok_filter:(BOOL(^)(id obj))filterBlock {
    if (!filterBlock) {
        NSAssert(0, @"NSArray+OKAdd.h qypp_filter 请传入正确 block");
        return @[];
    }

    NSMutableArray *result = [[NSMutableArray alloc] init];
    for (id item in self) {
        if (filterBlock(item)) {
            [result addObject:item];
        }
    }
    return [result copy];
}

- (NSArray *)ok_padding:(id)item to:(NSUInteger)idealCount {

    if (self.count >= idealCount || !item) {
        return self;
    }
    NSInteger diff = idealCount - self.count;

    NSMutableArray *mutArr = [self mutableCopy];
    for (int i = 0; i < diff; i++) {
        [mutArr addObject:item];
    }

    return [mutArr copy];
}
@end
