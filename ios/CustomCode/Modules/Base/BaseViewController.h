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
@property (nonatomic, strong) UIView *loadingIndicator;
@property (nonatomic, copy) void(^backToPreviousCallback)(void);

+ (__kindof BaseViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier;

+ (__kindof BaseViewController *)initViewControllerWithStoryboardName:(NSString *)name;

// override
- (UIColor *)navBarTintColor;

// 当实现了 <UIScrollViewDelegate> 且 scrollView 向上滚动时显示 navbar 分割线，子类重写时需要调用 super
- (void)scrollViewDidScroll:(UIScrollView *)scrollView NS_REQUIRES_SUPER;

- (void)backButtonWhiteColor;
- (void)hideBackBtn;
- (void)setNavigationBarBackgroundColorWithClearColor;
- (void)backToPrevious;
@end

NS_ASSUME_NONNULL_END
