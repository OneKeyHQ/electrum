//
//  OKChangePwdViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/6.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKChangePwdViewController.h"
#import "OKPwdViewController.h"
#import "OKDeleteWalletTipsViewController.h"

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
    OKWeakSelf(self)
    switch (indexPath.row) {
        case 0:
        {
            [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeUpdatePassword setPwd:^(NSString * _Nonnull pwd) {
                    
                }];
                pwdVc.oldPwd = pwd;
                [weakself.OK_TopViewController.navigationController pushViewController:pwdVc animated:YES];
            }];
        }
            break;
        case 1:
        {
            OKDeleteWalletTipsViewController *deleteVc = [OKDeleteWalletTipsViewController deleteWalletTipsViewController];
            deleteVc.deleteType = OKWhereToDeleteTypeAPP;
            [weakself.navigationController pushViewController:deleteVc animated:YES];
        }
            break;
        default:
            break;
    }
    
}
@end
