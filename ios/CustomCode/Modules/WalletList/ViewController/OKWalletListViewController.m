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


//tableViewHeaderView
@property (weak, nonatomic) IBOutlet UILabel *headerWalletTypeLabel;
@property (weak, nonatomic) IBOutlet UIButton *tipsBtn;
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
}

- (void)rightBarBtnClick
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.walletListArray.count;
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
    OKWalletListTableViewCellModel *model = self.walletListArray[indexPath.row];
    cell.model = model;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"选中一个钱包");
}

- (NSArray *)walletListArray
{
    if (!_walletListArray) {
        OKWalletListTableViewCellModel *model1 = [OKWalletListTableViewCellModel new];
        model1.walletName = @"BTC-1";
        model1.walletType = @"HD";
        model1.iconName = @"token_trans_btc";
        model1.address = @"3Lwosjkskskiaoakaksaa";
        
        OKWalletListTableViewCellModel *model2 = [OKWalletListTableViewCellModel new];
        model2.walletName = @"ETH-1";
        model2.walletType = @"HD";
        model2.iconName = @"token_trans_eth";
        model2.address = @"3Mwosjkskskiaoakaksaa";
        
        _walletListArray = @[model1,model2];
    }
    return _walletListArray;
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

- (NSArray *)allCoinTypeArray
{
    if (!_allCoinTypeArray) {
        OKWalletListCollectionViewCellModel *model0 = [OKWalletListCollectionViewCellModel new];
        model0.coinType = @"Logo";
        model0.iconName = @"hd_wallet-1";
        
        OKWalletListCollectionViewCellModel *model1 = [OKWalletListCollectionViewCellModel new];
        model1.coinType = @"BTC";
        model1.iconName = @"btc_icon_left";
        
        OKWalletListCollectionViewCellModel *model2 = [OKWalletListCollectionViewCellModel new];
        model2.coinType = @"ETH";
        model2.iconName = @"eth_icon_left";
        
        _allCoinTypeArray = @[model0,model1,model2];
    }
    return _allCoinTypeArray;;
}

#pragma mark - 点击问号提示
- (IBAction)tipsBtnClick:(UIButton *)sender {
    NSLog(@"点击了HD钱包的提示");
}
#pragma mark - 点击详情
- (IBAction)detailBtnClick:(UIButton *)sender {
    NSLog(@"点击了详情");
}

#pragma mark - 点击添加钱包
- (IBAction)addWalletClick:(UIButton *)sender {
    OKSelectCoinTypeViewController *selectVc = [OKSelectCoinTypeViewController selectCoinTypeViewController];
    selectVc.addType = OKAddTypeCreateHD;
    [self.navigationController pushViewController:selectVc animated:YES];
}
@end
