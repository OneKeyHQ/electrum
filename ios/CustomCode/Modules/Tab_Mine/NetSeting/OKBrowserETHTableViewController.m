//
//  OKBrowserethTableViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKBrowserETHTableViewController.h"
#import "OKNetTableViewCell.h"
#import "OKNetTableViewCellModel.h"

@interface OKBrowserETHTableViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *topDescLabel;

@end

@implementation OKBrowserETHTableViewController

+ (instancetype)browserETHTableViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBrowserETHTableViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"Block Browser (ETH)", nil);
    self.topDescLabel.text = MyLocalizedString(@"Block Browser (ETH) USES your favorite browser to view transactions on the chain",nil);
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return kUserSettingManager.ethBrowserList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKNetTableViewCell";
    OKNetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKNetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKNetTableViewCellModel *model = [OKNetTableViewCellModel new];
    model.titleStr = kUserSettingManager.ethBrowserList[indexPath.row];
    model.type = OKNetTableViewCellModelTypeB;
    model.typeB = OKNetTableViewCellModelTypeBEth;
    cell.model = model;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 74;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *indexStr = kUserSettingManager.ethBrowserList[indexPath.row];
    if (![indexStr isEqualToString:kUserSettingManager.currentEthBrowser]) {
        [kUserSettingManager setCurrentEthBrowser:indexStr];
        [[NSNotificationCenter defaultCenter]postNotificationName:kUserSetingEthBComplete object:nil];
        [self.tableView reloadData];
    }
}
@end
