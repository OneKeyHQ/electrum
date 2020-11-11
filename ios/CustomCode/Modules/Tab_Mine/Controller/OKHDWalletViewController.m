//
//  OKHDWalletViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKHDWalletViewController.h"
#import "OKTipsViewController.h"
#import "OKWalletListTableViewCell.h"
#import "OKWalletListTableViewCellModel.h"
#import "OKManagerHDViewController.h"

@interface OKHDWalletViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (strong, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *headerTitleLabel;
- (IBAction)headerTipsBtnclick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;
@property (nonatomic,strong)NSArray *showList;
@property (weak, nonatomic) IBOutlet UIView *footerBgView;
@property (weak, nonatomic) IBOutlet UIView *countBgView;

@end

@implementation OKHDWalletViewController

+ (instancetype)hdWalletViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKHDWalletViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self refreshListData];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"HD wallet", nil);
    [self.countBgView setLayerRadius:10];
    [self.footerBgView setLayerDefaultRadius];
    
    NSString *labelText = MyLocalizedString(@"management", nil);
    CGFloat labelW = [labelText getWidthWithHeight:30 font:14];
    CGFloat labelmargin = 10;
    CGFloat labelH = 30;
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(labelmargin, 0, labelW, labelH)];
    label.text = labelText;
    label.font = [UIFont boldSystemFontOfSize:14];
    label.textColor = HexColor(0x26CF02);
    
    UIView *rightView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, labelW + labelmargin * 2, labelH)];
    rightView.backgroundColor = HexColorA(0x26CF02, 0.1);
    [rightView setLayerRadius:labelH * 0.5];
    [rightView addSubview:label];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:rightView];
    
    UITapGestureRecognizer *tapRightViewClick = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapRightViewClick)];
    [rightView addGestureRecognizer:tapRightViewClick];
}

- (void)tapRightViewClick
{
    OKManagerHDViewController *managerHDVc = [OKManagerHDViewController managerHDViewController];
    [self.navigationController pushViewController:managerHDVc animated:YES];
}


#pragma mark - 刷新UI
- (void)refreshListData
{
    NSArray *listDictArray =  [kPyCommandsManager callInterface:kInterfaceList_wallets parameter:@{}];
    NSMutableArray *walletArray = [NSMutableArray arrayWithCapacity:listDictArray.count];
    for (int i = 0; i < listDictArray.count; i++) {
        NSDictionary *outerModelDict = listDictArray[i];
        OKWalletListTableViewCellModel *model = [OKWalletListTableViewCellModel new];
        model.walletName = [outerModelDict allKeys].firstObject;
        NSDictionary *innerDict = outerModelDict[model.walletName];
        model.walletType = [innerDict safeStringForKey:@"type"];
        model.walletTypeShowStr = [kWalletManager getWalletTypeShowStr:model.walletType];
        model.address = [innerDict safeStringForKey:@"addr"];
        model.backColor = [OKWalletListTableViewCellModel getBackColor:model.walletType];
        model.iconName = [OKWalletListTableViewCellModel getBgImageName:model.walletType];
        model.isCurrent = [kWalletManager.currentWalletName isEqualToString:model.walletName];
        [walletArray addObject:model];
    }
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"walletType contains %@",[@"HD" lowercaseString]];
    self.showList = [walletArray filteredArrayUsingPredicate:predicate];
    self.countLabel.text = [NSString stringWithFormat:@"%zd",self.showList.count];
    self.headerTitleLabel.text = MyLocalizedString(@"HD wallet", nil);
    [self.tableView reloadData];
}
#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.showList.count;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 90;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKWalletListTableViewCell";
    OKWalletListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKWalletListTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKWalletListTableViewCellModel *model = self.showList[indexPath.row];
    cell.model = model;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"点击了其中一行");
}

- (IBAction)headerTipsBtnclick:(UIButton *)sender {
    OKTipsViewController *tipsVc = [OKTipsViewController tipsViewController];
    tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self presentViewController:tipsVc animated:NO completion:nil];
}

- (NSArray *)showList
{
    if (!_showList) {
        _showList = [NSArray array];
    }
    return _showList;
}

@end
