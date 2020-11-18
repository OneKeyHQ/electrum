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
    [self loadList];
}

- (void)loadList
{
    NSArray *resultArray = [kPyCommandsManager callInterface:kInterfaceGet_all_tx_list parameter:@{@"search_type":self.searchType}];
    self.txListArray = [OKTxTableViewCellModel mj_objectArrayWithKeyValuesArray:resultArray];
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
- ( UISwipeActionsConfiguration *)tableView:(UITableView *)tableView trailingSwipeActionsConfigurationForRowAtIndexPath:(NSIndexPath *)indexPath {
    //删除
    UIContextualAction *deleteRowAction = [UIContextualAction contextualActionWithStyle:UIContextualActionStyleDestructive title:MyLocalizedString(@"delete", nil) handler:^(UIContextualAction * _Nonnull action, __kindof UIView * _Nonnull sourceView, void (^ _Nonnull completionHandler)(BOOL)) {
        OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
        [kPyCommandsManager callInterface:kInterfaceRemove_local_tx parameter:@{@"delete_tx":model.tx_hash}];
        completionHandler (YES);
        [self.tableView reloadData];
    }];
    UISwipeActionsConfiguration *config = [UISwipeActionsConfiguration configurationWithActions:@[deleteRowAction]];
    return config;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKTxTableViewCell";
    OKTxTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKTxTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
    cell.model = model;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
//    OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
//    [kPyCommandsManager callInterface:kInterfaceRemove_local_tx parameter:@{@"delete_tx":model.tx_hash}];
    OKTxTableViewCellModel *model = self.txListArray[indexPath.row];
    OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
    txDetailVc.tx_hash = model.tx_hash;
    txDetailVc.txDate = model.date;
    [self.navigationController pushViewController:txDetailVc animated:YES];
}
@end
