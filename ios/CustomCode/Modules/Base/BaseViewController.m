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
@property (nonatomic, assign) BOOL showNavbarSeparator;
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
    separator.backgroundColor = UIColor.SP_NavBarSeparator;
    [_navBarSeparator addSubview:separator];
    self.navigationController.navigationBar.translucent = NO;
    [self.navigationController.navigationBar setBarTintColor:[self navBarTintColor]];

    _interactivePopEnable = self.navigationController.interactivePopGestureRecognizer.isEnabled;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    _navBarSeparator.hidden = !self.showNavbarSeparator;
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

- (void)AT_setExtraCellHide:(UITableView *)aTableView {
     aTableView.tableFooterView = [[UIView alloc] init];
}

#pragma mark - Navbar appearence
- (void)backButtonWhiteColor {
    UIImage *whiteImage = [[UIImage imageNamed:@"arrow_left_white"] imageWithColor:[UIColor whiteColor]];
    [(UIButton *)self.navigationItem.leftBarButtonItem.customView setImage:whiteImage forState:UIControlStateNormal];
    [(UIButton *)self.navigationItem.leftBarButtonItem.customView setImage:whiteImage forState:UIControlStateHighlighted];
}

- (void)hideBackBtn {
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] init];
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

- (void)setTitleColor:(UIColor *)titleColor {
    _titleColor = titleColor;
    NSMutableDictionary<NSAttributedStringKey, id> *titleTextAttributes = [self.navigationController.navigationBar.titleTextAttributes mutableCopy];
    [titleTextAttributes setObject:self.titleColor forKey:NSForegroundColorAttributeName];
    self.navigationController.navigationBar.titleTextAttributes = titleTextAttributes;
}

- (UIColor *)navBarTintColor {
    return [UIColor whiteColor];
}

- (void)setShowNavbarSeparator:(BOOL)showNavbarSeparator {
    _showNavbarSeparator = showNavbarSeparator;
    _navBarSeparator.hidden = !showNavbarSeparator;
}

- (UIScrollView *)scrollViewForNavbar {
    return nil;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    UIScrollView *targetScrollView = [self scrollViewForNavbar];
    if (scrollView != targetScrollView) {
        return;
    }
    CGFloat offsetY = scrollView.contentOffset.y;
    self.showNavbarSeparator = offsetY > showNavBarSeparatorScrollViewOffsetThreshold;
}

#pragma mark - Loading indicator view
- (UIView *)loadingIndicator {
    if (!_loadingIndicator) {
        _loadingIndicator = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
        _loadingIndicator.backgroundColor = HexColorA(0x444444, 0.6);
        _loadingIndicator.center = self.view.center;
        _loadingIndicator.centerY = self.view.centerY * 0.85;
        [_loadingIndicator setLayerRadius:5];

        UIActivityIndicatorView *actView = [[UIActivityIndicatorView alloc] initWithFrame:_loadingIndicator.bounds];
        actView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [actView startAnimating];
        [_loadingIndicator addSubview:actView];
        [self.view addSubview:_loadingIndicator];
        _loadingIndicator.hidden = YES;
    }
    [self.view bringSubviewToFront:_loadingIndicator];
    return _loadingIndicator;
}
@end
