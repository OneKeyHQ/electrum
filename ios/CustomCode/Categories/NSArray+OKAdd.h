//
//  NSArray+OKAdd.h
//  OneKey
//
//  Created by zj on 2021/2/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSArray (OKAdd)
- (NSArray *)ok_map:(id(^)(id obj))mapBlock;
- (NSArray *)ok_filter:(BOOL(^)(id obj))filterBlock;
- (NSArray *)ok_padding:(id)item to:(NSUInteger)idealCount;
@end
