//
//  OKPINInputMethodController.m
//  OneKey
//
//  Created by zj on 2021/2/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKPINInputMethodController.h"
#import "OKPINInputMethodCell.h"

@interface OKPINInputMethodController () <UITableViewDelegate,UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic,strong)NSArray *allData;
@property (nonatomic) NSInteger oldLanguage;
@property (nonatomic) NSInteger newLanguage;
@end

@implementation OKPINInputMethodController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKPINInputMethodController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"hardwareWallet.pin.verifyMethod", nil);
    self.tableView.tableFooterView = [UIView new];
    self.oldLanguage = kLocalizableManager.languageType;
}

#pragma mark - UITableViewDataSource
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(0xF5F6F7);
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(20, 35, kScreenWidth - 40, 22)];
    label.text = MyLocalizedString(@"hardwareWallet.pin.verifyMethodTip", nil);
    label.textColor = HexColor(0x546370);
    label.font = [UIFont systemFontOfSize:14];
    [view addSubview:label];
    return view;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 65;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 75;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.allData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *ID = @"OKPINInputMethodCell";
    OKPINInputMethodCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKPINInputMethodCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKPINInputMethodCellModel *model = self.allData[indexPath.row];
    cell.model = model;
    return  cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    OKPINInputMethodCellModel *model = self.allData[indexPath.row];
    kUserSettingManager.pinInputMethod = model.type;
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    [tableView reloadData];
}

- (NSArray *)allData {
    OKDevicePINInputMethod type = kUserSettingManager.pinInputMethod;

    OKPINInputMethodCellModel *model0 = [[OKPINInputMethodCellModel alloc]init];
    model0.titleStr = MyLocalizedString(@"hardwareWallet.pin.onDevice", nil);
    model0.type = OKDevicePINInputMethodOnDevice;
    model0.isSelected = type == model0.type;

    OKPINInputMethodCellModel *model1 = [[OKPINInputMethodCellModel alloc]init];
    model1.titleStr = MyLocalizedString(@"hardwareWallet.pin.onApp", nil);
    model1.type = OKDevicePINInputMethodOnApp;
    model1.isSelected = type == model1.type;

    return @[model0, model1];
}

@end
