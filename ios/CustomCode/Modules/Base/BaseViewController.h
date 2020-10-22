//
//  BaseViewController.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
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

@end

NS_ASSUME_NONNULL_END
