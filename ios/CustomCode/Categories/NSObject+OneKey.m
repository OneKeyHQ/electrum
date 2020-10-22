//
//  NSObject+OneKey.m
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "NSObject+OneKey.h"

@implementation NSObject (OneKey)

- (UINavigationController *)AT_NavigationController {
    UIViewController *VC = [UIApplication sharedApplication].keyWindow.rootViewController;
    if(![VC isKindOfClass:NSClassFromString(@"MainViewController")]){
        if([VC isKindOfClass:[UINavigationController class]]) {
            return (UINavigationController *)VC;
        }else {
            return VC.navigationController;
        }
    }

    UIViewController *vc = [[[[UIApplication sharedApplication] keyWindow].rootViewController valueForKey:@"tab"] valueForKey:@"selectedViewController"];
    if ([vc isKindOfClass:[UINavigationController class]]) {
        return (UINavigationController *)vc;
    }else if ([vc isKindOfClass:[UIViewController class]]){
        return vc.navigationController;
    }
    return nil;
}

- (UITabBarController *)AT_TabBarController {
    UIViewController *VC = [UIApplication sharedApplication].keyWindow.rootViewController;
    if(![VC isKindOfClass:NSClassFromString(@"MainViewController")]){
        return nil;
    }
    return [[[UIApplication sharedApplication] keyWindow].rootViewController valueForKey:@"tab"];
}

- (UIViewController *)AT_TopViewController {
    UIViewController *resultVC;
    resultVC = [self _topViewController:[[UIApplication sharedApplication].keyWindow rootViewController]];
    while (resultVC.presentedViewController) {
        resultVC = [self _topViewController:resultVC.presentedViewController];
    }
    return resultVC;
}

- (UIViewController *)_topViewController:(UIViewController *)vc {
    if ([vc isKindOfClass:[UINavigationController class]]) {
        return [self _topViewController:[(UINavigationController *)vc topViewController]];
    } else if ([vc isKindOfClass:[UITabBarController class]]) {
        return [self _topViewController:[(UITabBarController *)vc selectedViewController]];
    } else if ([vc isKindOfClass:NSClassFromString(@"MainViewController")]){
        return self.AT_NavigationController.topViewController;
    } else {
        return vc;
    }
    return nil;
}

@end
