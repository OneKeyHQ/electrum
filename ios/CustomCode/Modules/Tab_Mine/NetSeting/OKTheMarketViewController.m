//
//  OKTheMarketViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/23.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKTheMarketViewController.h"
#import "OKNetTableViewCell.h"
#import "OKNetTableViewCellModel.h"

@interface OKTheMarketViewController ()<UITableViewDataSource,UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UILabel *topDescLabel;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic,strong)NSArray *allData;

@end

@implementation OKTheMarketViewController

+ (instancetype)theMarketViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKTheMarketViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = MyLocalizedString(@"Price quotation", nil);
    self.topDescLabel.text  = MyLocalizedString(@"Choosing the right market source only affects the PRICE display of BTC and ETH", nil);
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = [UIView new];
    
    self.allData =  [kPyCommandsManager callInterface:kInterfaceget_exchanges parameter:@{}];
    [self.tableView reloadData];
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.allData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKNetTableViewCell";
    OKNetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKNetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKNetTableViewCellModel *model = [OKNetTableViewCellModel new];
    model.titleStr = self.allData[indexPath.row];
    model.type = OKNetTableViewCellModelTypeM;
    cell.model = model;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 72;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *indexStr = self.allData[indexPath.row];
    if (![indexStr isEqualToString:kUserSettingManager.currentMarketSource]) {
        [kUserSettingManager setCurrentMarketSource:indexStr];
        [kPyCommandsManager callInterface:kInterfaceset_exchange parameter:@{@"exchange":indexStr}];
        [[NSNotificationCenter defaultCenter]postNotificationName:kUserSetingMarketSource object:nil];
        [self.tableView reloadData];
    }
}

@end
