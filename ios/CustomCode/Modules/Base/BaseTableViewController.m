//
//  BaseTableViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseTableViewController.h"

@interface BaseTableViewController ()

@end

@implementation BaseTableViewController
+ (__kindof BaseTableViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier {
    UIStoryboard *sb = [UIStoryboard storyboardWithName:storyboardName bundle:nil];
    BaseTableViewController *vc = [sb instantiateViewControllerWithIdentifier:identifier];
    return vc;
}

+ (__kindof BaseTableViewController *)initViewControllerWithStoryboardName:(NSString *)name {
    return [self initWithStoryboardName:name identifier:NSStringFromClass(self)];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // 替换返回按钮
    if ([self.navigationController.viewControllers count] > 1) {
        self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(backToPrevious)];
    };
}

- (void)AT_setExtraCellHide:(UITableView *)aTableView
{
    aTableView.tableFooterView = [[UIView alloc] init];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
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
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}
@end
