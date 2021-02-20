//
//  OKCSViewController.m
//  OneKey
//
//  Created by liuzhijie on 2021/2/20.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKCSViewController.h"
@import SupportSDK;

@interface OKCSViewController ()
@property (weak, nonatomic) IBOutlet UIView *submitView;
@property (weak, nonatomic) IBOutlet UIView *historyView;
@end

@implementation OKCSViewController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKCSViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"cs.title", nil);
    UITapGestureRecognizer *submitTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(submit)];
    [self.submitView addGestureRecognizer:submitTap];
    UITapGestureRecognizer *historyTap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(history)];
    [self.historyView addGestureRecognizer:historyTap];
}

- (void)submit {
    UIViewController *vc = [ZDKRequestUi buildRequestUiWith:@[]];
    BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:vc];
    [self presentViewController:nav animated:YES completion:nil];

}

- (void)history {
    UIViewController *vc = [ZDKRequestUi buildRequestList];
    BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:vc];
    [self presentViewController:nav animated:YES completion:nil];
}
@end
