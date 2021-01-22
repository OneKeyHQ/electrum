//
//  NSArray+OKKeyPath.h
//  OneKey
//
//  Created by liuzj on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NSArray (OKKeyPath)
- (id)ok_objectForKeyPath:(NSString *)keyPath;

@end

