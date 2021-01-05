//
//  BaseViewController.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BaseViewController : UIViewController
@property (nonatomic) BOOL isRoot;
@property (nonatomic) BOOL navigationbarTranslucent;
@property (nonatomic) BOOL forbidInteractivePopGestureRecognizer;

+ (__kindof BaseViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier;

+ (__kindof BaseViewController *)initViewControllerWithStoryboardName:(NSString *)name;

- (void)backButtonWhiteColor;

- (void)hideBackBtn;
- (void)setNavigationBarBackgroundColorWithClearColor;
@end

NS_ASSUME_NONNULL_END
