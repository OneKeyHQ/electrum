//
//  OKChangePwdViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/6.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKChangePwdViewController.h"
#import "OKPwdViewController.h"

@interface OKChangePwdViewController ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation OKChangePwdViewController
+ (instancetype)changePwdViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKChangePwdViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = MyLocalizedString(@"password", nil);
    self.tableView.tableFooterView = [UIView new];
    self.view.backgroundColor = HexColor(0xF5F6F7);
    
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKPwdViewController *pwdVc = [OKPwdViewController pwdViewController];
    pwdVc.pwdUseType = OKPwdUseTypeUpdatePassword;
    BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
    [self.view.window.rootViewController presentViewController:baseVc animated:YES completion:nil];

}


@end
