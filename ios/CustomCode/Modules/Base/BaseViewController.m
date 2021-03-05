//
//  BaseViewController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

static const CGFloat showNavBarSeparatorScrollViewOffsetThreshold = 5;

@interface BaseViewController ()
{
    BOOL _interactivePopEnable;
    UIImageView *_navBarSeparator;
}
@end

@implementation BaseViewController

+ (__kindof BaseViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier {
    UIStoryboard *sb = [UIStoryboard storyboardWithName:storyboardName bundle:nil];
    BaseViewController *vc = [sb instantiateViewControllerWithIdentifier:identifier];
    return vc;
}

+ (__kindof BaseViewController *)initViewControllerWithStoryboardName:(NSString *)name {
    return [self initWithStoryboardName:name identifier:NSStringFromClass(self)];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    if (@available(iOS 13.0, *)) {
            self.modalInPresentation = YES;
    }
    // 替换返回按钮
    if ([self.navigationController.viewControllers count] > 1) {
        self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(backToPrevious)];
    };
    _navBarSeparator = [self findHairlineImageViewUnder:self.navigationController.navigationBar];
    UIView *separator = [[UIView alloc] initWithFrame:_navBarSeparator.bounds];
    separator.backgroundColor = HexColor(0xceced3);
    [_navBarSeparator addSubview:separator];
    _navBarSeparator.hidden = YES;
    self.navigationController.navigationBar.translucent = NO;
    [self.navigationController.navigationBar setBarTintColor:[self navBarTintColor]];

    _interactivePopEnable = self.navigationController.interactivePopGestureRecognizer.isEnabled;
}

- (UIImageView *)findHairlineImageViewUnder:(UIView *)view {
   if ([view isKindOfClass:UIImageView.class] && view.bounds.size.height <= 1.0) {
       return (UIImageView *)view;
   }
   for (UIView *subview in view.subviews) {
       UIImageView *imageView = [self findHairlineImageViewUnder:subview];
       if (imageView) {
           return imageView;
       }
   }
   return nil;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar setBarTintColor:[self navBarTintColor]];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (_forbidInteractivePopGestureRecognizer) {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if (_forbidInteractivePopGestureRecognizer) {
        self.navigationController.interactivePopGestureRecognizer.enabled = YES;
    }
}

- (void)viewDidDisappear:(BOOL)animated {
    _navBarSeparator.hidden = YES;
    [super viewDidDisappear:animated];
}

- (void)backButtonWhiteColor {
    UIImage *whiteImage = [[UIImage imageNamed:@"arrow_left_white"] imageWithColor:[UIColor whiteColor]];
    [(UIButton *)self.navigationItem.leftBarButtonItem.customView setImage:whiteImage forState:UIControlStateNormal];
    [(UIButton *)self.navigationItem.leftBarButtonItem.customView setImage:whiteImage forState:UIControlStateHighlighted];
}

- (void)hideBackBtn {
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] init];
}

- (void)AT_setExtraCellHide:(UITableView *)aTableView
{
     aTableView.tableFooterView = [[UIView alloc] init];
}

- (void)backToPrevious {
    if (self.backToPreviousCallback) {
        self.backToPreviousCallback();
    }
    if (self.isRoot == YES) {
        [[self.navigationController.viewControllers firstObject] setHidesBottomBarWhenPushed:NO];
        [self.navigationController popToRootViewControllerAnimated:YES];
    } else {
        if (self.navigationController.viewControllers.count <= 2) {
            [[self.navigationController.viewControllers firstObject] setHidesBottomBarWhenPushed:NO];
        }
        [self.navigationController popViewControllerAnimated:YES];
    }
}

- (void)setNavigationBarBackgroundColorWithClearColor {
    self.navigationController.navigationBar.translucent = YES;
    UIColor *color = [UIColor clearColor];
    CGRect rect = CGRectMake(0, 0, 1, 1);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    [self.navigationController.navigationBar setBackgroundImage:image forBarMetrics:UIBarMetricsDefault];
}

- (void)presentNavigationViewController:(UIViewController *)viewController animated:(BOOL)animated completion:(void (^ __nullable)(void))completion{
    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:viewController];
    nav.modalPresentationStyle = UIModalPresentationCustom;
    [self presentViewController:nav animated:animated completion:completion];
}

- (UIColor *)navBarTintColor {
    return [UIColor whiteColor];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    CGFloat offsetY = scrollView.contentOffset.y;
    _navBarSeparator.hidden = offsetY < showNavBarSeparatorScrollViewOffsetThreshold;
}
@end
