//
//  OKLanguageViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKLanguageViewController.h"
#import "OKLanguageTableViewCell.h"

@interface OKLanguageViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *allData;

@property (nonatomic) NSInteger oldLanguage;

@property (nonatomic) NSInteger newLanguage;

@end

@implementation OKLanguageViewController

+ (instancetype)languageViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKLanguageViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"language", nil);
    self.tableView.tableFooterView = [UIView new];
    self.oldLanguage = kLocalizableManager.languageType;
}

#pragma mark - UITableViewDataSource
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(0xF5F6F7);
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(20, 35, kScreenWidth - 40, 22)];
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
    OKLanguageCellModel *model = self.allData[indexPath.row];
    cell.model = model;
    return  cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    switch (indexPath.row) {
        case 0:
            [kLocalizableManager setupAppLanguage:AppLanguageTypeFollowSys];
            break;
        case 1:
            [kLocalizableManager setupAppLanguage:AppLanguageTypeZh_Hans];
            break;
        case 2:
            [kLocalizableManager setupAppLanguage:AppLanguageTypeEn];
            break;
        default:
            break;
    }
    self.newLanguage = kLocalizableManager.languageType;
    if (self.oldLanguage != self.newLanguage) {
        [self.navigationController popToRootViewControllerAnimated:YES];
        [kAppdelegate resetMainVCRootViewControllerSelectSetingVc:YES];
    }
}

- (NSArray *)allData
{
    if (!_allData) {
        AppLanguageType type =  kLocalizableManager.languageType; ;
        
        OKLanguageCellModel *model1 = [[OKLanguageCellModel alloc]init];
        model1.titleStr = MyLocalizedString(@"Following system language", nil);
        model1.isSelected = type == AppLanguageTypeFollowSys;
        
        OKLanguageCellModel *model2 = [[OKLanguageCellModel alloc]init];
        model2.titleStr = MyLocalizedString(@"Chinese (Simplified)", nil);
        model2.isSelected = type == AppLanguageTypeZh_Hans;
        
        OKLanguageCellModel *model3 = [[OKLanguageCellModel alloc]init];
        model3.titleStr = @"English";
        model3.isSelected = type == AppLanguageTypeEn;
        
        _allData = @[model1,model2,model3];
    }
    return _allData;
}
@end
