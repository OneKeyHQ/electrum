//
//  BaseViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

@interface BaseViewController ()
{
    UIImage *_navigationBarBackgroundImage;
    NSDictionary<NSAttributedStringKey, id> *_navigationBarTitleTextAttributes;
    BOOL _isNavigationBarTranslucent;
    BOOL _interactivePopEnable;
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
    // 替换返回按钮
    if ([self.navigationController.viewControllers count] > 1) {
        self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTitle:MyLocalizedString(@"Return", nil) target:self selector:@selector(backToPrevious)];
    };
    
    if (_isNavigationBarTranslucent) {
        _navigationBarBackgroundImage = [self.navigationController.navigationBar backgroundImageForBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
        _isNavigationBarTranslucent = self.navigationController.navigationBar.isTranslucent;
        _navigationBarTitleTextAttributes = self.navigationController.navigationBar.titleTextAttributes;
    }
    _interactivePopEnable = self.navigationController.interactivePopGestureRecognizer.isEnabled;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];

    if (self.navigationbarTranslucent) {
        [self.navigationController.navigationBar setTranslucent:YES];
        [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
        self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor whiteColor]};
    }
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (_forbidInteractivePopGestureRecognizer) {
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
    if (self.navigationbarTranslucent) {
        [self.navigationController.navigationBar setTranslucent:YES];
        [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
        self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor whiteColor]};
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    if (_forbidInteractivePopGestureRecognizer) {
        self.navigationController.interactivePopGestureRecognizer.enabled = YES;
    }
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    if (_navigationbarTranslucent) {
        UINavigationController *naVC = self.navigationController ?:self.AT_NavigationController;
        [naVC.navigationBar setTranslucent:_isNavigationBarTranslucent];
        [naVC.navigationBar setBackgroundImage:_navigationBarBackgroundImage forBarMetrics:UIBarMetricsDefault];
        naVC.navigationBar.titleTextAttributes = _navigationBarTitleTextAttributes;
    }
}

- (void)backButtonWhiteColor {
    UIImage *whiteImage = [[UIImage imageNamed:@"Common_Back"] imageWithColor:[UIColor whiteColor]];
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
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)backToPrevious {
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
    CGRect rect = CGRectMake(0, 0, SCREEN_WIDTH, 64);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    [self.navigationController.navigationBar setBackgroundImage:image forBarMetrics:UIBarMetricsDefault];
    self.navigationController.navigationBar.clipsToBounds = YES;
}

- (void)presentNavigationViewController:(UIViewController *)viewController animated:(BOOL)animated completion:(void (^ __nullable)(void))completion{
    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:viewController];
    nav.modalPresentationStyle = UIModalPresentationCustom;
    [self presentViewController:nav animated:animated completion:completion];
}
@end
