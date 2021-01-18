//
//  OKHwNotiManager.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/15.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKHwNotiManager.h"
#define kHardwareNotifications      @"HardwareNotifications" //硬件通知
static dispatch_once_t once;
@implementation OKHwNotiManager
+ (OKHwNotiManager *)sharedInstance {
    static OKHwNotiManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKHwNotiManager alloc] init];
        [[NSNotificationCenter defaultCenter]addObserver:_sharedInstance selector:@selector(hwPushNoti:) name:kHardwareNotifications object:nil];
    });
    return _sharedInstance;
}

- (void)hwPushNoti:(NSNotification*)noti
{
    NSLog(@"hwPushNoti");
    if ([self.delegate respondsToSelector:@selector(hwNotiManagerDekegate:type:)]) {
        NSInteger type = [noti.object integerValue];
        [self.delegate hwNotiManagerDekegate:self type:type];
    }
}
@end
