//
//  OKTxViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
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
    cell.model = model;
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
    [self.navigationController pushViewController:txDetailVc animated:YES];
}



#pragma mark - txListArray
- (NSArray *)txListArray
{
    if (!_txListArray) {
        
        OKTxTableViewCellModel *model1 = [OKTxTableViewCellModel new];
        model1.timeStr = @"09/19 22:22";
        model1.amountStr = @"+5.9999";
        model1.addressStr = @"3Meddsfdsfafa";
        model1.imageStr = @"txin";
        
        OKTxTableViewCellModel *model2 = [OKTxTableViewCellModel new];
        model2.timeStr = @"09/05 11:11";
        model2.amountStr = @"+100.9999";
        model2.addressStr = @"3Meddsfdsfafa";
        model2.imageStr = @"txout";
        
        OKTxTableViewCellModel *model3 = [OKTxTableViewCellModel new];
        model3.timeStr = @"09/19 22:22";
        model3.amountStr = @"+5.9999";
        model3.addressStr = @"3Meddsfdsfafa";
        model3.imageStr = @"txin";
        
        OKTxTableViewCellModel *model4 = [OKTxTableViewCellModel new];
        model4.timeStr = @"09/05 11:11";
        model4.amountStr = @"+100.9999";
        model4.addressStr = @"3Meddsfdsfafa";
        model4.imageStr = @"txout";
        
        OKTxTableViewCellModel *model5 = [OKTxTableViewCellModel new];
        model5.timeStr = @"09/19 22:22";
        model5.amountStr = @"+5.9999";
        model5.addressStr = @"3Meddsfdsfafa";
        model5.imageStr = @"txin";
        model5.statusStr = @"发送失败";
        
        OKTxTableViewCellModel *model6 = [OKTxTableViewCellModel new];
        model6.timeStr = @"09/05 11:11";
        model6.amountStr = @"+100.9999";
        model6.addressStr = @"3Meddsfdsfafa";
        model6.imageStr = @"txout";
        
        _txListArray = @[model1,model2,model3,model4,model5,model6];
    }
    return _txListArray;
}
@end
