//
//  OKHDWalletViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKHDWalletViewController.h"
#import "OKTipsViewController.h"
#import "OKWalletListTableViewCell.h"
#import "OKWalletListTableViewCellModel.h"
#import "OKManagerHDViewController.h"
#import "OKSelectCoinTypeViewController.h"
#import "OKWalletListNoHDTableViewCellModel.h"
#import "OKWalletListNoHDTableViewCell.h"
#import "OKPwdViewController.h"
#import "OKWordImportVC.h"
#import "OKBiologicalViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"

@interface OKHDWalletViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *headerTitleLabel;
- (IBAction)headerTipsBtnclick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (nonatomic,strong)NSArray *showList;
@property (weak, nonatomic) IBOutlet UIView *countBgView;
- (IBAction)bottomBtnClick:(UIButton *)sender;
@property (nonatomic,copy)NSString *HDWalletName;
@property (nonatomic,strong)NSArray *NoHDArray;
@property (weak, nonatomic) IBOutlet UIView *footerBgView;
@end

@implementation OKHDWalletViewController

+ (instancetype)hdWalletViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKHDWalletViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(createWalletComplete) name:kNotiWalletCreateComplete object:nil];
    [self stupUI];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self refreshListData];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"HD wallet", nil);
    [self.countBgView setLayerRadius:10];
    [self.footerBgView setLayerDefaultRadius];
    
    NSString *labelText = MyLocalizedString(@"management", nil);
    CGFloat labelW = [labelText getWidthWithHeight:30 font:14];
    CGFloat labelmargin = 10;
    CGFloat labelH = 30;
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(labelmargin, 0, labelW, labelH)];
    label.text = labelText;
    label.font = [UIFont boldSystemFontOfSize:14];
    label.textColor = HexColor(0x26CF02);
    
    UIView *rightView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, labelW + labelmargin * 2, labelH)];
    rightView.backgroundColor = HexColorA(0x26CF02, 0.1);
    [rightView setLayerRadius:labelH * 0.5];
    [rightView addSubview:label];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:rightView];
    
    UITapGestureRecognizer *tapRightViewClick = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapRightViewClick)];
    [rightView addGestureRecognizer:tapRightViewClick];
    
    [self.footerBgView setLayerBoarderColor:HexColorA(0x546370, 0.3) width:1 radius:20];
}

- (void)tapRightViewClick
{
    OKManagerHDViewController *managerHDVc = [OKManagerHDViewController managerHDViewController];
    managerHDVc.walletName = self.HDWalletName;
    [self.navigationController pushViewController:managerHDVc animated:YES];
}


#pragma mark - 刷新UI
- (void)refreshListData
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    NSMutableArray *walletArray = [NSMutableArray arrayWithCapacity:listDictArray.count];
    for (int i = 0; i < listDictArray.count; i++) {
        NSDictionary *outerModelDict = listDictArray[i];
        OKWalletListTableViewCellModel *model = [OKWalletListTableViewCellModel new];
        model.walletName = [outerModelDict allKeys].firstObject;
        NSDictionary *innerDict = outerModelDict[model.walletName];
        model.walletType = [innerDict safeStringForKey:@"type"];
        model.walletTypeShowStr = [kWalletManager getWalletTypeShowStr:model.walletType];
        model.address = [innerDict safeStringForKey:@"addr"];
        model.label = [innerDict safeStringForKey:@"label"];
        model.backColor = [OKWalletListTableViewCellModel getBackColor:model.walletType];
        model.iconName = [OKWalletListTableViewCellModel getBgImageName:model.walletType];
        model.isCurrent = [kWalletManager.currentWalletInfo.name isEqualToString:model.walletName];
        [walletArray addObject:model];
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"walletType contains %@ || walletType contains %@",[@"HD" lowercaseString],[@"derived-standard" lowercaseString]];
    self.showList = [walletArray filteredArrayUsingPredicate:predicate];
    NSPredicate *predicateHD = [NSPredicate predicateWithFormat:@"walletType contains %@",[@"HD" lowercaseString]];
    OKWalletListTableViewCellModel *hdModel = [[walletArray filteredArrayUsingPredicate:predicateHD] firstObject];
    self.HDWalletName = hdModel.walletName;
    self.countLabel.text = [NSString stringWithFormat:@"%zd",self.showList.count];
    self.headerTitleLabel.text = MyLocalizedString(@"HD wallet", nil);
    self.footerBgView.hidden = self.showList.count == 0 ? YES : NO;
    
    if (self.showList.count == 0) {
        self.navigationItem.rightBarButtonItem.customView.userInteractionEnabled = NO;
        self.navigationItem.rightBarButtonItem.customView.alpha = 0.5;
    }else{
        self.navigationItem.rightBarButtonItem.customView.userInteractionEnabled = YES;
        self.navigationItem.rightBarButtonItem.customView.alpha = 1.0;
    }
    [self.tableView reloadData];
}
#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (self.showList.count == 0) {
        return self.NoHDArray.count;
    }
    return self.showList.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 90;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.showList.count == 0){
        static NSString *ID = @"OKWalletListNoHDTableViewCell";
        OKWalletListNoHDTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        if (cell == nil) {
            cell = [[OKWalletListNoHDTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        }
        OKWalletListNoHDTableViewCellModel *model = self.NoHDArray[indexPath.row];
        cell.model = model;
        return cell;
    }
    
    static NSString *ID = @"OKWalletListTableViewCell";
    OKWalletListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKWalletListTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKWalletListTableViewCellModel *model = self.showList[indexPath.row];
    cell.model = model;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (self.showList.count == 0) {
        switch (indexPath.row) {
            case 0:
            {
                OKWeakSelf(self)
                [weakself.OK_TopViewController dismissViewControllerAnimated:NO completion:^{
                    if ([kWalletManager checkIsHavePwd]) {
                        if (kWalletManager.isOpenAuthBiological) {
                            [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                                if (state == YZAuthIDStateNotSupport
                                    || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                                    [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                        [weakself createWallet:pwd];
                                    }];
                                } else if (state == YZAuthIDStateSuccess) {
                                    NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                                    [weakself createWallet:pwd];
                                }
                            }];
                        }else{
                            [OKValidationPwdController showValidationPwdPageOn:weakself isDis:NO complete:^(NSString * _Nonnull pwd) {
                                [weakself createWallet:pwd];
                            }];
                        }
                    }else{
                        OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
                            [weakself createWallet:pwd];
                        }];
                        BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
                        [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
                        
                    }
                }];
            }
                break;
            case 1:
            {
                OKWordImportVC *wordImport = [OKWordImportVC initViewController];
                BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:wordImport];
                [self.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
            }
                break;
            default:
                break;
        }
        return;
    }
}

- (void)createWallet:(NSString *)pwd
{
    NSString *seed = @"";
    NSArray *words = [NSArray array];
    NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
    OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
    words = [createResultModel.seed componentsSeparatedByString:@" "];
    if (words.count > 0) {
        OKCreateResultWalletInfoModel *model =  createResultModel.wallet_info.firstObject;
        OKWalletInfoModel *curentWalletModel= [kWalletManager getCurrentWalletAddress:model.name];
        [kWalletManager setCurrentWalletInfo:curentWalletModel];
        if (!kWalletManager.isOpenAuthBiological) {
            OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
            }];
            [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
        }else{
            [self.OK_TopViewController dismissToViewControllerWithClassName:@"OKHDWalletViewController" animated:YES complete:^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
            }];
        }
    }
}

- (IBAction)headerTipsBtnclick:(UIButton *)sender {
    OKTipsViewController *tipsVc = [OKTipsViewController tipsViewController];
    tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self presentViewController:tipsVc animated:NO completion:nil];
}

- (NSArray *)showList
{
    if (!_showList) {
        _showList = [NSArray array];
    }
    return _showList;
}

- (IBAction)bottomBtnClick:(UIButton *)sender {
    OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
    selectVc.addType = OKAddTypeCreateHDDerived;
    selectVc.where = OKWhereToSelectTypeHDMag;
    [self.navigationController pushViewController:selectVc animated:YES];
}


#pragma mark - createWalletComplete
- (void)createWalletComplete
{
    [self  refreshListData];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

- (NSArray *)NoHDArray
{
    if (!_NoHDArray) {
        
        OKWalletListNoHDTableViewCellModel *model1 = [OKWalletListNoHDTableViewCellModel new];
        model1.iconName = @"retorei_add";
        model1.titleStr = MyLocalizedString(@"Add HD Wallet", nil);
        model1.descStr = MyLocalizedString(@"Support BTC, ETH and other main chain", nil);
        
        OKWalletListNoHDTableViewCellModel *model2 = [OKWalletListNoHDTableViewCellModel new];
        model2.iconName = @"restore_phone";
        model2.titleStr = MyLocalizedString(@"Restore the purse", nil);
        model2.descStr = MyLocalizedString(@"Import through mnemonic", nil);
        
        _NoHDArray = @[model1,model2];
    }
    return _NoHDArray;
}
@end
