//
//  OKWalletViewController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletViewController.h"
#import "OKSelectCellModel.h"
#import "OKSelectTableViewCell.h"
#import "OKCreateHDCell.h"
#import "OKFirstUseViewController.h"
#import "OKPwdViewController.h"
#import "OKWordImportVC.h"
#import "OKAssetTableViewCell.h"
#import "OKTxListViewController.h"
#import "OKWalletDetailViewController.h"
#import "OKWalletListViewController.h"
#import "OKReceiveCoinViewController.h"
#import "OKBackUpTipsViewController.h"
#import "OKSendCoinViewController.h"
#import "OKBiologicalViewController.h"
#import "OKReadyToStartViewController.h"
#import "OKTxDetailViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKTakeCareMnemonicViewController.h"
#import "OKMatchingInCirclesViewController.h"
#import "OKSelectAssetTypeController.h"
#import "OKTokenManagementController.h"
#import "OKURLSchemeHandler.h"
#import "OKNotiAssetModel.h"
#import "OKChangeWalletController.h"
#import "OKQRScanResultController.h"
#import "OKSwitchWalletModel.h"

@interface OKWalletViewController ()<UITableViewDelegate,UITableViewDataSource,UINavigationControllerDelegate,UIGestureRecognizerDelegate>

//顶部切换钱包视图

@property (weak, nonatomic) IBOutlet UIView *topLeftBgView;

//无钱包的创建页面
@property (weak, nonatomic) IBOutlet UIView *createBgView;
@property (weak, nonatomic) IBOutlet UIView *topView;
@property (weak, nonatomic) IBOutlet UIView *bottomView;
@property (weak, nonatomic) IBOutlet UIButton *coinImage;
@property (weak, nonatomic) IBOutlet UILabel *walletName;
@property (weak, nonatomic) IBOutlet UITableView *selectCreateTableView;
@property (weak, nonatomic) IBOutlet UIButton *scanBtn;
@property (weak, nonatomic) IBOutlet UIImageView *bannerImageView;

@property (weak, nonatomic) IBOutlet UIView *leftView;
@property (weak, nonatomic) IBOutlet UIView *leftViewBg;

@property (nonatomic,strong)NSArray *allData;

//有钱包的界面
@property (weak, nonatomic) IBOutlet UIView *walletHomeBgView;
@property (weak, nonatomic) IBOutlet UIView *walletTopBgView;
@property (weak, nonatomic) IBOutlet UILabel *balance;
@property (weak, nonatomic) IBOutlet UILabel *myassetLabel;
@property (weak, nonatomic) IBOutlet UIImageView *eyeBtn;
@property (weak, nonatomic) IBOutlet UIView *eyebgView;

@property (weak, nonatomic) IBOutlet UIView *srBgView;
@property (weak, nonatomic) IBOutlet UIButton *sendBtn;
- (IBAction)sendBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *receiveBtn;
- (IBAction)receiveBtnClick:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UIButton *signatureBtn;
- (IBAction)signatureBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UITableView *assetTableView;
@property (strong, nonatomic) UIRefreshControl* refreshControl;
@property (weak, nonatomic) IBOutlet UIView *hwBgView;
@property (weak, nonatomic) IBOutlet UILabel *hwWalletNameLabel;
//备份提醒
@property (weak, nonatomic) IBOutlet UIView *backupBgView;
@property (weak, nonatomic) IBOutlet UILabel *backupTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *backupDescLabel;
@property (weak, nonatomic) IBOutlet UIStackView *stackView;
//assetTableViewHeader
@property (weak, nonatomic) IBOutlet UIView *assettableViewHeader;
@property (weak, nonatomic) IBOutlet UILabel *tableViewHeaderTitleLabel;
@property (weak, nonatomic) IBOutlet UITextField *tableViewHeaderSearch;
@property (weak, nonatomic) IBOutlet UIButton *tableViewHeaderAddBtn;
- (IBAction)tableViewHeaderAddBtnClick:(UIButton *)sender;
- (IBAction)assetListBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *tableViewHeaderView;
@property (weak, nonatomic) IBOutlet UIView *tableViewFooterView;
@property (weak, nonatomic) IBOutlet UIView *footerViewlow;
@property (weak, nonatomic) IBOutlet UIView *footerViewBlank;
@property (nonatomic,strong)NSArray *listWallets;
@property (nonatomic,assign)BOOL isCanSideBack;
@property (nonatomic,strong)OKNotiAssetModel *notiAssetModel;
@property (nonatomic,strong)NSArray *allAssetData;
@property (nonatomic,assign)BOOL isRefreshing;
@end

@implementation OKWalletViewController

+ (instancetype)walletViewController
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:@"OKWalletViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];

    [self stupUI];
    [self showFirstUse];
    [self addNotifications];
    [OKPyCommandsManager sharedInstance];
    [kPyCommandsManager callInterface:kInterfaceLoad_all_wallet parameter:@{}];
    [self checkWalletResetUI];
    [kUserSettingManager setDefaultSetings];
}

- (void)switchWallet
{
    dispatch_async(dispatch_get_main_queue(), ^{
        self.balance.text = @"--";
    });
    NSString *name = kWalletManager.currentWalletInfo.name;
    if ((name == nil || name.length == 0) && self.listWallets.count > 0 ) {
        NSDictionary *dict = [self.listWallets lastObject];
        name = [[dict allKeys] firstObject];
        NSDictionary *value = dict[name];
        [kWalletManager setCurrentWalletInfo:[OKWalletInfoModel mj_objectWithKeyValues:value]];
    }
    if (name.length == 0 || name == nil) {
        return;
    }
    OKWeakSelf(self)
    [MBProgressHUD showHUDAddedTo:weakself.view animated:YES];
    dispatch_sync(dispatch_get_global_queue(0, 0), ^{
        id result = [kPyCommandsManager callInterface:kInterface_switch_wallet parameter:@{@"name":kWalletManager.currentWalletInfo.name}];
        if (result != nil) {
            [self updateDataList:result isPush:NO];
        }else{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [MBProgressHUD hideHUDForView:weakself.view animated:YES];
            });
        }
    });
}

- (void)getBalance
{
    OKWeakSelf(self)
    weakself.isRefreshing = YES;
    dispatch_sync(dispatch_get_global_queue(0, 0), ^{
        NSDictionary* result = [kPyCommandsManager callInterface:kInterface_get_wallet_balance parameter:@{}];
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself.refreshControl endRefreshing];
        });
        if (result != nil) {
            NSMutableDictionary *dictM = [NSMutableDictionary dictionaryWithDictionary:result];
            [dictM setValue:[result safeStringForKey:@"all_balance"] forKey:@"sum_fiat"];
            [dictM setValue:[result safeStringForKey:@"all_balance"] forKey:@"fiat"];
            [dictM setValue:kWalletManager.currentWalletInfo.addr forKey:@"address"];
            [dictM setValue:kWalletManager.currentWalletInfo.coinType forKey:@"coin"];
            NSMutableArray *walletM = [NSMutableArray arrayWithArray:result[@"wallets"]];
            if (walletM.count > 0) {
                NSDictionary *walletD = [walletM firstObject];
                if ([walletD safeStringForKey:@"address"].length == 0) {
                    [dictM setValue:[walletD safeStringForKey:@"balance"] forKey:@"balance"];
                    [walletM removeObject:walletD];
                }
            }
            dictM[@"tokens"] = walletM;
            [self updateDataList:dictM isPush:NO];
        }
    });
}

- (void)updateDataList:(NSDictionary *)dict isPush:(BOOL)push
{
    NSMutableArray *arrayM = [NSMutableArray array];
    NSArray *tokens = [NSArray array];
    OKAssetTableViewCellModel *coinModel = [OKAssetTableViewCellModel new];
    if (push) {
        if (self.isRefreshing) {
            return;
        }
        self.notiAssetModel = [OKNotiAssetModel mj_objectWithKeyValues:dict];
        tokens = self.notiAssetModel.tokens;
        coinModel.balance = self.notiAssetModel.balance;
        coinModel.coinType = [kWalletManager getShowUICoinType:self.notiAssetModel.coin];
        coinModel.money = self.notiAssetModel.fiat;
        coinModel.iconImage = [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]];
        [arrayM addObject:coinModel];
    }else{
        if (self.isRefreshing) {
            self.notiAssetModel = [OKNotiAssetModel mj_objectWithKeyValues:dict];
            tokens = self.notiAssetModel.tokens;
            coinModel.balance = self.notiAssetModel.balance;
            coinModel.coinType = [kWalletManager getShowUICoinType:self.notiAssetModel.coin];
            coinModel.money = self.notiAssetModel.fiat;
            coinModel.iconImage = [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]];
            [arrayM addObject:coinModel];
        }else{
            OKSwitchWalletModel *switchWalletModel = [OKSwitchWalletModel mj_objectWithKeyValues:dict];
            tokens = switchWalletModel.wallets;
            coinModel.balance = @"0";
            coinModel.coinType = [kWalletManager getShowUICoinType:@""];
            coinModel.money = @"0";
            coinModel.iconImage = [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]];
            [arrayM addObject:coinModel];
        }
    }
    if (tokens.count != 0) {
        for (OKTokenAssetModel *model in tokens) {
            OKAssetTableViewCellModel *tokenModel = [OKAssetTableViewCellModel new];
            tokenModel.balance = model.balance;
            tokenModel.coinType = model.coin;
            tokenModel.money = model.fiat?:@"";
            tokenModel.contract_addr = model.address;
            NSArray *tokens = [[OKTokenManager sharedInstance]tokensFilterWith:model.address];
            NSString *imageName = [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]];
            if (tokens.count == 0) {
                tokenModel.iconImage = imageName;
            }else{
                OKToken *t = [tokens firstObject];
                tokenModel.iconImage = t.logoURI.length ?t.logoURI:imageName;
            }
            [arrayM addObject:tokenModel];
        }
    }
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
        self.allAssetData = arrayM;
        [MBProgressHUD hideHUDForView:weakself.view animated:YES];
       // UI更新代码
        NSString *fiatStr = [kWalletManager isETHClassification:kWalletManager.currentWalletInfo.coinType ]?self.notiAssetModel.sum_fiat:self.notiAssetModel.fiat;
        NSArray *barray = [fiatStr componentsSeparatedByString:@" "];
        NSString *bStr = [NSString stringWithFormat:@"%@ %@",kWalletManager.currentFiatSymbol,[barray firstObject]];
        if (fiatStr.length == 0) {
            bStr = @"--";
        }
        if (kWalletManager.showAsset) {
            bStr = @"****";
        }
        self.balance.text = bStr;
        self.walletName.text = kWalletManager.currentWalletInfo.label.length > 0 ? kWalletManager.currentWalletInfo.label : MyLocalizedString(@"No purse", nil);
        if (kWalletManager.currentWalletInfo.label.length > 0) {
            [self.coinImage setImage:[UIImage imageNamed:coinModel.iconImage] forState:UIControlStateNormal];
        }else{
            [self.coinImage setImage:[UIImage imageNamed:@"loco_round"] forState:UIControlStateNormal];
        }
        if (self.allAssetData.count > 4) {
            self.footerViewBlank.hidden = YES;
            self.tableViewFooterView.frame = CGRectMake(0, 0, SCREEN_WIDTH, 74);
        }else{
            self.footerViewBlank.hidden = NO;
            self.tableViewFooterView.frame = CGRectMake(0, 0, SCREEN_WIDTH, 194);
        }
        [self.assetTableView reloadData];
        if (!push) {
            weakself.isRefreshing = NO;
        }
    });
}

- (void)onRefresh
{
    [self performSelector:@selector(loadData) withObject:nil afterDelay:1.0];
}
- (void)loadData
{
    [self getBalance];
    [_refreshControl endRefreshing];
}
#pragma mark - 检查钱包状态并重置UI
- (void)checkWalletResetUI
{
    self.listWallets = [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    if (self.listWallets == nil) {
        usleep(10000);
        self.listWallets = [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    }

    if (self.listWallets.count > 0) {
        [self switchWallet];
    }else{
        dispatch_async(dispatch_get_main_queue(), ^{
            self.walletName.text = MyLocalizedString(@"No purse", nil);
            [self.coinImage setImage:[UIImage imageNamed:@"loco_round"] forState:UIControlStateNormal];
        });
    };
    BOOL isBackUp = YES;
    //是否创建过钱包
    if (self.listWallets.count > 0) { //创建过
        self.scanBtn.hidden = NO;
        self.createBgView.hidden = YES;
        self.walletHomeBgView.hidden = NO;
        isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":kWalletManager.currentWalletInfo.name}] boolValue];
    }else{
        self.scanBtn.hidden = YES;
        self.walletName.text = MyLocalizedString(@"No purse", nil);
        self.createBgView.hidden = NO;
        self.walletHomeBgView.hidden = YES;
    }
    if (isBackUp) {
        self.backupBgView.hidden = YES;
        self.tableViewHeaderView.frame = CGRectMake(0, 0, SCREEN_WIDTH, 348);
        [self.assetTableView setTableHeaderView:self.tableViewHeaderView];
    }else{
        self.tableViewHeaderView.frame = CGRectMake(0, 0, SCREEN_WIDTH, 490);
        [self.assetTableView setTableHeaderView:self.tableViewHeaderView];
        self.backupBgView.hidden = NO;
    }

    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        self.signatureBtn.hidden = [[kWalletManager.currentWalletInfo.coinType uppercaseString] isEqualToString:COIN_BTC]?NO:YES;
        self.hwBgView.hidden = NO;
        OKDeviceModel *deviceModel = [[OKDevicesManager sharedInstance]getDeviceModelWithID:kWalletManager.currentWalletInfo.device_id];
        if (deviceModel.deviceInfo.label.length > 0 && deviceModel.deviceInfo.label != nil) {
            self.hwWalletNameLabel.text = deviceModel.deviceInfo.label;
        }else if (deviceModel.deviceInfo.ble_name.length > 0 && deviceModel.deviceInfo.ble_name != nil){
            self.hwWalletNameLabel.text = deviceModel.deviceInfo.ble_name;
        }else{
            self.hwWalletNameLabel.text = deviceModel.deviceInfo.device_id;
        }
    }else{
        self.signatureBtn.hidden = YES;
        self.hwBgView.hidden = YES;
    }
    if ([kWalletManager isETHClassification:kWalletManager.currentWalletInfo.coinType]) {
        self.tableViewHeaderAddBtn.hidden = NO;
    }else{
        self.tableViewHeaderAddBtn.hidden = YES;
    };
}

- (void)showFirstUse
{
    NSString *firstUsed = [OKStorageManager loadFromUserDefaults:kFirstUsedShowed];
    if ([firstUsed isValid]) {
        return;
    }
    OKFirstUseViewController *firstUseVc = [OKFirstUseViewController firstUseViewController];
    BaseNavigationController *navVc = [[BaseNavigationController alloc]initWithRootViewController:firstUseVc];
    navVc.modalPresentationStyle = UIModalPresentationFullScreen;
    [self.navigationController presentViewController:navVc animated:NO completion:nil];
}

- (void)backUpBgClick
{
     OKWeakSelf(self)
     if (kWalletManager.isOpenAuthBiological) {
        [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
            if (state == YZAuthIDStateNotSupport
                || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                    [weakself backUpPwd:pwd];
                }];
            } else if (state == YZAuthIDStateSuccess) {
                NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                [weakself backUpPwd:pwd];
            }
        }];
    }else{
     [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
         [weakself backUpPwd:pwd];
     }];
    }
}
- (void)backUpPwd:(NSString *)pwd
{
    OKWeakSelf(self)
    NSString *words = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd,@"name":kWalletManager.currentWalletInfo.name}];
    if (words != nil) {
        OKReadyToStartViewController *readyToStart = [OKReadyToStartViewController readyToStartViewController];
        readyToStart.words = words;
       readyToStart.pwd = pwd;
       readyToStart.walletName = kWalletManager.currentWalletInfo.name;
       [weakself.OK_TopViewController.navigationController pushViewController:readyToStart animated:YES];
    }
}
#pragma mark - tapEyeClick
- (void)tapEyeClick
{
    BOOL isShowAsset = !kWalletManager.showAsset;
    [kWalletManager setShowAsset:isShowAsset];
    if (!isShowAsset) {
        self.eyeBtn.image = [UIImage imageNamed:@"eyehome"];
        NSString *money =  [[self.notiAssetModel.sum_fiat componentsSeparatedByString:@" "] firstObject];
        NSString *bStr = [NSString stringWithFormat:@"%@ %@",kWalletManager.currentFiatSymbol,money];
        if (self.notiAssetModel.sum_fiat.length == 0) {
            bStr = @"--";
        }
        self.balance.text = bStr;
    }else{
        self.eyeBtn.image = [UIImage imageNamed:@"hide_on"];
        self.balance.text = @"****";
    }
    [self.assetTableView reloadData];
}
- (void)changeEye
{
    if (!kWalletManager.showAsset) {
        self.eyeBtn.image = [UIImage imageNamed:@"eyehome"];
    }else{
        self.eyeBtn.image = [UIImage imageNamed:@"hide_on"];
    }
}

- (void)tapBanner {
    WebViewVC * webVC = [WebViewVC loadWebViewControllerWithTitle:nil url:kAppUserGuide useProxy:YES];
    [self.navigationController pushViewController:webVC animated:YES];
}

#pragma mark - 切换钱包
- (void)tapGestureClick
{
    OKWeakSelf(self)
    OKWalletListViewController *walletListVc = [OKWalletListViewController walletListViewController:^{
        [weakself loadData];
    }];
    BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:walletListVc];
    [self.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
}
#pragma mark - 扫描二维码
- (void)scanBtnClick
{
    OKWeakSelf(self)
    OKWalletScanVC *vc = [OKWalletScanVC initViewControllerWithStoryboardName:@"Scan"];
    vc.scanningType = ScanningTypeAddressInplace;
    vc.scanningCompleteBlock = ^(OKWalletScanVC *scanVC, id result) {
        if (!result) {
            return;
        }

        NSDictionary *typeDict = [kPyCommandsManager callInterface:kInterfaceparse_pr parameter:@{@"data":result}];

        if (!typeDict) {
            OKQRScanResultController *resultVC = [OKQRScanResultController controllerWithStoryboard];
            resultVC.resultText = result;
            [weakself.navigationController pushViewController:resultVC animated:YES];
        }

        OKQRParseType type = (OKQRParseType)[typeDict[@"type"] integerValue];
        switch (type) {
            case OKQRParseTypeAddress: {
                NSDictionary *data = typeDict[@"data"];
                NSString *address = [data safeStringForKey:@"address"];
                NSString *coin = [data safeStringForKey:@"coin"];
                OKChangeWalletController *changeVC = [OKChangeWalletController controllerWithStoryboard];
                changeVC.chianType = [coin isEqualToString:@"btc"] ? OKChangeWalletChainTypeBTC : OKChangeWalletChainTypeETHLike;
                changeVC.cancelCallback = ^(BOOL selected) {
                    if (!selected) {
                        [scanVC.scanManager sessionStartRunning];
                    }
                };
                changeVC.walletChangedCallback = ^(OKWalletInfoModel * _Nonnull wallet) {
                    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
                        OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
                        matchingVc.type = OKMatchingTypeTransfer;
                        matchingVc.where = OKMatchingFromWhereNav;
                        matchingVc.addressForTransfer = address;
                        [weakself.navigationController pushViewController:matchingVc animated:YES];
                    } else {
                        OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
                        sendCoinVc.address = address;
                        sendCoinVc.coinType = kWalletManager.currentWalletInfo.coinType;
                        [weakself.navigationController pushViewController:sendCoinVc animated:YES];
                    }
                };
                changeVC.modalPresentationStyle = UIModalPresentationOverCurrentContext;
                [weakself.navigationController presentViewController:changeVC animated:NO completion:nil];
            } break;
            default: {
                OKQRScanResultController *resultVC = [OKQRScanResultController controllerWithStoryboard];
                resultVC.resultText = result;
                [weakself.navigationController pushViewController:resultVC animated:YES];
            } break;


        }
    };
    [vc authorizePushOn:self];
}
#pragma mark - TableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if (tableView == self.selectCreateTableView) {
        return 1;
    }
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (tableView == self.selectCreateTableView) {
        return self.allData.count;
    }
    return  self.allAssetData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == self.selectCreateTableView) {
        if (indexPath.row == 0) {
            static NSString *ID = @"OKCreateHDCell";
            OKCreateHDCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
            if (cell == nil) {
                cell = [[OKCreateHDCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
            }
            cell.model = [self.allData firstObject];
            return cell;
        }else{
            static NSString *ID = @"OKSelectTableViewCell";
            OKSelectTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
            if (cell == nil) {
                cell = [[OKSelectTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
            }
            OKSelectCellModel *model = self.allData[indexPath.row];
            cell.model = model;
            return cell;
        }
    }
    static NSString *ID = @"OKAssetTableViewCell";
    OKAssetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAssetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allAssetData[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView == self.selectCreateTableView) {
        OKWeakSelf(self)
        OKSelectCellModel *model = self.allData[indexPath.row];
        if (model.type == OKSelectCellTypeCreateHD) { //创建
            if ([kWalletManager checkIsHavePwd]) {
                if (kWalletManager.isOpenAuthBiological) {
                   [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                       if (state == YZAuthIDStateNotSupport
                           || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                           [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                               [weakself createWallet:pwd isInit:NO];
                           }];
                       } else if (state == YZAuthIDStateSuccess) {
                           NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                           [weakself createWallet:pwd isInit:NO];
                       }
                   }];
               }else{
                   [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                        [weakself createWallet:pwd isInit:NO];
                    }];
               }
            }else{
                OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
                    [weakself createWallet:pwd isInit:YES];
                }];
                BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
                [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
            }
        }else if (model.type == OKSelectCellTypeRestoreHD){ //恢复
            OKWordImportVC *wordImport = [OKWordImportVC initViewController];
            BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:wordImport];
            [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
        }else if (model.type == OKSelectCellTypeMatchHD){ //匹配硬件
            OKMatchingInCirclesViewController *matchVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
            matchVc.where = OKMatchingFromWhereDis;
            BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:matchVc];
            [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
        }
        return;
    }
    OKAssetTableViewCellModel *model = self.allAssetData[indexPath.row];
    if (model.coinType.length == 0 || model.coinType == nil) {
        return;
    }
    OKTxListViewController *txListVc = [OKTxListViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    txListVc.model = model;
    txListVc.coinType = kWalletManager.currentWalletInfo.coinType;
    if ([[kWalletManager.currentWalletInfo.coinType lowercaseString]isEqualToString:[txListVc.model.coinType lowercaseString]]) {
        txListVc.tokenType = @"";
    }else{
        txListVc.tokenType = [txListVc.model.coinType uppercaseString];
    }
    [self.navigationController pushViewController:txListVc animated:YES];
}

- (void)createWallet:(NSString *)pwd isInit:(BOOL)isInit
{
    OKSelectAssetTypeController *selectAssetTypeVc = [OKSelectAssetTypeController selectAssetTypeController];
    selectAssetTypeVc.pwd = pwd;
    selectAssetTypeVc.isInit = isInit;
    [self.OK_TopViewController.navigationController pushViewController:selectAssetTypeVc animated:YES];
}

- (NSArray *)allData
{
    if (!_allData) {
        _allData = [NSArray array];
        OKSelectCellModel *model1 = [OKSelectCellModel new];
        model1.titleStr = MyLocalizedString(@"Create HD Wallet", nil);
        model1.descStr = MyLocalizedString(@"easy to use", nil);
        model1.imageStr = @"add";

        OKSelectCellModel *model2 = [OKSelectCellModel new];
        model2.titleStr = MyLocalizedString(@"Recover HD Wallet", nil);
        model2.descStr = MyLocalizedString(@"Recovery by mnemonic", nil);
        model2.imageStr = @"import";
        model2.descStrL = @"";
        model2.type = OKSelectCellTypeRestoreHD;

        OKSelectCellModel *model3 = [OKSelectCellModel new];
        model3.titleStr = MyLocalizedString(@"Paired hardware wallet", nil);
        model3.descStr = MyLocalizedString(@"Support BixinKey", nil);
        model3.imageStr = @"match_hardware";
        model3.descStrL = @"";
        model3.type = OKSelectCellTypeMatchHD;

        _allData = @[model1,model2,model3];
    }
    return  _allData;
}

#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowHomePage = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowHomePage animated:YES];
}
#pragma mark - 转账
- (IBAction)sendBtnClick:(UIButton *)sender {
    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
        matchingVc.type = OKMatchingTypeTransfer;
        matchingVc.where = OKMatchingFromWhereNav;
        [self.navigationController pushViewController:matchingVc animated:YES];
    }else{
        OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
        sendCoinVc.coinType = kWalletManager.currentWalletInfo.coinType;
        [self.navigationController pushViewController:sendCoinVc animated:YES];
    }
}
#pragma mark - 收款
- (IBAction)receiveBtnClick:(UIButton *)sender {
    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
        matchingVc.type = OKMatchingTypeReceiveCoin;
        matchingVc.where = OKMatchingFromWhereNav;
        [self.navigationController pushViewController:matchingVc animated:YES];
    }else{
        OKReceiveCoinViewController *receiveCoinVc = [OKReceiveCoinViewController receiveCoinViewController];
        receiveCoinVc.coinType = kWalletManager.currentWalletInfo.coinType;
        receiveCoinVc.walletType = [kWalletManager getWalletDetailType];
        [self.navigationController pushViewController:receiveCoinVc animated:YES];
    }
}
#pragma mark - 签名
- (IBAction)signatureBtnClick:(UIButton *)sender {
    OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
    matchingVc.type = OKMatchingTypeSignatureData;
    matchingVc.where = OKMatchingFromWhereNav;
    [self.navigationController pushViewController:matchingVc animated:YES];
}
#pragma mark - 钱包详情
- (IBAction)assetListBtnClick:(UIButton *)sender {
    OKWalletDetailViewController *walletDetailVc = [OKWalletDetailViewController walletDetailViewController];
    [self.navigationController pushViewController:walletDetailVc animated:YES];
}
- (void)tapWalletTopBgViewClick
{
    [self assetListBtnClick:nil];
}
#pragma mark - 添加Token
- (IBAction)tableViewHeaderAddBtnClick:(UIButton *)sender {
    OKTokenManagementController *tokenVc = [OKTokenManagementController controllerWithStoryboard];
    self.isRefreshing = YES;
    [self.navigationController pushViewController:tokenVc animated:YES];
}

#pragma mark - NotiWalletCreateComplete
- (void)notiWalletCreateComplete:(NSNotification *)noti
{
    [self checkWalletResetUI];
    NSDictionary *dict = noti.object;
    BOOL showB = [[dict safeStringForKey:@"backupshow"] boolValue];
    BOOL showT = [[dict safeStringForKey:@"takecareshow"]boolValue];

    if (showB == YES) {
        NSString *pwd = [dict safeStringForKey:@"pwd"];
        OKWeakSelf(self)
        OKBackUpTipsViewController *backUpTips = [OKBackUpTipsViewController backUpTipsViewController:^(BackUpBtnClickType type) {
                if (type == BackUpBtnClickTypeClose) {
                    //下次再说  关闭窗口
                }else if (type == BackUpBtnClickTypeBackUp){
                    NSString *words = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd,@"name":kWalletManager.currentWalletInfo.name}];
                    if (words != nil) {
                        OKReadyToStartViewController *readyToStart = [OKReadyToStartViewController readyToStartViewController];
                        readyToStart.pwd = pwd;
                        readyToStart.words = words;
                        readyToStart.walletName = kWalletManager.currentWalletInfo.name;
                        [weakself.OK_TopViewController.navigationController pushViewController:readyToStart animated:YES];
                    }
                }
        }];
        UINavigationController *navVc = [[UINavigationController alloc]initWithRootViewController:backUpTips];
        navVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
        [self.OK_TopViewController presentViewController:navVc animated:NO completion:nil];
    }else if (showT == YES){
        OKTakeCareMnemonicViewController *TakeCareMnemonicVc = [OKTakeCareMnemonicViewController takeCareMnemonicViewController];
        TakeCareMnemonicVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
        [self.OK_TopViewController presentViewController:TakeCareMnemonicVc animated:NO completion:nil];
    }
}

- (void)notiSelectWalletComplete
{
    [self checkWalletResetUI];
}

- (void)notiUpdate_status:(NSNotification *)noti
{
    NSDictionary * infoDic = [noti object];
    [self updateDataList:infoDic isPush:YES];
}

- (void)notiDeleteWalletComplete
{
    [self checkWalletResetUI];
    if (![kWalletManager checkIsHavePwd]) {
        kWalletManager.isOpenAuthBiological = NO;
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiUpdatePassWordComplete object:nil];
    }
}

- (void)notiBackUPWalletComplete
{
    if (kWalletManager.isOpenAuthBiological) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    [self checkWalletResetUI];
}

#pragma mark - notiHwInfoUpdate
- (void)notiHwInfoUpdate
{
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *jsonDict =  [kPyCommandsManager callInterface:kInterfaceget_feature parameter:@{@"path":kBluetooth_iOS}];
        if (jsonDict != nil) {
            OKDeviceModel *deviceModel  = [[OKDeviceModel alloc]initWithJson:jsonDict];
            kOKBlueManager.currentDeviceID = deviceModel.deviceInfo.device_id;
            [[OKDevicesManager sharedInstance]addDevices:deviceModel];
        }
    });
}

#pragma mark - notiSwitchWalletNeed
- (void)notiSwitchWalletNeed
{
    [self switchWallet];
}


-(void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    self.isCanSideBack = NO; //关闭ios右滑返回
    if([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
        self.navigationController.interactivePopGestureRecognizer.delegate = self;
    }

}

- (void)viewDidDisappear:(BOOL)animated {

    [super viewDidDisappear:animated];

    self.isCanSideBack=YES; //开启ios右滑返回
    if([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]) {
        self.navigationController.interactivePopGestureRecognizer.delegate = nil;
    }
}


- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer*)gestureRecognizer
{
    return self.isCanSideBack;
}


- (void)addNotifications
{
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiWalletCreateComplete:) name:kNotiWalletCreateComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiSelectWalletComplete) name:kNotiSelectWalletComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiUpdate_status:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiDeleteWalletComplete) name:kNotiDeleteWalletComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiBackUPWalletComplete) name:kNotiBackUPWalletComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiHwInfoUpdate) name:kNotiHwInfoUpdate object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiSwitchWalletNeed) name:kNotiSwitchWalletNeed object:nil];
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}


#pragma mark -  初始化UI
- (void)stupUI
{
    [self.topView setLayerDefaultRadius];
    [self.bottomView setLayerDefaultRadius];
    [self.leftViewBg setLayerRadius:14];
    [self.hwBgView setLayerRadius:15];
    [self.scanBtn addTarget:self action:@selector(scanBtnClick) forControlEvents:UIControlEventTouchUpInside];
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapGestureClick)];
    [self.leftView addGestureRecognizer:tapGesture];
    self.navigationController.delegate = self;

    [self.coinImage addTarget:self action:@selector(tapGestureClick) forControlEvents:UIControlEventTouchUpInside];

    //asset界面
    [self.walletTopBgView setCornerWith:20 side:OKCornerPathTopLeft|OKCornerPathTopRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 168)];
    [self.srBgView setCornerWith:20 side:OKCornerPathBottomLeft|OKCornerPathBottomRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 68)];
    [self.assettableViewHeader setCornerWith:20 side:OKCornerPathTopLeft|OKCornerPathTopRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 74)];
    [self.footerViewlow setCornerWith:20 side:OKCornerPathBottomLeft|OKCornerPathBottomRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 74)];
    [self.backupBgView setLayerDefaultRadius];
    [self.tableViewHeaderSearch setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:self.tableViewHeaderSearch.height * 0.5];
    self.assetTableView.rowHeight = 75;

    _refreshControl = [[UIRefreshControl alloc]init];
    _refreshControl.tintColor = [UIColor lightGrayColor];
    _refreshControl.attributedTitle = [[NSAttributedString alloc]initWithString:@""];
    [_refreshControl addTarget:self action:@selector(onRefresh) forControlEvents:UIControlEventValueChanged];
    self.assetTableView.refreshControl = _refreshControl;

    if (@available(iOS 11.0, *)) {
        [self.stackView setCustomSpacing:0 afterView:self.walletTopBgView];
    } else {
        // Fallback on earlier versions
    }


    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(backUpBgClick)];
    [self.backupBgView addGestureRecognizer:tap];


    UITapGestureRecognizer *tapWalletTopBgView = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapWalletTopBgViewClick)];

    [self.walletTopBgView addGestureRecognizer:tapWalletTopBgView];

    UITapGestureRecognizer *tapeye = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapEyeClick)];
    self.eyeBtn.userInteractionEnabled = YES;
    [self.eyebgView addGestureRecognizer:tapeye];
    [self changeEye];

    UITapGestureRecognizer *tapBanner = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapBanner)];
    self.bannerImageView.userInteractionEnabled = YES;
    [self.bannerImageView addGestureRecognizer:tapBanner];
}
@end
