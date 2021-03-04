//
//  OKDeviceInfoViewController.m
//  OneKey
//
//  Created by liuzj on 08/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceInfoViewController.h"
#import "OKDevicesManager.h"

@interface OKDeviceInfoViewController()
@property (weak, nonatomic) IBOutlet UILabel *modelNameLabel;
@property (weak, nonatomic) IBOutlet UIView *nameView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *deviceIDLabel;
@property (weak, nonatomic) IBOutlet UILabel *bluetoothNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *sysVersionLabel;
@property (weak, nonatomic) IBOutlet UILabel *bluetoothVersionLabel;
@property (weak, nonatomic) IBOutlet UILabel *deviceID;
@property (weak, nonatomic) IBOutlet UILabel *bluetoothName;
@property (weak, nonatomic) IBOutlet UILabel *sysVersion;
@property (weak, nonatomic) IBOutlet UILabel *bluetoothVersion;

@end

@implementation OKDeviceInfoViewController
//static const NSUInteger backgroundColor = 0xF5F6F7;

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceInfoViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeName)];
    [self.nameView addGestureRecognizer:tap];
    self.title = @"hardwareWallet.info".localized;
    [self setupUI];
}

- (void)setupUI {
    OKDeviceInfoModel *deviceInfo = [[OKDevicesManager sharedInstance] getDeviceModelWithID:self.deviceId].deviceInfo;
    self.deviceIDLabel.text = @"hardwareWallet.info.id".localized;
    self.bluetoothNameLabel.text = @"hardwareWallet.info.buletoothName".localized;
    self.sysVersionLabel.text = @"hardwareWallet.info.sysVersion".localized;
    self.bluetoothVersionLabel.text = @"hardwareWallet.info.buletoothVersion".localized;

    self.nameLabel.text = deviceInfo.label;
    self.deviceID.text = deviceInfo.device_id;
    self.bluetoothName.text = deviceInfo.ble_name;
    self.sysVersion.text = deviceInfo.deviceSysVersion;
    self.bluetoothVersion.text = deviceInfo.ble_ver;

}

- (void)changeName {
    [kTools tipMessage:@"Temporary does not support".localized];
}
@end
