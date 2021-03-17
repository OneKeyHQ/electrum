//
//  OKTxViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTxViewController.h"
#import "OKTxTableViewCell.h"
#import "OKTxTableViewCellModel.h"
#import "OKTxDetailViewController.h"
#import "OKAssetTableViewCellModel.h"

@interface OKTxViewController ()<UITableViewDelegate,UITableViewDataSource>
+ (instancetype)txViewController;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic,strong)NSArray *txListArray;

@end

@implementation OKTxViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
}

- (void)stupUI
{
    self.tableView.tableFooterView = [UIView new];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(notiSendTxComplete) name:kNotiSendTxComplete object:nil];

    if ([[self.coinType uppercaseString]isEqualToString:COIN_BTC]) {
        [self loadList];
    }else{
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            [self loadList];
        });
    }
}

- (void)loadList
{
    OKWeakSelf(self)
    NSMutableDictionary *params = [@{@"search_type":self.searchType,@"coin":[weakself.coinType lowercaseString]} mutableCopy];
    if (weakself.assetTableViewCellModel.contract_addr.length > 0) {
        [params addEntriesFromDictionary:@{
            @"contract_address":weakself.assetTableViewCellModel.contract_addr
        }];
    }
    NSArray *resultArray = [kPyCommandsManager callInterface:kInterfaceGet_all_tx_list parameter:params];
    self.txListArray = [OKTxTableViewCellModel mj_objectArrayWithKeyValuesArray:resultArray];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}


+ (instancetype)txViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil] instantiateViewControllerWithIdentifier:@"OKTxViewController"];
}

#pragma  mark - TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.txListArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 68;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKTxTableViewCell";
    OKTxTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKTxTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
    if (self.assetTableViewCellModel.contract_addr.length > 0) {
        model.coinType = [NSString stringWithFormat:@"token_%@",self.coinType];
    }else{
        model.coinType = self.coinType;
    }
    cell.model = model;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
    OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
    txDetailVc.model = model;
    txDetailVc.tx_hash = model.tx_hash;
    txDetailVc.txDate = model.date;
    [self.navigationController pushViewController:txDetailVc animated:YES];
}

#pragma mark - notiSendTxComplete
- (void)notiSendTxComplete
{
    [self loadList];
}
@end
