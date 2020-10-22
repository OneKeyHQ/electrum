//
//  OKMineViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKMineViewController.h"

@interface OKMineViewController ()<UINavigationControllerDelegate>

@end

@implementation OKMineViewController
+ (instancetype)mineViewController
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:@"OKMineViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.navigationController.delegate = self;
}


#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowMine = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowMine animated:YES];
}

@end
