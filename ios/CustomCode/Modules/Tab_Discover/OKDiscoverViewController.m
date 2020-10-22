//
//  OKDiscoverViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKDiscoverViewController.h"

@interface OKDiscoverViewController ()<UINavigationControllerDelegate>

@end

@implementation OKDiscoverViewController
+ (instancetype)discoverViewController
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Tab_Discover" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:@"OKDiscoverViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.navigationController.delegate = self;
}

#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowDiscover = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowDiscover animated:YES];
}
@end
