//
//  OKTradeSettingViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTradeSettingViewController.h"
#import "OKTradeSettingViewCell.h"
#import "OKTradeSettingViewCellModel.h"

@interface OKTradeSettingViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (nonatomic,strong)NSArray *allData;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *topTipsLabel;



@end

@implementation OKTradeSettingViewController
+ (instancetype)tradeSettingViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKTradeSettingViewController"];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
    
}
- (void)stupUI
{
    self.title = MyLocalizedString(@"Transaction Settings (Advanced)", nil);
    self.tableView.tableFooterView = [UIView new];
    self.topTipsLabel.text = MyLocalizedString(@"The following Settings apply to the Bitcoin account for the hardware wallet", nil);
  
    NSString *labelText = MyLocalizedString(@"Restore the default", nil);
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

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.allData count];
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKTradeSettingViewCell";
    OKTradeSettingViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKTradeSettingViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.row];
    return  cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

#pragma mark - tapRightViewClick
- (void)tapRightViewClick
{
    NSLog(@"点击了恢复默认");
}
- (NSArray *)allData
{
    if (!_allData) {
        
        OKTradeSettingViewCellModel *model1 = [[OKTradeSettingViewCellModel alloc]init];
        model1.titleStr = MyLocalizedString(@"Use RBF (trade substitution)", nil);
        model1.switchOn = YES;
        
        OKTradeSettingViewCellModel *model2 = [[OKTradeSettingViewCellModel alloc]init];
        model2.titleStr = MyLocalizedString(@"Spend unrecognized income", nil);
        model2.switchOn = YES;
        
        _allData = @[model1,model2];
    }
    return _allData;
}
@end
