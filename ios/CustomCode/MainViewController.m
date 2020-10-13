//
//  ViewController.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import "MainViewController.h"
#import "OKWalletViewController.h"
#import "OKDiscoverViewController.h"
#import "OKMineViewController.h"

#define ItemSelectedColor   UIColorFromRGB(RGB_THEME_GREEN)
#define ItemUnSelectedColor    UIColorFromRGB(0x14293B)
#define ItemTitleFont    [UIFont boldSystemFontOfSize:10]

@interface MainViewController ()<UITabBarControllerDelegate>

@property (nonatomic, strong) UITabBarController *tab;
@property (nonatomic, strong)BaseNavigationController *walletVC;
@property (nonatomic, strong)BaseNavigationController *discoverVC;
@property (nonatomic, strong)BaseNavigationController *mineVC;

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
   
    [self addTabBarController];
    [self tabBarAppearance];
    [self tabBarResetLineColor];
    // Do any additional setup after loading the view.
    
}

- (void)addTabBarController
{
    NSMutableArray *viewControllers = [[NSMutableArray alloc] init];
    [viewControllers addObject:self.walletVC];
    [viewControllers addObject:self.discoverVC];
    [viewControllers addObject:self.mineVC];
    [self.tab setViewControllers:viewControllers animated:NO];
    [self.view addSubview:self.tab.view];
}

- (void)tabBarResetLineColor {
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, -0.5, SCREEN_WIDTH, 0.5)];
    view.backgroundColor = UIColorFromRGB(RGB_PARTING_LINE_GRAY);
    [self.tab.tabBar insertSubview:view atIndex:0];
}

- (void)tabBarAppearance {
    self.tab.tabBar.barTintColor = UIColorFromRGB(0xFCFCFC);
    if (@available(iOS 13.0, *)) {
        self.tab.tabBar.tintColor = ItemSelectedColor;
        self.tab.tabBar.unselectedItemTintColor = ItemUnSelectedColor;
        UITabBarItem *item = [UITabBarItem appearance];
        item.titlePositionAdjustment = UIOffsetMake(0, -2);
        [item setTitleTextAttributes:@{NSFontAttributeName:ItemTitleFont} forState:UIControlStateNormal];
        [item setTitleTextAttributes:@{NSFontAttributeName:ItemTitleFont} forState:UIControlStateSelected];
    } else {
        UITabBarItem *item = [UITabBarItem appearance];
        item.titlePositionAdjustment = UIOffsetMake(0, -2);
        [item setTitleTextAttributes:@{NSFontAttributeName:ItemTitleFont, NSForegroundColorAttributeName:ItemUnSelectedColor} forState:UIControlStateNormal];
        [item setTitleTextAttributes:@{NSFontAttributeName:ItemTitleFont, NSForegroundColorAttributeName:ItemSelectedColor} forState:UIControlStateSelected];
    }
}

- (UITabBarController *)tab {
    if (_tab == nil) {
        _tab = [[UITabBarController alloc] init];
        _tab.view.frame = self.view.bounds;
        _tab.tabBar.translucent = NO;
        _tab.delegate = self;
    }
    return _tab;
}

- (BaseNavigationController *)walletVC {
    if (_walletVC == nil) {
        _walletVC = [[BaseNavigationController alloc] initWithRootViewController:[OKWalletViewController walletViewController]];
        _walletVC.tabBarItem.title = @"钱包";
        _walletVC.tabBarItem.selectedImage = [[UIImage imageNamed:@"wallet_highlight"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        _walletVC.tabBarItem.image = [[UIImage imageNamed:@"wallet_normal"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    }
    return _walletVC;
}
- (BaseNavigationController *)discoverVC {
    if (_discoverVC == nil) {
        _discoverVC = [[BaseNavigationController alloc] initWithRootViewController:[OKDiscoverViewController discoverViewController]];
        _discoverVC.tabBarItem.title = @"发现";
        _discoverVC.tabBarItem.selectedImage = [[UIImage imageNamed:@"discovery_highlight"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        _discoverVC.tabBarItem.image = [[UIImage imageNamed:@"discovery_normal"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    }
    return _discoverVC;
}
- (BaseNavigationController *)mineVC {
    if (_mineVC == nil) {
        _mineVC = [[BaseNavigationController alloc] initWithRootViewController:[OKMineViewController mineViewController]];
        _mineVC.tabBarItem.title = @"我的";
        _mineVC.tabBarItem.selectedImage = [[UIImage imageNamed:@"me_highlight"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        _mineVC.tabBarItem.image = [[UIImage imageNamed:@"me_normal"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    }
    return _mineVC;
}
@end
