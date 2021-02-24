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
@property (nonatomic) BOOL forbidInteractivePopGestureRecognizer;
@property (nonatomic, copy) void(^backToPreviousCallback)(void);

+ (__kindof BaseViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier;

+ (__kindof BaseViewController *)initViewControllerWithStoryboardName:(NSString *)name;

// override
- (UIColor *)navBarTintColor;

- (void)backButtonWhiteColor;
- (void)hideBackBtn;
- (void)setNavigationBarBackgroundColorWithClearColor;
- (void)backToPrevious;
@end

NS_ASSUME_NONNULL_END
