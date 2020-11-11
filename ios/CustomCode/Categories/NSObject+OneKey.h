//
//  NSObject+OneKey.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSObject (OneKey)
- (UINavigationController *)OK_NavigationController;
- (UITabBarController *)OK_TabBarController;
- (UIViewController*)OK_TopViewController;
@end

NS_ASSUME_NONNULL_END
