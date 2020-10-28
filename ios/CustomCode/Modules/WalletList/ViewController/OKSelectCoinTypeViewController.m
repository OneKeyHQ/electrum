//
//  OKSelectCoinTypeViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKSelectCoinTypeViewController.h"
#import "OKSelectCoinTypeTableViewCell.h"
#import "OKSelectCoinTypeTableViewCellModel.h"
#import "OKSetWalletNameViewController.h"
#import "OKSelectImportTypeViewController.h"


@interface OKSelectCoinTypeViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *coinTypeListArray;

@end

@implementation OKSelectCoinTypeViewController

+ (instancetype)selectCoinTypeViewController
{
    return  [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSelectCoinTypeViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self stupUI];
    self.tableView.tableFooterView = [UIView new];
}

- (void)stupUI
{
    self.titleLabel.text = MyLocalizedString(@"Select the currency", nil);
    if (_addType == OKAddTypeCreateHD) {
        self.title = MyLocalizedString(@"Create a wallet", nil);
    }else if (_addType == OKAddTypeImport){
        self.title = MyLocalizedString(@"Import a single currency wallet", nil);
    }
    
}


#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.coinTypeListArray.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 74;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKSelectCoinTypeTableViewCell";
    OKSelectCoinTypeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKSelectCoinTypeTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.coinTypeListArray[indexPath.row];
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (_addType == OKAddTypeCreateHD || _addType == OKAddTypeCreateSolo) {
        OKSetWalletNameViewController *setWalletNameVc = [OKSetWalletNameViewController setWalletNameViewController];
        [self.navigationController pushViewController:setWalletNameVc animated:YES];
    }else if (_addType == OKAddTypeImport){
        OKSelectImportTypeViewController *selectImportTypeVc = [OKSelectImportTypeViewController selectImportTypeViewController];
        [self.navigationController pushViewController:selectImportTypeVc animated:YES];
    }
}


- (NSArray *)coinTypeListArray
{
    if (!_coinTypeListArray) {
        
        OKSelectCoinTypeTableViewCellModel *model = [OKSelectCoinTypeTableViewCellModel new];
        model.titleString = @"BTC";
        model.iconName = @"token_btc";
        
        OKSelectCoinTypeTableViewCellModel *model1 = [OKSelectCoinTypeTableViewCellModel new];
        model1.titleString = @"ETH";
        model1.iconName = @"token_eth";
        
        OKSelectCoinTypeTableViewCellModel *model2 = [OKSelectCoinTypeTableViewCellModel new];
        model2.titleString = @"EOS";
        model2.iconName = @"token_eos";
        
        
        _coinTypeListArray = @[model,model1,model2];
    
    }
    return _coinTypeListArray;
}
@end
