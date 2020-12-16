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
#import "OKFindFollowingWalletController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKMatchingInCirclesViewController.h"
#import "OKTakeCareMnemonicViewController.h"


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

@property (weak, nonatomic) IBOutlet UITableView *assetTableView;


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

@property (nonatomic,strong)NSArray *listWallets;

@property (nonatomic,strong)OKAssetTableViewCellModel *model;

@property (nonatomic,assign)BOOL isCanSideBack;

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
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiWalletCreateComplete:) name:kNotiWalletCreateComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiSelectWalletComplete) name:kNotiSelectWalletComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiUpdate_status:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiDeleteWalletComplete) name:kNotiDeleteWalletComplete object:nil];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiBackUPWalletComplete) name:kNotiBackUPWalletComplete object:nil];
    
    [OKPyCommandsManager sharedInstance];
    
    [kPyCommandsManager callInterface:kInterfaceLoad_all_wallet parameter:@{}];

    [self loadWalletList];
    [self refreshUI];
    [self setDefault];
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
- (void)setDefault
{
    //设置默认法币
    if (kWalletManager.currentFiat == nil || kWalletManager.currentFiat.length == 0) {
        [kWalletManager setCurrentFiat:@"CNY"];
        [kWalletManager setCurrentFiatSymbol:kWalletManager.supportFiatsSymbol[0]];
        [kPyCommandsManager callInterface:kInterfaceSet_currency parameter:@{@"ccy":@"CNY"}];
    }else{
        [kPyCommandsManager callInterface:kInterfaceSet_currency parameter:@{@"ccy":kWalletManager.currentFiat}];
    }
    
    //设置默认BTC单位
    if (kWalletManager.currentBitcoinUnit == nil || kWalletManager.currentBitcoinUnit.length == 0) {
        [kWalletManager setCurrentBitcoinUnit:@"BTC"];
        [kPyCommandsManager callInterface:kInterfaceSet_base_uint parameter:@{@"base_unit":@"BTC"}];
    }else{
        [kPyCommandsManager callInterface:kInterfaceSet_base_uint parameter:@{@"base_unit":kWalletManager.currentBitcoinUnit}];
    }
    
    
    //设置默认的浏览器
    if (kUserSettingManager.currentBtcBrowser == nil || kUserSettingManager.currentBtcBrowser.length == 0) {
        [kUserSettingManager setCurrentBtcBrowser:kUserSettingManager.btcBrowserList.firstObject];
    }
    

    [kPyCommandsManager callInterface:kInterfaceset_rbf parameter:@{@"status_rbf":@"1"}];
    [kPyCommandsManager callInterface:kInterfaceset_unconf parameter:@{@"x":@"1"}];
    [kUserSettingManager setUnconfFlag:YES];
    [kUserSettingManager setRbfFlag:YES];
    
    
    if (kUserSettingManager.currentMarketSource == nil || kUserSettingManager.currentMarketSource.length == 0) {
        NSArray *marketSource =  [kPyCommandsManager callInterface:kInterfaceget_exchanges parameter:@{}];
        NSString *first =  marketSource.firstObject;
        [kUserSettingManager setCurrentMarketSource:first];
        [kPyCommandsManager callInterface:kInterfaceset_exchange parameter:@{@"exchange":first}];
    }
    
    if (kUserSettingManager.electrum_server == nil || kUserSettingManager.electrum_server.length == 0) {
       NSDictionary *dict =   [kPyCommandsManager callInterface:kInterfaceget_default_server parameter:@{}];
        if (dict != nil) {
            NSString *electrumNode = [NSString stringWithFormat:@"%@:%@",[dict safeStringForKey:@"host"],[dict safeStringForKey:@"port"]];
            [kUserSettingManager setElectrum_server:electrumNode];
        }
    }
    
}

- (void)selectWallet
{
    NSString *name = kWalletManager.currentWalletInfo.name;
    if ((name == nil || name.length == 0) && self.listWallets.count > 0 ) {
        NSDictionary *dict = [self.listWallets lastObject];
        name = [[dict allKeys] firstObject];
        NSDictionary *value = dict[name];
        [kWalletManager setCurrentWalletInfo:[OKWalletInfoModel mj_objectWithKeyValues:value]];
    }
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceSelect_wallet parameter:@{@"name":kWalletManager.currentWalletInfo.name}];
    NSString *b = [dict safeStringForKey:@"balance"];
    NSString *balance = [[b componentsSeparatedByString:@" "]firstObject];
    
    NSString *fiatS =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":balance}];
    [self updateStatus:@{@"balance":balance,@"fiat":fiatS}];
}

- (void)updateStatus:(NSDictionary *)dict
{
    NSLog(@"dict = %@",dict);
    self.model.balance = [dict safeStringForKey:@"balance"];
    self.model.coinType = kWalletManager.currentWalletInfo.coinType;
    self.model.iconImage = [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]];
    self.model.money = [dict safeStringForKey:@"fiat"];
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        NSArray *barray = [self.model.money componentsSeparatedByString:@" "];
        NSString *bStr = [NSString stringWithFormat:@"%@ %@",kWalletManager.currentFiatSymbol,[barray firstObject]];
        if (self.model.money.length == 0) {
            bStr = @"--";
        }
        if (kWalletManager.showAsset) {
            bStr = @"****";
        }
        self.balance.text = bStr;
        self.walletName.text = kWalletManager.currentWalletInfo.label.length > 0 ? kWalletManager.currentWalletInfo.label : MyLocalizedString(@"No purse", nil);
        if (kWalletManager.currentWalletInfo.label.length > 0) {
            [self.coinImage setImage:[UIImage imageNamed:self.model.iconImage] forState:UIControlStateNormal];
        }else{
            [self.coinImage setImage:[UIImage imageNamed:@"loco_round"] forState:UIControlStateNormal];
        }

        [self.assetTableView reloadData];
    });
}

#pragma mark -  初始化UI
- (void)stupUI
{
    self.model = [OKAssetTableViewCellModel new];
    [self.topView setLayerDefaultRadius];
    [self.bottomView setLayerDefaultRadius];
    [self.leftViewBg setLayerRadius:14];
    [self.scanBtn addTarget:self action:@selector(scanBtnClick) forControlEvents:UIControlEventTouchUpInside];
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapGestureClick)];
    [self.leftView addGestureRecognizer:tapGesture];
    self.navigationController.delegate = self;
    
    [self.coinImage addTarget:self action:@selector(tapGestureClick) forControlEvents:UIControlEventTouchUpInside];
    
    //asset界面
    [self.walletTopBgView setCornerWith:20 side:OKCornerPathTopLeft|OKCornerPathTopRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 168)];
    [self.srBgView setCornerWith:20 side:OKCornerPathBottomLeft|OKCornerPathBottomRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 68)];
    [self.assettableViewHeader setCornerWith:20 side:OKCornerPathTopLeft|OKCornerPathTopRight withSize:CGSizeMake(SCREEN_WIDTH - 40, 74)];
    [self.backupBgView setLayerDefaultRadius];
    [self.tableViewHeaderSearch setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:self.tableViewHeaderSearch.height * 0.5];
    self.assetTableView.rowHeight = 75;
    if (@available(iOS 11.0, *)) {
        [self.stackView setCustomSpacing:0 afterView:self.walletTopBgView];
    } else {
        // Fallback on earlier versions
    }
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(backUpBgClick)];
    [self.backupBgView addGestureRecognizer:tap];
    self.assetTableView.tableFooterView = [UIView new];
    
    
    UITapGestureRecognizer *tapWalletTopBgView = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapWalletTopBgViewClick)];
    
    [self.walletTopBgView addGestureRecognizer:tapWalletTopBgView];
    
    UITapGestureRecognizer *tapeye = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapEyeClick)];
    self.eyeBtn.userInteractionEnabled = YES;
    [self.eyebgView addGestureRecognizer:tapeye];
    [self changeEye];
}
#pragma mark - 检查钱包状态并重置UI
- (void)checkWalletResetUI
{
    [self loadWalletList];
    [self refreshUI];
}
- (void)loadWalletList
{
    self.listWallets = [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    if (self.listWallets.count > 0) {
        [self selectWallet];
        self.scanBtn.hidden = NO;
    }else{
        self.scanBtn.hidden = YES;
        dispatch_async(dispatch_get_main_queue(), ^{
            self.walletName.text = MyLocalizedString(@"No purse", nil);
            [self.coinImage setImage:[UIImage imageNamed:@"loco_round"] forState:UIControlStateNormal];
        });
    }
}
- (void)refreshUI
{
    BOOL isBackUp = YES;
    //是否创建过钱包
    if (self.listWallets.count > 0) { //创建过
        self.createBgView.hidden = YES;
        self.walletHomeBgView.hidden = NO;
        isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":kWalletManager.currentWalletInfo.name}] boolValue];
    }else{
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
        NSString *money =  [[self.model.money componentsSeparatedByString:@" "] firstObject];
        NSString *bStr = [NSString stringWithFormat:@"%@ %@",kWalletManager.currentFiatSymbol,money];
        if (self.model.money.length == 0) {
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

#pragma mark - 切换钱包
- (void)tapGestureClick
{
    OKWalletListViewController *walletListVc = [OKWalletListViewController walletListViewController];
    BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:walletListVc];
    [self.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
}
#pragma mark - 扫描二维码
- (void)scanBtnClick
{
    OKWeakSelf(self)
    OKWalletScanVC *vc = [OKWalletScanVC initViewControllerWithStoryboardName:@"Scan"];
    vc.scanningType = ScanningTypeAddress;
    vc.scanningCompleteBlock = ^(id result) {
        if (result) {
            NSDictionary *typeDict = [kPyCommandsManager callInterface:kInterfaceparse_pr parameter:@{@"data":result}];
            if (typeDict != nil) {
                NSInteger type = [typeDict[@"type"] integerValue];
                switch (type) {
                    case 1:
                    {
                        NSDictionary *data = typeDict[@"data"];
                        NSString *address = [data safeStringForKey:@"address"];
                        OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
                        sendCoinVc.address = address;
                        [weakself.navigationController pushViewController:sendCoinVc animated:YES];
                    }
                        break;
                    case 2:
                    {
//                        NSDictionary *data = typeDict[@"data"];
//                        OKTxDetailViewController *txDetail = [OKTxDetailViewController txDetailViewController];
//                        txDetail.tx_hash = [data safeStringForKey:@"txid"];
//                        [weakself.navigationController pushViewController:txDetail animated:YES];
                    }
                        break;
                    default:
                        [kTools tipMessage:result];
                        break;
                }
            }else{
                [kTools tipMessage:result];
            }
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
    return  1;
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
    
    //assetTableView
    static NSString *ID = @"OKAssetTableViewCell";
    OKAssetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAssetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.model;
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
                               [weakself createWallet:pwd];
                           }];
                       } else if (state == YZAuthIDStateSuccess) {
                           NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                           [weakself createWallet:pwd];
                       }
                   }];
               }else{
                   [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
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
        }else if (model.type == OKSelectCellTypeRestoreHD){ //恢复
            OKWordImportVC *wordImport = [OKWordImportVC initViewController];
            BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:wordImport];
            [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
        }else if (model.type == OKSelectCellTypeMatchHD){ //匹配硬件
            [kTools tipMessage:MyLocalizedString(@"Temporary does not support", nil)];
//            OKMatchingInCirclesViewController *matchVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
//            [self.navigationController pushViewController:matchVc animated:YES];
        }
        return;
    }
    OKTxListViewController *txListVc = [OKTxListViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    txListVc.model = self.model;
    txListVc.coinType = [kWalletManager.currentWalletInfo.coinType uppercaseString];
    [self.navigationController pushViewController:txListVc animated:YES];
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
        OKWalletInfoModel *walletInfoModel = [kWalletManager getCurrentWalletAddress:model.name];
        [kWalletManager setCurrentWalletInfo:walletInfoModel];
        if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
            [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
        }
        OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
        }];
        [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
    }
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (scrollView == self.assetTableView) {
        CGFloat offY = scrollView.contentOffset.y;
        if (offY < 0) {
            scrollView.contentOffset = CGPointZero;
        }
    }
}

- (NSArray *)allData
{
    if (!_allData) {
        _allData = [NSArray array];
        OKSelectCellModel *model1 = [OKSelectCellModel new];
        model1.titleStr = MyLocalizedString(@"Create HD Wallet", nil);
        model1.descStr = MyLocalizedString(@"Completely free and unlimited quantity", nil);
        model1.imageStr = @"add";
        model1.descStrL = MyLocalizedString(@"It takes just a few minutes to create a wallet for free and quickly, and then you're free to send and receive assets, make transactions, and explore the blockchain world", nil);
        model1.type = OKSelectCellTypeCreateHD;
        
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
    OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
    [self.navigationController pushViewController:sendCoinVc animated:YES];
}
#pragma mark - 收款
- (IBAction)receiveBtnClick:(UIButton *)sender {
    OKReceiveCoinViewController *receiveCoinVc = [OKReceiveCoinViewController receiveCoinViewController];
    receiveCoinVc.coinType = COIN_BTC;
    [self.navigationController pushViewController:receiveCoinVc animated:YES];
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
#pragma mark - 添加资产
- (IBAction)tableViewHeaderAddBtnClick:(UIButton *)sender {
    NSLog(@"添加资产");
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
    [self updateStatus:infoDic];
}

- (void)notiDeleteWalletComplete
{
    [self checkWalletResetUI];
}

- (void)notiBackUPWalletComplete
{
    if (kWalletManager.isOpenAuthBiological) {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    [self checkWalletResetUI];
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
@end
