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


typedef enum {
    OKListTypeCreate,
    OKListTypeRestore
}OKListType;

@interface OKFindFollowingWalletController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *tableViewBgView;
@property (weak, nonatomic) IBOutlet UITableView *tbaleView;
@property (weak, nonatomic) IBOutlet UIView *headerView;
@property (weak, nonatomic) IBOutlet OKButton *restoreBtn;
- (IBAction)restoreBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (nonatomic,strong)NSArray *walletList;
@property (weak, nonatomic) IBOutlet UIImageView *quanquanView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tableViewBgCons;

@property (nonatomic,assign)OKListType listType;

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

- (void)rotateImageView {
    OKWeakSelf(self)
    CGFloat circleByOneSecond = 2.5f;
    [UIView animateWithDuration:1.f / circleByOneSecond
                          delay:0
                        options:UIViewAnimationOptionCurveLinear
                     animations:^{
        weakself.quanquanView.transform = CGAffineTransformRotate(weakself.quanquanView.transform, M_PI_2);
    }
                     completion:^(BOOL finished){
        [weakself rotateImageView];
    }];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.tableFooterView = [UIView new];
    self.title = MyLocalizedString(@"restore", nil);
    self.titleLabel.text = MyLocalizedString(@"Find the following wallet", nil);
    self.descLabel.text = MyLocalizedString(@"You have derived the following wallet using the HD mnemonic, select the one you want to recover. If you do not want to recover the wallet for a while, you can skip it and readd it later in the HD Wallet. Your assets will not be lost", nil);
    [self.tableViewBgView setLayerRadius:20];

    if (self.createResultModel == nil) {
        [self refreshUISearch:YES];
        [self rotateImageView];
        [self changeConfirmBtn];
        [self createWallet:self.pwd mnemonicStr:self.mnemonicStr isInit:self.isInit];
    }else{
        [self refreshUISearch:NO];
        [self changeConfirmBtn];
    }
}

- (void)refreshUISearch:(BOOL)isSearching
{
    if (isSearching) {
        self.quanquanView.hidden = NO;
        self.tableViewBgCons.constant = 28;
        self.restoreBtn.hidden = YES;
        self.descLabel.text = MyLocalizedString(@"Search your wallet...", nil);
        [self.view layoutIfNeeded];
    }else{
        self.quanquanView.hidden = YES;
        self.quanquanView.transform = CGAffineTransformIdentity;
        self.tableViewBgCons.constant = 200;
        self.restoreBtn.hidden = NO;
        self.descLabel.text = MyLocalizedString(@"You have created these wallets for this App, select which you want to restore", nil);
        [self.restoreBtn setTitle:MyLocalizedString(@"restore", nil) forState:UIControlStateNormal];
        [self.view layoutIfNeeded];
    }
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
    [self.navigationController popToRootViewControllerAnimated:YES];
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
    [self changeConfirmBtn];
}

- (void)createWallet:(NSString *)pwd mnemonicStr:(NSString *)mnemonicStr isInit:(BOOL)isInit
{
    NSString *seed = mnemonicStr == nil?@"":mnemonicStr;
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
        OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
        if (createResultModel != nil) {
            dispatch_sync(dispatch_get_main_queue(), ^{
                if (createResultModel.derived_info.count == 0) {
                    NSMutableArray *arrayM = [NSMutableArray array];
                    for (OKCreateResultWalletInfoModel *model in createResultModel.wallet_info) {
                        OKFindWalletTableViewCellModel *modelF = [OKFindWalletTableViewCellModel new];
                        modelF.name = model.name;
                        modelF.coin = model.coin_type;
                        modelF.label = [NSString stringWithFormat:@"%@-1",[model.coin_type uppercaseString]];
                        modelF.blance = [NSString stringWithFormat:@"0 %@",[model.coin_type uppercaseString]];
                        [arrayM addObject:modelF];
                    }
                    weakself.walletList = arrayM;
                    weakself.listType = OKListTypeCreate;
                    [weakself refreshUISearch:NO];
                    [weakself.tableView reloadData];
                }else{
                    weakself.walletList = createResultModel.derived_info;
                    weakself.listType = OKListTypeRestore;
                    [weakself refreshUISearch:NO];
                    [weakself.tableView reloadData];

                }
            });
        }
    });
}
- (BOOL)ishaveSelectModel
{
    NSInteger count = 0;
    for (OKFindWalletTableViewCellModel *model in self.walletList) {
        if (model.isSelected || model.exist) {
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
    for (OKFindWalletTableViewCellModel *model in self.walletList) {
        if (model.isSelected) {
            [arrayM addObject:model.name];
        }
    }
    id result =  [kPyCommandsManager callInterface:kInterfacerecovery_confirmed parameter:@{@"name_list":arrayM}];
    if (result != nil) {
        NSString *selectName = [arrayM firstObject];
        OKWalletInfoModel *infoModel = [kWalletManager getCurrentWalletAddress:selectName];
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
}
@end
