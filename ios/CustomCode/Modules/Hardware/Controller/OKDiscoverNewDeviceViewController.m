//
//  OKDiscoverNewDeviceViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDiscoverNewDeviceViewController.h"
#import "OKSetDeviceNameViewController.h"
#import "OKCreateSelectWalletTypeCell.h"
#import "OKCreateSelectWalletTypeModel.h"
#import "OKDeviceSettingsViewController.h"

@interface OKDiscoverNewDeviceViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (nonatomic,strong)NSArray *walletTypeListArray;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *toSettingLabel;
@end

@implementation OKDiscoverNewDeviceViewController
+ (instancetype)discoverNewDeviceViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDiscoverNewDeviceViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"pairing", nil);
    self.titleLabel.text = MyLocalizedString(@"Discover a new device you can...", nil);
    self.tableView.tableFooterView = [UIView new];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapClick)];
    [self.toSettingLabel addGestureRecognizer:tap];
}

- (void)backToPrevious
{
    switch (_where) {
        case OKMatchingFromWhereNav:
            [self.navigationController popToRootViewControllerAnimated:YES];
            break;
        case OKMatchingFromWhereDis:
            [self.navigationController dismissViewControllerAnimated:YES completion:nil];
            break;
        default:
            break;
    }
}

- (void)tapClick
{
    OKDeviceSettingsViewController *vc = [OKDeviceSettingsViewController deviceSettingsViewController];
    vc.deviceModel = [[OKDevicesManager sharedInstance]getDeviceModelWithID:kOKBlueManager.currentDeviceID];
    [self.navigationController pushViewController:vc animated:YES];
}


#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.walletTypeListArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKCreateSelectWalletTypeCell";
    OKCreateSelectWalletTypeCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKCreateSelectWalletTypeCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.walletTypeListArray[indexPath.row];
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKSetDeviceNameViewController *setDeviceNameVc = [OKSetDeviceNameViewController setDeviceNameViewController];
    setDeviceNameVc.type = OKMatchingTypeActivation;
    setDeviceNameVc.where = self.where;
    [self.navigationController pushViewController:setDeviceNameVc animated:YES];
}


- (NSArray *)walletTypeListArray
{
    if (!_walletTypeListArray) {
        OKCreateSelectWalletTypeModel *model = [OKCreateSelectWalletTypeModel new];
        model.createWalletType = MyLocalizedString(@"Activated as a new device", nil);
        model.iconName = @"new_device";
        model.tipsString = MyLocalizedString(@"It is suitable for the vast majority of first-time users of the hardware wallet", nil);
        model.addtype = OKAddTypeActivationHWWallet;
        _walletTypeListArray = @[model];
    }
    return _walletTypeListArray;
}


@end
