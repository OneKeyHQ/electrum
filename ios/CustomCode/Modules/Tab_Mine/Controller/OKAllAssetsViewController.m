//
//  OKAllAssetsViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKAllAssetsViewController.h"
#import "OKAllAssetsTableViewCell.h"
#import "OKAllAssetsTableViewCellModel.h"

@interface OKAllAssetsViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UIView *searchBgView;
@property (weak, nonatomic) IBOutlet UITextField *searchTextField;

@property (nonatomic,strong)NSArray *allData;


@end

@implementation OKAllAssetsViewController

+ (instancetype)allAssetsViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKAllAssetsViewController"];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = MyLocalizedString(@"All assets", nil);
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = [UIView new];
    [self.searchBgView setLayerRadius:20];
    [self loadListData];
}

- (void)loadListData
{
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceget_all_wallet_balance parameter:@{}];
    NSString *all_balance = [dict safeStringForKey:@"all_balance"];
    NSArray *wallet_info = dict[@"wallet_info"];
    self.allData  = [OKAllAssetsTableViewCellModel mj_objectArrayWithKeyValuesArray:wallet_info];
    self.balanceLabel.text = all_balance;
    [self.tableView reloadData];
}



#pragma mark - UITableViewDataSource ,UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.allData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID  = @"OKAllAssetsTableViewCell";
    OKAllAssetsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAllAssetsTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 72;
}

@end
