//
//  OKSelectImportTypeViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSelectImportTypeViewController.h"
#import "OKSelectCoinTypeTableViewCell.h"
#import "OKSelectCoinTypeTableViewCellModel.h"

//四种导入方式
#import "OKPrivateImportViewController.h"
#import "OKMnemonicImportViewController.h"
#import "OKKeystoreImportViewController.h"
#import "OKObserveImportViewController.h"

@interface OKSelectImportTypeViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *coinTypeListArray;

@end

@implementation OKSelectImportTypeViewController

+ (instancetype)selectImportTypeViewController
{
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSelectImportTypeViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"Import a single currency wallet", nil);
    self.tableView.tableFooterView = [UIView new];
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
    switch (indexPath.row) {
        case 0:
        {
            OKPrivateImportViewController *privateImportVc = [OKPrivateImportViewController privateImportViewController];
            privateImportVc.importType = OKAddTypeImportPrivkeys;
            [self.navigationController pushViewController:privateImportVc animated:YES];
        }
            break;
        case 1:
        {
            OKMnemonicImportViewController *mnemonicImportVc = [OKMnemonicImportViewController mnemonicImportViewController];
            mnemonicImportVc.importType = OKAddTypeImportSeed;
            [self.navigationController pushViewController:mnemonicImportVc animated:YES];
        }
            break;
//        case 2:
//        {
//            OKKeystoreImportViewController *keystoreImportVc = [OKKeystoreImportViewController keystoreImportViewController];
//            keystoreImportVc.importType = OKAddTypeImportKeystore;
//            [self.navigationController pushViewController:keystoreImportVc animated:YES];
//        }
//            break;
        case 2:
        {
            OKObserveImportViewController *observeImportVc = [OKObserveImportViewController observeImportViewController];
            observeImportVc.importType = OKAddTypeImportAddresses;
            [self.navigationController pushViewController:observeImportVc animated:YES];
        }
            break;
            
        default:
            break;
    }
}

- (NSArray *)coinTypeListArray
{
    if (!_coinTypeListArray) {
        
        OKSelectCoinTypeTableViewCellModel *model = [OKSelectCoinTypeTableViewCellModel new];
        model.titleString = MyLocalizedString(@"Private key import (direct input or scan)", nil);
        model.iconName = @"private_key_import";
        
        OKSelectCoinTypeTableViewCellModel *model1 = [OKSelectCoinTypeTableViewCellModel new];
        model1.titleString = MyLocalizedString(@"Mnemonic import", nil);
        model1.iconName = @"memo_import";
        
//        OKSelectCoinTypeTableViewCellModel *model2 = [OKSelectCoinTypeTableViewCellModel new];
//        model2.titleString = MyLocalizedString(@"Keystore import", nil);
//        model2.iconName = @"keystore_import";
        
        OKSelectCoinTypeTableViewCellModel *model3 = [OKSelectCoinTypeTableViewCellModel new];
        model3.titleString = MyLocalizedString(@"Observe the purse", nil);
        model3.iconName = @"watch_only_wallet";
        
        _coinTypeListArray = @[model,model1,model3];
    }
    return _coinTypeListArray;
}

@end
