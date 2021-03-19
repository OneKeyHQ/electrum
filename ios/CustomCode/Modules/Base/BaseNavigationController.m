//
//  BaseNavigationController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseNavigationController.h"

@interface BaseNavigationController ()

@end

@implementation BaseNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.navigationBar setTitleTextAttributes:@{NSFontAttributeName:[UIFont boldSystemFontOfSize:17], NSForegroundColorAttributeName:UIColorFromRGB(RGB_NAVI_BAR_TEXT_BLACK)}];
    [self.navigationBar setTintColor:UIColorFromRGB(RGB_THEME_GREEN)];
}
- (void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated {
    viewController.hidesBottomBarWhenPushed = self.viewControllers.count > 0;
    [super pushViewController:viewController animated:animated];
}
- (NSArray<__kindof UIViewController *> *)popToRootViewControllerAnimated:(BOOL)animated {
    if (self.viewControllers.count > 1) {
        self.topViewController.hidesBottomBarWhenPushed = NO;
    }

    return [super popToRootViewControllerAnimated:animated];
}

@end


@implementation UINavigationBar (OKAppreance)
- (UIImageView *)ok_separator {
    return [self ok_findHairlineImageViewUnder:self];
}

- (UIImageView *)ok_findHairlineImageViewUnder:(UIView *)view {
   if ([view isKindOfClass:UIImageView.class] && view.bounds.size.height <= 1.0) {
       return (UIImageView *)view;
   }
   for (UIView *subview in view.subviews) {
       UIImageView *imageView = [self ok_findHairlineImageViewUnder:subview];
       if (imageView) {
           return imageView;
       }
   }
   return nil;
}
@end
