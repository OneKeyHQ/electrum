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
            if (kWalletManager.isOpenAuthBiological) {
                [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                    if (state == YZAuthIDStateNotSupport
                        || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                        [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                            [weakself importSeedPwd:pwd];
                        }];
                    } else if (state == YZAuthIDStateSuccess) {
                        NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                        [weakself importSeedPwd:pwd];
                    }
                }];
            }else {
                [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                    [weakself importSeedPwd:pwd];
                }];
            }
        }
            break;
        case 3:
        {
            OKDeleteWalletTipsViewController *deleteWalletVc = [OKDeleteWalletTipsViewController deleteWalletTipsViewController];
            deleteWalletVc.walletName = self.walletName;
            deleteWalletVc.deleteType = OKWhereToDeleteTypeMine;
            [self.navigationController pushViewController:deleteWalletVc animated:YES];
        }
            break;
        default:
            break;
    };
}

- (void)importSeedPwd:(NSString *)pwd
{
    OKWeakSelf(self)
    NSString *result = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd,@"name":self.walletName}];
    if (result != nil) {
        OKReadyToStartViewController *readyToVc = [OKReadyToStartViewController readyToStartViewController];
        readyToVc.words = result;
        readyToVc.isExport = YES;
        [weakself.OK_TopViewController.navigationController pushViewController:readyToVc animated:YES];
    }
}
@end
