//
//  OKFiatSelectViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFiatSelectViewController.h"
#import "OKUnitTableViewCell.h"
#import "OKUnitTableViewCellModel.h"

@interface OKFiatSelectViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic,strong)NSArray *allData;
@end


@implementation OKFiatSelectViewController

+ (instancetype)fiatSelectViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKFiatSelectViewController"];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Legal tender units", nil);
    self.tableView.tableFooterView = [UIView new];
    
    NSArray *allData = [kPyCommandsManager callInterface:kInterfaceGet_currencies parameter:@{}];
    self.allData = allData;
}

#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.allData.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKUnitTableViewCell";
    OKUnitTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKUnitTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    NSString *typeString = self.allData[indexPath.row];
    OKUnitTableViewCellModel *model = [OKUnitTableViewCellModel new];
    model.typeString = typeString;
    model.titleStr = typeString;
    model.type = GroupTypeFait;
    cell.model = model;
    return  cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *typeString = self.allData[indexPath.row];
    [kPyCommandsManager callInterface:kInterfaceSet_currency parameter:@{@"ccy":typeString}];
    [kWalletManager setCurrentFiat:typeString];
    [self.tableView reloadData];
}

@end
