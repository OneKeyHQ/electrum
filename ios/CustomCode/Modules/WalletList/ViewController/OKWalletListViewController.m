//
//  OKWalletListViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
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
    NSString *coinType = [OKStorageManager loadFromUserDefaults:kSelectedWalletListType];
    if (coinType.length == 0 || coinType == nil) {
        [OKStorageManager saveToUserDefaults:kDefaultType key:kSelectedWalletListType];
    }
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
        model.walletTypeShowStr = [kWalletManager getWalletTypeShowStr:model.walletType];
        model.address = [innerDict safeStringForKey:@"addr"];
        model.backColor = [OKWalletListTableViewCellModel getBackColor:model.walletType];
        model.iconName = [OKWalletListTableViewCellModel getBgImageName:model.walletType];
        model.isCurrent = [kWalletManager.currentWalletName isEqualToString:model.walletName];
        [walletArray addObject:model];
    }
    self.walletListArray = walletArray;
    NSString *walletType =  [OKStorageManager loadFromUserDefaults:kSelectedWalletListType];
    if ([walletType isEqualToString:kDefaultType]) {
        self.detailBtn.hidden = NO;
        self.detailLabel.hidden = NO;
        self.circlePlusBtn.hidden = YES;
    }else{
        self.detailBtn.hidden = YES;
        self.detailLabel.hidden = YES;
        self.circlePlusBtn.hidden = NO;
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"walletType contains %@",[walletType lowercaseString]];
    self.showList = [self.walletListArray filteredArrayUsingPredicate:predicate];
    
    self.countLabel.text = [NSString stringWithFormat:@"%zd",self.showList.count];
    self.headerWalletTypeLabel.text = [self headerWalletType:walletType];
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
    return self.showList.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 90;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
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
    OKWalletListTableViewCellModel *model = self.showList[indexPath.row];
    [OKStorageManager saveToUserDefaults:model.walletName key:kCurrentWalletName];
    [OKStorageManager saveToUserDefaults:model.address key:kCurrentWalletAddress];
    [OKStorageManager saveToUserDefaults:model.walletType key:kCurrentWalletType];
    [kPyCommandsManager callInterface:kInterfaceSelect_wallet parameter:@{@"name":kWalletManager.currentWalletName}];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSelectWalletComplete object:nil];
    [self refreshListData];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)macthWalletBtnClick:(OKWalletListBottomBtn *)sender {
    NSLog(@"macthWalletBtnClick");
}

- (IBAction)addWalletBtnClick:(OKWalletListBottomBtn *)sender {
    
    OKWeakSelf(self);
    OKAddBottomViewController *vc = [OKAddBottomViewController initViewControllerWithStoryboardName:@"WalletList"];
    [vc showOnWindowWithParentViewController:self block:^(BtnClickType type) {
        if (type == BtnClickTypeCreate) {
            OKCreateSelectWalletTypeController *createSelectWalletTypeVc = [OKCreateSelectWalletTypeController createSelectWalletTypeController];
            [weakself.navigationController pushViewController:createSelectWalletTypeVc animated:YES];
        }else if (type == BtnClickTypeImport){
            OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
            selectVc.addType = OKAddTypeImport;
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

#pragma mark - 点击问号提示
- (IBAction)tipsBtnClick:(UIButton *)sender {
    OKTipsViewController *tipsVc = [OKTipsViewController tipsViewController];
    tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self presentViewController:tipsVc animated:NO completion:nil];
}

- (IBAction)circlePlusBtnClick:(UIButton *)sender {
    OKCreateSelectWalletTypeController *createSelectVc = [OKCreateSelectWalletTypeController createSelectWalletTypeController];
    [self.navigationController pushViewController:createSelectVc animated:YES];
}
#pragma mark - 点击详情
- (IBAction)detailBtnClick:(UIButton *)sender {
    OKWalletDetailViewController *walletDetail = [OKWalletDetailViewController walletDetailViewController];
    [self.navigationController pushViewController:walletDetail animated:YES];
}

#pragma mark - 点击添加钱包
- (IBAction)addWalletClick:(UIButton *)sender {
    OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
    selectVc.addType = OKAddTypeCreateHDDerived;
    [self.navigationController pushViewController:selectVc animated:YES];
}
@end
