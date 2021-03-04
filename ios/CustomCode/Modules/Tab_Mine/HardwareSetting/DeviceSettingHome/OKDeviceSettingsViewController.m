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
#import "OKDevicesManager.h"
#import "OKDeviceChangePINController.h"
#import "OKDeviceShowXPUBController.h"
#import "OKDeviceListViewController.h"
#import "OKVersion.h"

static const NSUInteger backgroundColor = 0xF5F6F7;
static const NSUInteger sectionTitleColor = 0x546370;
static const NSUInteger titleColor = 0x14293b;
static const NSUInteger titleColorRed = 0xeb5757;

@implementation OKDeviceSettingsModel
@end


@interface OKDeviceSettingsViewController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic, strong) NSArray<OKDeviceSettingsModel *>* listData;
@property (strong, nonatomic) NSString *deviceId;
@property (strong, nonatomic) UIView *loadingView;
@end

@implementation OKDeviceSettingsViewController

+ (instancetype)deviceSettingsViewController {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceSettingsViewController"];

}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.backgroundColor = HexColor(backgroundColor);
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.title = self.deviceModel.deviceInfo.label.length ? self.deviceModel.deviceInfo.label : self.deviceModel.deviceInfo.ble_name;
    self.deviceId = self.deviceModel.deviceInfo.device_id;
    [self.view addSubview:self.loadingView];
    self.loadingView.hidden = YES;
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

    OKHardwareListBaseCellType type = [self cellModelForIndexPath:indexPath].cellType;
    switch (type) {
        case OKHardwareListBaseCellTypeDeviceInfo: {
            OKDeviceInfoViewController *vc = [OKDeviceInfoViewController controllerWithStoryboard];
            vc.deviceId = self.deviceId;
            [self.navigationController pushViewController:vc animated:YES];
        } break;

        case OKHardwareListBaseCellTypeDeviceUpdate: {
            [self ensureDeviceMatch:^(BOOL needUpdate) {
                OKDeviceUpdateViewController *vc = [OKDeviceUpdateViewController controllerWithStoryboard];
                vc.deviceId = self.deviceId;
                [self.navigationController pushViewController:vc animated:YES];
            }];
        } break;

        case OKHardwareListBaseCellTypeDeviceVerify: {
            [self ensureDeviceMatch:^(BOOL needUpdate) {
                OKDeviceVerifyController *vc = [OKDeviceVerifyController controllerWithStoryboard];
                vc.deviceId = self.deviceId;
                OKWeakSelf(self)
                vc.resultCallback = ^(BOOL isPass) {
                    if (isPass) {
                        [weakself reloadListData];
                    }
                };
                [self.navigationController pushViewController:vc animated:YES];
            }];
        } break;

        case OKHardwareListBaseCellTypeDeviceChangePIN: {
            [self ensureDeviceMatch:^(BOOL needUpdate) {
                if (needUpdate) {
                    [kTools tipMessage:@"hardwareWallet.update.needUpdate".localized];
                    return;
                }
                UIViewController *vc = [[OKDeviceChangePINController alloc] init];
                BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:vc];
                [self presentViewController:nav animated:YES completion:nil];
            }];
        } break;

        case OKHardwareListBaseCellTypeDeviceShowXPUB: {
            [self ensureDeviceMatch:^(BOOL needUpdate) {
                UIViewController *vc = [OKDeviceShowXPUBController controllerWithStoryboard];
                [self.navigationController pushViewController:vc animated:YES];
            }];
        } break;

        case OKHardwareListBaseCellTypeDeviceReset: {
            [self ensureDeviceMatch:^(BOOL needUpdate) {
                UIViewController *vc = [OKResetDeviceWarningViewController controllerWithStoryboard];
                [self.navigationController pushViewController:vc animated:YES];
            }];
        } break;

        case OKHardwareListBaseCellTypeDeviceDelete: {
            OKDeviceDeleteController *vc = [OKDeviceDeleteController controllerWithStoryboard];
            vc.deleteDeviceCallback = ^{
                if ([kOKBlueManager.currentDeviceID isEqualToString:self.deviceId]) {
                    [kOKBlueManager disconnectAllPeripherals];
                }
                [[OKDevicesManager sharedInstance] removeDevice:self.deviceId];
                [[NSNotificationCenter defaultCenter] postNotificationName:OKDeviceListReloadNotificationKey object:nil];
                [self.navigationController popViewControllerAnimated:YES];
            };
            [self presentViewController:vc animated:YES completion:nil];
        } break;

        default:
            break;
    }
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)ensureDeviceMatch:(void(^)(BOOL needUpdate))callback {
    if (!callback || !self.deviceId) {
        [kTools tipMessage:@"蓝牙连接失败"];
        self.loadingView.hidden = YES;
        return;
    }
    self.loadingView.hidden = NO;
    OKWeakSelf(self)
    [kOKBlueManager startScanAndConnectWithName:self.deviceModel.deviceInfo.ble_name complete:^(BOOL isSuccess) {
        if (!isSuccess) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [kTools tipMessage:@"蓝牙连接超时"];
                self.loadingView.hidden = YES;
            });
            return;
        }
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            [kPyCommandsManager cancel];
            NSDictionary *json =  [kPyCommandsManager callInterface:kInterfaceget_feature parameter:@{}];
            if (!json) {
                return;
            }
            OKDeviceModel *device = [[OKDeviceModel alloc] initWithJson:json];
            NSString *currentDeviceId = device.deviceInfo.device_id;
            NSString *currentDeviceVersion = device.deviceInfo.deviceSysVersion;
            [[OKDevicesManager sharedInstance] addDevices:device];
            dispatch_async(dispatch_get_main_queue(), ^{
                weakself.loadingView.hidden = YES;
                if ([currentDeviceId isEqualToString:weakself.deviceId]) {
                    callback([weakself checkNeedUpdate:currentDeviceVersion]);
                    return;
                } else {
                    [kTools tipMessage:@"hardwareWallet.id_not_match".localized];
                }
            });
        });
    }];
}

- (UIView *)loadingView {
    if (!_loadingView) {
        _loadingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
        _loadingView.backgroundColor = HexColorA(0x444444, 0.7);
        _loadingView.center = self.view.center;
        [_loadingView setLayerRadius:10];

        UIActivityIndicatorView *actView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
        actView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [actView startAnimating];
        [_loadingView addSubview:actView];
    }
    return _loadingView;
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

- (BOOL)checkNeedUpdate:(NSString *)ver {
    return [OKVersion versionString:ver lessThen:@"2.0.4"];
}

- (void)reloadListData {
    OKHardwareListBaseCellModel *deviceInfoCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceInfoCellModel.title = @"hardwareWallet.info".localized;
    deviceInfoCellModel.imageName = @"c-info 1";
    deviceInfoCellModel.details = @"";
    deviceInfoCellModel.cellType = OKHardwareListBaseCellTypeDeviceInfo;

//    OKHardwareListBaseCellModel *devicelanguageCellModel = [[OKHardwareListBaseCellModel alloc] init];
//    devicelanguageCellModel.title = @"language".localized;//@"语言";
//    devicelanguageCellModel.imageName = @"language";
//    devicelanguageCellModel.details = @"中文";
//    devicelanguageCellModel.cellType = OKHardwareListBaseCellTypeDeviceLanguage;

    OKHardwareListBaseCellModel *deviceUpdateCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceUpdateCellModel.title = @"hardwareWallet.update".localized;//@"升级固件";
    deviceUpdateCellModel.imageName = @"sim-card 1";
    deviceUpdateCellModel.details = @"";
    deviceUpdateCellModel.cellType = OKHardwareListBaseCellTypeDeviceUpdate;

//    OKHardwareListBaseCellModel *deviceAutoOffCellModel = [[OKHardwareListBaseCellModel alloc] init];
//    deviceAutoOffCellModel.title = @"hardwareWallet.autoOff".localized;//@"自动关机";
//    deviceAutoOffCellModel.imageName = @"engine-start 1";
//    deviceAutoOffCellModel.details = @"600s";
//    deviceAutoOffCellModel.cellType = OKHardwareListBaseCellTypeDeviceAutoOff;

    OKHardwareListBaseCellModel *deviceVerifyCellModel = [[OKHardwareListBaseCellModel alloc] init];
    deviceVerifyCellModel.title = @"hardwareWallet.verify".localized;
    deviceVerifyCellModel.imageName = @"privacy 1";
    deviceVerifyCellModel.details = @"";
    deviceVerifyCellModel.cellType = OKHardwareListBaseCellTypeDeviceVerify;
    if (self.deviceModel.verifiedDevice) {
        deviceVerifyCellModel.tagBgColor = HexColorA(0x26cf02, 0.1);
        deviceVerifyCellModel.tagTextColor = HexColor(0x00b812);
        deviceVerifyCellModel.tagText = @"hardwareWallet.verify.pass".localized;
    }


    OKDeviceSettingsModel *generalSettings = [[OKDeviceSettingsModel alloc] init];
    generalSettings.sectionTitle = @"general".localized;//@"通用";
    generalSettings.cellModel = @[
        deviceInfoCellModel,
//        devicelanguageCellModel,
        deviceUpdateCellModel,
//        deviceAutoOffCellModel,
        deviceVerifyCellModel
    ];


//    OKHardwareListBaseCellModel *quickPayCellModel = [[OKHardwareListBaseCellModel alloc] init];
//    quickPayCellModel.title = @"快捷支付";
//    quickPayCellModel.imageName = @"traffic 1";

    OKHardwareListBaseCellModel *changepPINCellModel = [[OKHardwareListBaseCellModel alloc] init];
    changepPINCellModel.title = @"hardwareWallet.pin".localized; //修改 PIN 码
    changepPINCellModel.imageName = @"Group";
    changepPINCellModel.cellType = OKHardwareListBaseCellTypeDeviceChangePIN;

    OKHardwareListBaseCellModel *showXPUBCellModel = [[OKHardwareListBaseCellModel alloc] init];
    showXPUBCellModel.title = @"hardwareWallet.xpub".localized;
    showXPUBCellModel.imageName = @"Group-1";
    showXPUBCellModel.cellType = OKHardwareListBaseCellTypeDeviceShowXPUB;


    OKDeviceSettingsModel *securitySettings = [[OKDeviceSettingsModel alloc] init];
    securitySettings.sectionTitle = @"security".localized;//@"安全";
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
    recoverDeviceCellModel.title = @"hardwareWallet.recover.title".localized;//@"恢复出厂设置";
    recoverDeviceCellModel.titleColor = titleColorRed;
    recoverDeviceCellModel.cellType = OKHardwareListBaseCellTypeDeviceReset;

    OKHardwareListBaseCellModel *removeDeviceCellModel = [[OKHardwareListBaseCellModel alloc] init];
    removeDeviceCellModel.title = @"hardwareWallet.delete".localized;//@"删除设备";
    removeDeviceCellModel.titleColor = titleColorRed;
    removeDeviceCellModel.cellType = OKHardwareListBaseCellTypeDeviceDelete;
    removeDeviceCellModel.hideRightArrow = YES;

    OKDeviceSettingsModel *dangerSettings = [[OKDeviceSettingsModel alloc] init];
    dangerSettings.sectionTitle = @"Dangerous operation".localized;//@"危险设置";
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

    [self.tableView reloadData];
}
@end
