//
//  OKSelectCoinTypeViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
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
    if (_addType == OKAddTypeCreateHDDerived) {
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
    NSString *coinType = self.coinTypeListArray[indexPath.row];
    OKSelectCoinTypeTableViewCellModel *model = [OKSelectCoinTypeTableViewCellModel new];
    model.titleString = coinType;
    model.iconName = [NSString stringWithFormat:@"token_%@",[coinType lowercaseString]];
    cell.model = model;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *coinType = self.coinTypeListArray[indexPath.row];
    if (_addType == OKAddTypeCreateHDDerived || _addType == OKAddTypeCreateSolo) {
        OKSetWalletNameViewController *setWalletNameVc = [OKSetWalletNameViewController setWalletNameViewController];
        setWalletNameVc.addType = _addType;
        setWalletNameVc.coinType = coinType;
        setWalletNameVc.where = _where;
        [self.navigationController pushViewController:setWalletNameVc animated:YES];
    }else if (_addType == OKAddTypeImport){
        OKSelectImportTypeViewController *selectImportTypeVc = [OKSelectImportTypeViewController selectImportTypeViewController];
        [self.navigationController pushViewController:selectImportTypeVc animated:YES];
    }
}
-(NSArray *)coinTypeListArray
{
    return kWalletManager.supportCoinArray;
}
@end
