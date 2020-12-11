//
//  OKWalletListViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletListViewController.h"
#import "OKWalletListTableViewCell.h"
#import "OKWalletListTableViewCellModel.h"
#import "OKWalletListBottomBtn.h"
#import "OKWalletListCollectionViewCell.h"
#import "OKWalletListCollectionViewCellModel.h"
#import "OKSelectCoinTypeViewController.h"
#import "OKAddBottomViewController.h"
#import "OKCreateSelectWalletTypeController.h"
#import "OKWalletDetailViewController.h"
#import "OKTipsViewController.h"
#import "OKCreateSelectWalletTypeController.h"
#import "OKWalletListNoHDTableViewCellModel.h"
#import "OKWalletListNoHDTableViewCell.h"
#import "OKPwdViewController.h"
#import "OKWordImportVC.h"
#import "OKHDWalletViewController.h"
#import "OKBiologicalViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKMatchingInCirclesViewController.h"


#define kDefaultType  @"HD"

@interface OKWalletListViewController ()<UITableViewDelegate,UITableViewDataSource,UICollectionViewDelegate,UICollectionViewDataSource>

@property (weak, nonatomic) IBOutlet UIView *bottomBgView;
@property (weak, nonatomic) IBOutlet OKWalletListBottomBtn *macthWalletBtn;
- (IBAction)macthWalletBtnClick:(OKWalletListBottomBtn *)sender;
@property (weak, nonatomic) IBOutlet OKWalletListBottomBtn *addWalletBtn;
- (IBAction)addWalletBtnClick:(OKWalletListBottomBtn *)sender;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UICollectionView *leftCollectionView;

@property (nonatomic,strong)NSArray *allCoinTypeArray;
@property (nonatomic,strong)NSArray* walletListArray;
@property (nonatomic,strong)NSArray *showList;

@property (nonatomic,strong)NSArray *NoHDArray;

//tableViewHeaderView
@property (weak, nonatomic) IBOutlet UILabel *headerWalletTypeLabel;
@property (weak, nonatomic) IBOutlet UIButton *tipsBtn;
@property (weak, nonatomic) IBOutlet UIButton *circlePlusBtn;
- (IBAction)circlePlusBtnClick:(UIButton *)sender;
- (IBAction)tipsBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *countBgView;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (weak, nonatomic) IBOutlet UIButton *detailBtn;
- (IBAction)detailBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
//tableViewFooterView
@property (weak, nonatomic) IBOutlet UIView *footBgView;
@property (weak, nonatomic) IBOutlet UILabel *footerTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *footerDescLabel;
- (IBAction)addWalletClick:(UIButton *)sender;

@property (nonatomic,assign)BOOL haveHD;

@end

@implementation OKWalletListViewController

+ (instancetype)walletListViewController
{
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil]instantiateViewControllerWithIdentifier:@"OKWalletListViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiRefreshWalletList) name:kNotiRefreshWalletList object:nil];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"The wallet list", nil);
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemWithImage:[UIImage imageNamed:@"close_dark_small"] frame:CGRectMake(0, 0, 40, 40) target:self selector:@selector(rightBarBtnClick)];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.macthWalletBtn.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.addWalletBtn.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.leftCollectionView.delegate = self;
    self.leftCollectionView.dataSource = self;
    [self.countBgView setLayerRadius:10];
    [self.footBgView setLayerDefaultRadius];
    [self.footBgView setLayerBoarderColor:HexColorA(0x546370, 0.3) width:1 radius:20];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self refreshListData];
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
        if ([model.walletType containsString:[kDefaultType lowercaseString]]) {
            _haveHD = YES;
        }
        model.walletTypeShowStr = [kWalletManager getWalletTypeShowStr:model.walletType];
        model.address = [innerDict safeStringForKey:@"addr"];
        model.label = [innerDict safeStringForKey:@"label"];
        model.backColor = [OKWalletListTableViewCellModel getBackColor:model.walletType];
        model.iconName = [OKWalletListTableViewCellModel getBgImageName:model.walletType];
        model.isCurrent = [kWalletManager.currentWalletInfo.name isEqualToString:model.walletName];
        [walletArray addObject:model];
    }
    self.walletListArray = walletArray;
    NSString *walletType =  [OKStorageManager loadFromUserDefaults:kSelectedWalletListType];
    if (walletType.length == 0 || walletType == nil) {
        [OKStorageManager saveToUserDefaults:kDefaultType key:kSelectedWalletListType];
        walletType = kDefaultType;
    }
    NSPredicate *predicate  = nil;
    if ([walletType isEqualToString:kDefaultType]) {
        predicate = [NSPredicate predicateWithFormat:@"walletType contains %@ || walletType contains %@",[walletType lowercaseString],[@"derived-standard" lowercaseString]];
    }else{
        predicate = [NSPredicate predicateWithFormat:@"walletType contains %@",[walletType lowercaseString]];
    }
    self.showList = [self.walletListArray filteredArrayUsingPredicate:predicate];
    
    self.countLabel.text = [NSString stringWithFormat:@"%zd",self.showList.count];
    self.headerWalletTypeLabel.text = [self headerWalletType:walletType];
    if([kWalletManager.currentWalletInfo.coinType isEqualToString:kDefaultType]){
        self.footBgView.hidden = self.showList.count == 0 ? YES : NO;
    }else{
        self.footBgView.hidden = YES;
    }
    
    if ([walletType isEqualToString:kDefaultType]) {
        self.detailBtn.hidden = self.showList.count== 0 ? YES:NO;
        self.detailLabel.hidden = self.showList.count== 0 ? YES:NO;
        self.circlePlusBtn.hidden = YES;
    }else{
        self.detailBtn.hidden = YES;
        self.detailLabel.hidden = YES;
        self.circlePlusBtn.hidden = NO;
    }
    [self.tableView reloadData];
}

- (NSString *)headerWalletType:(NSString *)walletType
{
    for (OKWalletListCollectionViewCellModel *model in self.allCoinTypeArray) {
        if ([walletType isEqualToString:model.coinType]) {
            return model.headerWaletType;
        }
    }
    return [[self.allCoinTypeArray firstObject]headerWaletType];
}

- (void)rightBarBtnClick
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)notiRefreshWalletList
{
    [self refreshListData];
}

#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if([kWalletManager.currentSelectCoinType isEqualToString:kDefaultType] && self.showList.count == 0){
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
    if([kWalletManager.currentSelectCoinType isEqualToString:kDefaultType] && self.showList.count == 0){
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
    if([kWalletManager.currentSelectCoinType isEqualToString:kDefaultType] && self.showList.count == 0){
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
                                       [OKWalletListViewController createWallet:pwd];
                                   }];
                               } else if (state == YZAuthIDStateSuccess) {
                                   NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                                   [OKWalletListViewController createWallet:pwd];
                               }
                           }];
                       }else{
                           [OKValidationPwdController showValidationPwdPageOn:weakself isDis:NO complete:^(NSString * _Nonnull pwd) {
                                [OKWalletListViewController createWallet:pwd];
                            }];
                       }
                    }else{
                        OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
                            [OKWalletListViewController createWallet:pwd];
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
    OKWalletListTableViewCellModel *model = self.showList[indexPath.row];
    OKWalletInfoModel *curentWalletModel= [kWalletManager getCurrentWalletAddress:model.walletName];
    [kWalletManager setCurrentWalletInfo:curentWalletModel];
    [kPyCommandsManager callInterface:kInterfaceSelect_wallet parameter:@{@"name":kWalletManager.currentWalletInfo.name}];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSelectWalletComplete object:nil];
    [self refreshListData];
    [self dismissViewControllerAnimated:YES completion:nil];
}

+ (void)createWallet:(NSString *)pwd
{
    NSString *seed = @"";
    NSString *createHD = @"";
    NSArray *words = [NSArray array];
    NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
    OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
    words = [createHD componentsSeparatedByString:@" "];
    if (words.count > 0) {
        OKCreateResultWalletInfoModel *createResultWalletInfoModel = [createResultModel.wallet_info firstObject];
        OKWalletInfoModel *infoModel = [kWalletManager getCurrentWalletAddress:createResultWalletInfoModel.name];
        [kWalletManager setCurrentWalletInfo:infoModel];
        if (!kWalletManager.isOpenAuthBiological) {
            OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
            }];
            [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
        }else{
            [self.OK_TopViewController dismissToViewControllerWithClassName:@"OKWalletViewController" animated:YES complete:^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
            }];
        }
    }
}

- (IBAction)macthWalletBtnClick:(OKWalletListBottomBtn *)sender {
    OKMatchingInCirclesViewController *matchVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
    [self.navigationController pushViewController:matchVc animated:YES];
}

- (IBAction)addWalletBtnClick:(OKWalletListBottomBtn *)sender {
    OKWeakSelf(self);
    OKAddBottomViewController *vc = [OKAddBottomViewController initViewControllerWithStoryboardName:@"WalletList"];
    [vc showOnWindowWithParentViewController:self block:^(BtnClickType type) {
        if (type == BtnClickTypeCreate) {
            OKCreateSelectWalletTypeController *createSelectWalletTypeVc = [OKCreateSelectWalletTypeController createSelectWalletTypeController];
            createSelectWalletTypeVc.haveHD = weakself.haveHD;
            [weakself.navigationController pushViewController:createSelectWalletTypeVc animated:YES];
        }else if (type == BtnClickTypeImport){
            OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
            selectVc.addType = OKAddTypeImport;
            selectVc.where = OKWhereToSelectTypeWalletList;
            [weakself.navigationController pushViewController:selectVc animated:YES];
        }
    }];
}

#pragma mark -collectionview 数据源方法
- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.allCoinTypeArray.count;
}
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    OKWalletListCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"OKWalletListCollectionViewCell" forIndexPath:indexPath];
    OKWalletListCollectionViewCellModel *model = self.allCoinTypeArray[indexPath.row];
    cell.model = model;
    return cell;
}

-(CGSize)collectionView:(UICollectionView*)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath*)indexPath
{
    NSInteger cellWidth = 64;
    return CGSizeMake(cellWidth,cellWidth);
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    int i = 0;
    for (OKWalletListCollectionViewCellModel *model in self.allCoinTypeArray) {
        if (indexPath.row == i) {
            model.isSelected = YES;
            [OKStorageManager saveToUserDefaults:model.coinType key:kSelectedWalletListType];
            [self refreshListData];
        }else{
            model.isSelected = NO;
        }
        i++;
    }
    [self.leftCollectionView reloadData];
}

- (NSArray *)allCoinTypeArray
{
    if (!_allCoinTypeArray) {
        
        OKWalletListCollectionViewCellModel *model0 = [OKWalletListCollectionViewCellModel new];
        model0.coinType = @"HD";
        model0.iconName = @"cointype_hd";
        model0.isSelected = [kWalletManager.currentSelectCoinType isEqualToString:model0.coinType];
        model0.headerWaletType = MyLocalizedString(@"HD wallet", nil);
        
        OKWalletListCollectionViewCellModel *model1 = [OKWalletListCollectionViewCellModel new];
        model1.coinType = @"BTC";
        model1.iconName = @"cointype_btc";
        model1.isSelected = [kWalletManager.currentSelectCoinType isEqualToString:model1.coinType];
        model1.headerWaletType = MyLocalizedString(@"BTC wallet", nil);
        
//        OKWalletListCollectionViewCellModel *model2 = [OKWalletListCollectionViewCellModel new];
//        model2.coinType = @"ETH";
//        model2.iconName = @"cointype_eth";
//        model2.isSelected = [kWalletManager.currentSelectCoinType isEqualToString:model2.coinType];
//        model2.headerWaletType = MyLocalizedString(@"ETH wallet", nil);
//        _allCoinTypeArray = @[model0,model1,model2];
         _allCoinTypeArray = @[model0,model1];
    }
    return _allCoinTypeArray;
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

#pragma mark - 点击问号提示
- (IBAction)tipsBtnClick:(UIButton *)sender {
    OKTipsViewController *tipsVc = [OKTipsViewController tipsViewController];
    tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self presentViewController:tipsVc animated:NO completion:nil];
}

- (IBAction)circlePlusBtnClick:(UIButton *)sender {
    OKCreateSelectWalletTypeController *createSelectVc = [OKCreateSelectWalletTypeController createSelectWalletTypeController];
    createSelectVc.haveHD = self.haveHD;
    [self.navigationController pushViewController:createSelectVc animated:YES];
}
#pragma mark - 点击详情
- (IBAction)detailBtnClick:(UIButton *)sender {
    [self detailClick];
}
- (IBAction)detailLabelClick:(UITapGestureRecognizer *)sender {
    [self detailClick];
}
- (void)detailClick
{
    OKHDWalletViewController *hdVc = [OKHDWalletViewController hdWalletViewController];
    [self.navigationController pushViewController:hdVc animated:YES];
}
#pragma mark - 点击添加钱包
- (IBAction)addWalletClick:(UIButton *)sender {
    OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
    selectVc.addType = OKAddTypeCreateHDDerived;
    selectVc.where = OKWhereToSelectTypeWalletList;
    [self.navigationController pushViewController:selectVc animated:YES];
}
@end
