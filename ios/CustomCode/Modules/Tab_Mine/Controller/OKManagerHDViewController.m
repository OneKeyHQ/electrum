//
//  OKManagerHDViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKManagerHDViewController.h"
#import "OKBackUpViewController.h"
#import "OKDontScreenshotTipsViewController.h"
#import "OKDeleteWalletTipsViewController.h"
#import "OKReadyToStartViewController.h"

@interface OKManagerHDViewController () <UITableViewDelegate,UITableViewDataSource>

@end

@implementation OKManagerHDViewController

+ (instancetype)managerHDViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKManagerHDViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"management", nil);
    self.tableView.tableFooterView = [UIView new];
}

#pragma mark - Table view data source
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.row) {
        case 1:
        {
            OKWeakSelf(self)
            [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                NSString *result = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd,@"name":self.walletName}];
                if (result != nil) {
                    
                    OKReadyToStartViewController *readyToVc = [OKReadyToStartViewController readyToStartViewController];
                    readyToVc.words = result;
                    readyToVc.isExport = YES;
                    [weakself.OK_TopViewController.navigationController pushViewController:readyToVc animated:YES];
                }
            }];
        }
            break;
        case 3:
        {
            OKDeleteWalletTipsViewController *deleteWalletVc = [OKDeleteWalletTipsViewController deleteWalletTipsViewController];
            deleteWalletVc.walletName = self.walletName;
            [self.navigationController pushViewController:deleteWalletVc animated:YES];
        }
            break;
        default:
            break;
    };
}
@end
