//
//  OKTools.m
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTools.h"

@implementation OKTools
+ (OKTools *)sharedInstance {
    static OKTools *_sharedInstance = nil;
    static dispatch_once_t pred;
    dispatch_once(&pred, ^{
        _sharedInstance = [[OKTools alloc] init];
    });
    return _sharedInstance;
}

- (void)pasteboardCopyString:(NSString *)string msg:(NSString *)msg {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    if (string != nil) {
        pasteboard.string = string;
    }
    if ([msg isKindOfClass:[NSString class]] && msg.length != 0) {
        [kTools tipMessage:msg];
    } else {
        [kTools tipMessage:MyLocalizedString(@"Copied to pasteboard", nil)];
    }
}
- (void)tipMessage:(NSString *)msg {
    NSLog(@"%@",msg); //需要弹框显示
}
@end
