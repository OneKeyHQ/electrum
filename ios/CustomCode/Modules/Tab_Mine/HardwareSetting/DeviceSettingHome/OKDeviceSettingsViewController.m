//
//  OKDeviceSettingsViewController.m
//  OneKey
//
//  Created by liuzj on 07/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceSettingsViewController.h"
#import "OKHardwareListBaseCell.h"
#import "OKDeviceInfoViewController.h"
#import "OKDeviceUpdateViewController.h"
#import "OKDeviceDeleteController.h"
#import "OKDeviceOffController.h"
#import "OKDeviceVerifyController.h"
#import "OKResetDeviceWarningViewController.h"
#import "OKDeviceLanguageViewController.h"
#import "OKDeviceModel.h"

static const NSUInteger backgroundColor = 0xF5F6F7;
static const NSUInteger sectionTitleColor = 0x546370;
static const NSUInteger titleColor = 0x14293b;
static const NSUInteger titleColorRed = 0xeb5757;

@implementation OKDeviceSettingsModel
@end


@interface OKDeviceSettingsViewController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic, strong) NSArray<OKDeviceSettingsModel *>* listData;
@end

@implementation OKDeviceSettingsViewController

+ (instancetype)deviceSettingsViewController {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceSettingsViewController"];

}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.backgroundColor = HexColor(backgroundColor);
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.title = self.deviceModel.deviceInfo.label;
    [self reloadListData];
}

#pragma mark - UITableViewDelegate & UITableViewDataSource

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *view = [[UIView alloc] initWithFrame: CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(backgroundColor);
    
    UILabel *label = [[UILabel alloc] initWithFrame: CGRectMake(20, 35, 200, 22)];
    label.textColor = HexColor(sectionTitleColor);
    label.font = [UIFont systemFontOfSize:14];
    label.text = self.listData[section].sectionTitle;
    [view addSubview:label];
    return view;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    UIViewController *vc = [OKDeviceInfoViewController controllerWithStoryboard];
    
    switch ([self cellModelForIndexPath:indexPath].cellType) {
        case OKHardwareListBaseCellTypeDeviceInfo: {
            OKDeviceInfoViewController *vc = [OKDeviceInfoViewController controllerWithStoryboard];
            vc.deviceModel = self.deviceModel;
            [self.navigationController pushViewController:vc animated:YES];
        } break;
            
        case OKHardwareListBaseCellTypeDeviceLanguage: {
            vc = [OKDeviceLanguageViewController controllerWithStoryboard];
            [self.navigationController pushViewController:vc animated:YES];
        } break;
        
        case OKHardwareListBaseCellTypeDeviceUpdate: {
            vc = [OKDeviceUpdateViewController controllerWithStoryboard];
            [self.navigationController pushViewController:vc animated:YES];
        } break;
            
        case OKHardwareListBaseCellTypeDeviceAutoOff: {
            vc = [OKDeviceOffController controllerWithStoryboard];
            [self.navigationController pushViewController:vc animated:YES];
        } break;
            
        case OKHardwareListBaseCellTypeDeviceVerify: {
            vc = [OKDeviceVerifyController controllerWithStoryboard];
            [self.navigationController pushViewController:vc animated:YES];
        } break;
            
        case OKHardwareListBaseCellTypeDeviceChangePIN: {
        } break;
        case OKHardwareListBaseCellTypeDeviceShowXPUB: {
        } break;
            
        case OKHardwareListBaseCellTypeDeviceRecover: {
            vc = [OKResetDeviceWarningViewController controllerWithStoryboard];
            [self.navigationController pushViewController:vc animated:YES];
        } break;
            
        case OKHardwareListBaseCellTypeDeviceDelete: {
            vc = [OKDeviceDeleteController controllerWithStoryboard];
            [self presentViewController:vc animated:YES completion:nil];
        } break;

        default:
            break;
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    

    static NSString *cellID = @"OKHardwareListBaseCell";
    OKHardwareListBaseCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[OKHardwareListBaseCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellID];
    }
    NSUInteger section = indexPath.section;
    OKHardwareListBaseCellModel *cellModel = self.listData[section].cellModel[indexPath.row];
    cellModel.titleColor = cellModel.titleColor ?: titleColor;
    [cell setModel:cellModel];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 65;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 75;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.listData[section].cellModel.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.listData.count;
}

- (OKHardwareListBaseCellModel *)cellModelForIndexPath:(NSIndexPath *)indexPath {
    return self.listData[indexPath.section].cellModel[indexPath.row];
}

- (void)reloadListData {
    OKHardwareListBaseCellModel *deviceInfoCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceInfoCellModel.title = MyLocalizedString(@"hardwareWallet.info", nil);
    deviceInfoCellModel.imageName = @"c-info 1";
    deviceInfoCellModel.details = @"";
    deviceInfoCellModel.cellType = OKHardwareListBaseCellTypeDeviceInfo;

    OKHardwareListBaseCellModel *devicelanguageCellModel = [[OKHardwareListBaseCellModel alloc] init];
    devicelanguageCellModel.title = MyLocalizedString(@"language", nil);//@"语言";
    devicelanguageCellModel.imageName = @"language";
    devicelanguageCellModel.details = @"中文";
    devicelanguageCellModel.cellType = OKHardwareListBaseCellTypeDeviceLanguage;

    OKHardwareListBaseCellModel *deviceUpdateCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceUpdateCellModel.title = MyLocalizedString(@"hardwareWallet.update", nil);//@"升级固件";
    deviceUpdateCellModel.imageName = @"sim-card 1";
    deviceUpdateCellModel.details = @"";
    deviceUpdateCellModel.cellType = OKHardwareListBaseCellTypeDeviceUpdate;

    OKHardwareListBaseCellModel *deviceAutoOffCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceAutoOffCellModel.title = MyLocalizedString(@"hardwareWallet.autoOff", nil);//@"自动关机";
    deviceAutoOffCellModel.imageName = @"engine-start 1";
    deviceAutoOffCellModel.details = @"600s";
    deviceAutoOffCellModel.cellType = OKHardwareListBaseCellTypeDeviceAutoOff;

    OKHardwareListBaseCellModel *deviceVerifyCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceVerifyCellModel.title = MyLocalizedString(@"hardwareWallet.verify", nil);
    deviceVerifyCellModel.imageName = @"privacy 1";
    deviceVerifyCellModel.details = @"";
    deviceVerifyCellModel.cellType = OKHardwareListBaseCellTypeDeviceVerify;

    OKDeviceSettingsModel *generalSettings = [[OKDeviceSettingsModel alloc] init];
    generalSettings.sectionTitle = MyLocalizedString(@"general", nil);//@"通用";
    generalSettings.cellModel = @[
        deviceInfoCellModel,
        devicelanguageCellModel,
        deviceUpdateCellModel,
        deviceAutoOffCellModel,
        deviceVerifyCellModel
    ];
    
    
//    OKHardwareListBaseCellModel *quickPayCellModel = [[OKHardwareListBaseCellModel alloc] init];
//    quickPayCellModel.title = @"快捷支付";
//    quickPayCellModel.imageName = @"traffic 1";
    
    OKHardwareListBaseCellModel *changepPINCellModel = [[OKHardwareListBaseCellModel alloc] init];
    changepPINCellModel.title = MyLocalizedString(@"hardwareWallet.pin", nil); //修改 PIN 码
    changepPINCellModel.imageName = @"Group";
    changepPINCellModel.cellType = OKHardwareListBaseCellTypeDeviceChangePIN;

    OKHardwareListBaseCellModel *showXPUBCellModel = [[OKHardwareListBaseCellModel alloc] init];
    showXPUBCellModel.title = MyLocalizedString(@"hardwareWallet.xpub", nil);
    showXPUBCellModel.imageName = @"Group-1";
    showXPUBCellModel.cellType = OKHardwareListBaseCellTypeDeviceShowXPUB;

    
    OKDeviceSettingsModel *securitySettings = [[OKDeviceSettingsModel alloc] init];
    securitySettings.sectionTitle = MyLocalizedString(@"security", nil);//@"安全";
    securitySettings.cellModel = @[
//        quickPayCellModel,
        changepPINCellModel,
        showXPUBCellModel
    ];
    
//    OKHardwareListBaseCellModel *hideWalletCellModel = [[OKHardwareListBaseCellModel alloc] init];
//    hideWalletCellModel.title = @"隐藏钱包";
//    hideWalletCellModel.imageName = @"traffic 2";
//    hideWalletCellModel.cellType = OKHardwareListBaseCellTypeDeviceHidden;
//
//    OKDeviceSettingsModel *advanceSettings = [[OKDeviceSettingsModel alloc] init];
//    advanceSettings.sectionTitle = @"高级";
//    advanceSettings.cellModel = @[
//        hideWalletCellModel
//    ];
//
    
    OKHardwareListBaseCellModel *recoverDeviceCellModel = [[OKHardwareListBaseCellModel alloc] init];
    recoverDeviceCellModel.title = MyLocalizedString(@"hardwareWallet.recover.title", nil);//@"恢复出厂设置";
    recoverDeviceCellModel.titleColor = titleColorRed;
    recoverDeviceCellModel.cellType = OKHardwareListBaseCellTypeDeviceRecover;

    OKHardwareListBaseCellModel *removeDeviceCellModel = [[OKHardwareListBaseCellModel alloc] init];
    removeDeviceCellModel.title = MyLocalizedString(@"hardwareWallet.delete", nil);//@"删除设备";
    removeDeviceCellModel.titleColor = titleColorRed;
    removeDeviceCellModel.cellType = OKHardwareListBaseCellTypeDeviceDelete;
    removeDeviceCellModel.hideRightArrow = YES;
    
    OKDeviceSettingsModel *dangerSettings = [[OKDeviceSettingsModel alloc] init];
    dangerSettings.sectionTitle = MyLocalizedString(@"Dangerous operation", nil);//@"危险设置";
    dangerSettings.cellModel = @[
        recoverDeviceCellModel,
        removeDeviceCellModel
    ];
    
    self.listData = @[
        generalSettings,
        securitySettings,
//        advanceSettings,
        dangerSettings
    ];
}
@end
