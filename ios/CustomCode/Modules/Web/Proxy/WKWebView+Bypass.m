//
//  WKWebView+Bypass.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/27.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "WKWebView+Bypass.h"

@implementation WKWebView (Bypass)
+ (BOOL)handlesURLScheme:(NSString *)urlScheme {
    return NO;
}
@end
