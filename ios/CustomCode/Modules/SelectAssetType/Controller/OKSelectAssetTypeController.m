//
//  OKSelectAssetTypeController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSelectAssetTypeController.h"
#import "OKSelectAssetTypeTableViewCell.h"
#import "OKSelectAssetTypeModel.h"
#import "OKBiologicalViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKSelectAssetTypeModel.h"


@interface OKSelectAssetTypeController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *tableViewBgView;
@property (weak, nonatomic) IBOutlet UITableView *tbaleView;
@property (weak, nonatomic) IBOutlet OKButton *restoreBtn;
- (IBAction)restoreBtnClick:(UIButton *)sender;
@property (nonatomic,strong)NSArray *walletList;
@end

@implementation OKSelectAssetTypeController

+ (instancetype)selectAssetTypeController
{
    return [[UIStoryboard storyboardWithName:@"SelectAssetType" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSelectAssetTypeController"];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.tableFooterView = [UIView new];
    self.title = MyLocalizedString(@"Add the asset", nil);
    self.titleLabel.text = MyLocalizedString(@"Select the assets to add", nil);
    [self setNavigationBarBackgroundColorWithClearColor];
    [self changeConfirmBtn];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]){
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
}

- (void)viewDidDisappear:(BOOL)animated {
  [super viewDidDisappear:animated];
  self.navigationController.interactivePopGestureRecognizer.enabled = YES;
}

- (void)backToPrevious
{
    OKWeakSelf(self)
    [self.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{
        [weakself.navigationController popToRootViewControllerAnimated:YES];
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.walletList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKSelectAssetTypeTableViewCell";
    OKSelectAssetTypeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKSelectAssetTypeTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.walletList[indexPath.row];
    return  cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 90;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKSelectAssetTypeModel *model = self.walletList[indexPath.row];
    model.isSelected = !model.isSelected;
    [self.tableView reloadData];
    [self changeConfirmBtn];
}

- (BOOL)ishaveSelectModel
{
    NSInteger count = 0;
    for (OKSelectAssetTypeModel *model in self.walletList) {
        if (model.isSelected) {
            count ++;
        }
    }
    return count > 0 ? YES:NO;
}
- (void)changeConfirmBtn
{
    BOOL isEnable = [self ishaveSelectModel];
    if (isEnable) {
        [self.restoreBtn status:OKButtonStatusEnabled];
    }else{
        [self.restoreBtn status:OKButtonStatusDisabled];
    }
}
- (IBAction)restoreBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    NSMutableArray *arrayM = [NSMutableArray array];
    for (OKSelectAssetTypeModel *model in self.walletList) {
        if (model.isSelected) {
            [arrayM addObject:[model.coin lowercaseString]];
        }
    }
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":weakself.pwd,@"create_coin":[arrayM mj_JSONString]}];
        dispatch_async(dispatch_get_main_queue(), ^{
            [MBProgressHUD hideHUDForView:self.view animated:YES];
            OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
            OKCreateResultWalletInfoModel *model =  createResultModel.wallet_info.firstObject;
            OKWalletInfoModel *walletInfoModel = [kWalletManager getCurrentWalletAddress:model.name];
            [kWalletManager setCurrentWalletInfo:walletInfoModel];
            if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
                [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
            }
            if (!kWalletManager.isOpenAuthBiological && weakself.isInit) {
                OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:weakself.pwd biologicalViewBlock:^{
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":weakself.pwd,@"backupshow":@"1"}];
                }];
                [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
            }else{
                OKWeakSelf(self)
                [self.OK_TopViewController dismissToViewControllerWithClassName:@"OKWalletViewController" animated:YES complete:^{
                    [weakself.navigationController popToRootViewControllerAnimated:YES];
                    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":weakself.pwd,@"backupshow":@"1"}];
                }];
            }
        });
    });
}

- (NSArray *)walletList
{
    if (!_walletList) {
        NSMutableArray *arrayM = [NSMutableArray array];
        for (NSString *coinType in kWalletManager.supportCoinArray) {
            OKSelectAssetTypeModel *modelF = [OKSelectAssetTypeModel new];
            modelF.coin = coinType;
            [arrayM addObject:modelF];
        }
        _walletList = arrayM;
    }
    return _walletList;
}
@end
