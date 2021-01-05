//
//  OKBrowserBTCTableViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKBrowserBTCTableViewController.h"
#import "OKNetTableViewCell.h"
#import "OKNetTableViewCellModel.h"

@interface OKBrowserBTCTableViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *topDescLabel;

@end

@implementation OKBrowserBTCTableViewController

+ (instancetype)browserBTCTableViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBrowserBTCTableViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"Block Browser (BTC)", nil);
    self.topDescLabel.text = MyLocalizedString(@"Block Browser (BTC) USES your favorite browser to view transactions on the chain",nil);
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return kUserSettingManager.btcBrowserList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKNetTableViewCell";
    OKNetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKNetTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKNetTableViewCellModel *model = [OKNetTableViewCellModel new];
    model.titleStr = kUserSettingManager.btcBrowserList[indexPath.row];
    model.type = OKNetTableViewCellModelTypeB;
    cell.model = model;
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 74;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *indexStr = kUserSettingManager.btcBrowserList[indexPath.row];
    if (![indexStr isEqualToString:kUserSettingManager.currentBtcBrowser]) {
        [kUserSettingManager setCurrentBtcBrowser:indexStr];
        [[NSNotificationCenter defaultCenter]postNotificationName:kUserSetingBtcBComplete object:nil];
        [self.tableView reloadData];
    }
}
@end
