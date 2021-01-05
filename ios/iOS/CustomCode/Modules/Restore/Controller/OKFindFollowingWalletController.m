//
//  OKFindFollowingWalletController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFindFollowingWalletController.h"
#import "OKFindWalletTableViewCell.h"
#import "OKFindWalletTableViewCellModel.h"
#import "OKBiologicalViewController.h"

@interface OKFindFollowingWalletController ()<UITableViewDelegate,UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *tableViewBgView;
@property (weak, nonatomic) IBOutlet UITableView *tbaleView;
@property (weak, nonatomic) IBOutlet UIView *headerView;
@property (weak, nonatomic) IBOutlet UIButton *restoreBtn;
- (IBAction)restoreBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (nonatomic,strong)NSArray *walletList;

@end

@implementation OKFindFollowingWalletController

+ (instancetype)findFollowingWalletController
{
    return [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKFindFollowingWalletController"];
}
- (void)setCreateResultModel:(OKCreateResultModel *)createResultModel
{
    _createResultModel = createResultModel;
    self.walletList = self.createResultModel.derived_info;
    [self.tableView reloadData];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self hideBackBtn];
    self.tableView.tableFooterView = [UIView new];
    self.title = MyLocalizedString(@"Recover HD Wallet", nil);
    self.titleLabel.text = MyLocalizedString(@"Find the following wallet", nil);
    self.descLabel.text = MyLocalizedString(@"You have derived the following wallet using the HD mnemonic, select the one you want to recover. If you do not want to recover the wallet for a while, you can skip it and readd it later in the HD Wallet. Your assets will not be lost", nil);
    [self.tableViewBgView setLayerRadius:20];
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
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.walletList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKFindWalletTableViewCell";
    OKFindWalletTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKFindWalletTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
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
    OKFindWalletTableViewCellModel *model = self.walletList[indexPath.row];
    model.isSelected = !model.isSelected;
    [self.tableView reloadData];
}

- (IBAction)restoreBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    NSMutableArray *arrayM = [NSMutableArray array];
    for (OKFindWalletTableViewCellModel *model in self.walletList) {
        if (model.isSelected) {
            [arrayM addObject:model.name];
        }
    }
    [kPyCommandsManager callInterface:kInterfacerecovery_confirmed parameter:@{@"name_list":arrayM}];
    OKCreateResultWalletInfoModel *createResultWalletInfoModel = [self.createResultModel.wallet_info firstObject];
    OKWalletInfoModel *infoModel = [kWalletManager getCurrentWalletAddress:createResultWalletInfoModel.name];
    [kWalletManager setCurrentWalletInfo:infoModel];
    if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
        [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
    }
    if (!kWalletManager.isOpenAuthBiological && weakself.isInit) {
        OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:weakself.pwd biologicalViewBlock:^{
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":self.pwd,@"backupshow":@"0",@"takecareshow":@"1"}];
        }];
        [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
    }else{
        [self.OK_TopViewController dismissToViewControllerWithClassName:@"OKWalletViewController" animated:YES complete:^{
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":self.pwd,@"backupshow":@"0",@"takecareshow":@"1"}];
        }];
    }
}
@end
