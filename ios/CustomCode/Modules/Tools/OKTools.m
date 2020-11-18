//
//  OKTools.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTools.h"
#import "KeyChainSaveUUID.h"

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
        dispatch_main_async_safe(
            if (msg == nil || ![msg isKindOfClass:[NSString class]] ||msg.length == 0) {
            return;
            }
            UIWindow *window = [[[UIApplication sharedApplication] windows] firstObject];
            if ([window viewWithTag:20201029] != nil) {
            return;
            }
            __block UIView *view = [[UIView alloc] initWithFrame:[UIScreen mainScreen].bounds];
            [view setTag:20201029];
            [view setUserInteractionEnabled:NO];
            [window addSubview:view];
            CSToastStyle *style = [CSToastStyle new];
            style = [[CSToastStyle alloc] initWithDefaultStyle];
            style.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
            [view makeToast:msg duration:2.0 position:CSToastPositionCenter style:style];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [view removeFromSuperview];
            view = nil;
        });)
}

- (NSString *)immutableUUID { // 不可变的UUID
    if (!_immutableUUID) {
        _immutableUUID = [KeyChainSaveUUID getDeviceIDInKeychain];
    }
    return _immutableUUID;
}
- (BOOL)okJumpOpenURL:(NSString *)urlStr {
    NSURL *url = [NSURL URLWithString:urlStr];
    if ([[UIDevice currentDevice] systemVersion].doubleValue < 10.0) {
        if ([[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url];
            return YES;
        } else {
            return NO;
        }
    } else {
        if ([[UIApplication sharedApplication] respondsToSelector:@selector(openURL:options:completionHandler:)]) {
            [[UIApplication sharedApplication] openURL:url options:@{UIApplicationOpenURLOptionsSourceApplicationKey:@YES} completionHandler:nil];
            return YES;
        } else {
            return NO;
        }
    }
}
- (NSString *)getAppVersionString {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}

- (NSString *)getAppDisplayName {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
}

- (NSString *)getAppBundleID {
    return [[NSBundle mainBundle] bundleIdentifier];
}



- (int)findNumFromStr:(NSString *)string
{
    // Intermediate
    NSMutableString *numberString = [[NSMutableString alloc] init];
    NSString *tempStr = @"";
    NSScanner *scanner = [NSScanner scannerWithString:string];
    NSCharacterSet *numbers = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
    
    while (![scanner isAtEnd]) {
        // Throw away characters before the first number.
        [scanner scanUpToCharactersFromSet:numbers intoString:NULL];
        
        // Collect numbers.
        [scanner scanCharactersFromSet:numbers intoString:&tempStr];
        if (tempStr != nil) {
            [numberString appendString:tempStr];
        }
        tempStr = @"";
        break;
    }
    // Result.
    int number = [numberString intValue];
    
    return number;
}


#define USER_APP_PATH                 @"/User/Applications/"
- (BOOL)isJailBreak
{
    if ([[NSFileManager defaultManager] fileExistsAtPath:USER_APP_PATH]) {
        NSArray *applist = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:USER_APP_PATH error:nil];
        return YES;
    }
    return NO;
}

- (BOOL)isNotchScreen {
    if (@available(iOS 11.0, *)) {
        CGFloat height = [[UIApplication sharedApplication] delegate].window.safeAreaInsets.bottom;
        return (height > 0);
    } else {
        return NO;
    }
}


@end
