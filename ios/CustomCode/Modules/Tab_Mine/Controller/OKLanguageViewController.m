//
//  OKLanguageViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKLanguageViewController.h"
#import "OKLanguageTableViewCell.h"

@interface OKLanguageViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *allData;

@end

@implementation OKLanguageViewController

+ (instancetype)languageViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKLanguageViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"language", nil);;
    
    self.tableView.tableFooterView = [UIView new];
}

#pragma mark - UITableViewDataSource
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(0xF5F6F7);
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(20, 35, 200, 22)];
    switch (section) {
        case 0:
            label.text = MyLocalizedString(@"Select the App's display language", nil);
            break;
        default:
            break;
    }
    label.textColor = HexColor(0x546370);
    label.font = [UIFont systemFontOfSize:14];
    [view addSubview:label];
    return view;
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 65;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.allData.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKLanguageTableViewCell";
    OKLanguageTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKLanguageTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.row];
    return  cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (NSArray *)allData
{
    if (!_allData) {
        OKLanguageCellModel *model1 = [[OKLanguageCellModel alloc]init];
        model1.titleStr = MyLocalizedString(@"Following system language", nil);
        model1.isSelected = YES;
        
        OKLanguageCellModel *model2 = [[OKLanguageCellModel alloc]init];
        model2.titleStr = MyLocalizedString(@"Chinese (Simplified)", nil);
        model2.isSelected = NO;
        
        OKLanguageCellModel *model3 = [[OKLanguageCellModel alloc]init];
        model3.titleStr = @"English";
        model3.isSelected = NO;
        
        _allData = @[model1,model2,model3];
    }
    return _allData;
}


@end
