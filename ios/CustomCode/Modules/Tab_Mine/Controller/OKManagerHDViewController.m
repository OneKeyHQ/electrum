//
//  OKManagerHDViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKManagerHDViewController.h"
#import "OKBackUpViewController.h"

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
                NSString *result = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd}];
                if (![result isEqualToString:kErrorMsg]) {
                    OKBackUpViewController *backUpVc = [OKBackUpViewController backUpViewController];
                    backUpVc.words = [result componentsSeparatedByString:@" "];
                    backUpVc.showType = WordsShowTypeExport;
                    [weakself.OK_TopViewController.navigationController pushViewController:backUpVc animated:YES];
                }
            }];
        }
            break;
        case 3:
        {
            
        }
            break;
        default:
            break;
    };
}
@end
