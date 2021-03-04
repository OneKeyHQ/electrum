//
//  OKDeviceListViewController.m
//  OneKey
//
//  Created by liuzj on 06/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceListViewController.h"
#import "OKHardwareListBaseCell.h"
#import "OKDeviceSettingsViewController.h"
#import "OKDeviceListCellModel.h"
#import "OKDevicesManager.h"

NSString *const OKDeviceListReloadNotificationKey = @"OKDeviceListReloadNotificationKey";

static const NSUInteger backgroundColor = 0xF5F6F7;
static const NSUInteger sectionTitleColor = 0x546370;
static const NSUInteger titleColor = 0x14293b;

@interface OKDeviceListViewController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSMutableArray <OKDeviceListCellModel *>*deviceCells;

@end

@implementation OKDeviceListViewController

+ (instancetype)deviceListController {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceListViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadDeviceList) name:OKDeviceListReloadNotificationKey object:nil];
    self.deviceCells = [[NSMutableArray alloc] init];
    self.tableView.backgroundColor = HexColor(backgroundColor);
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.title = @"All the equipment".localized;
    [self reloadDeviceList];
}


#pragma mark - UITableViewDelegate & UITableViewDataSource

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    UIView *view = [[UIView alloc] initWithFrame: CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(backgroundColor);
    UILabel *label = [[UILabel alloc] initWithFrame: CGRectMake(20, 35, 200, 22)];
    label.text = @"hardwareWallet.devices".localized;
    label.textColor = HexColor(sectionTitleColor);
    label.font = [UIFont systemFontOfSize:14];
    [view addSubview:label];
    return view;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {

    static NSString *cellID = @"OKHardwareListBaseCell";
    OKHardwareListBaseCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[OKHardwareListBaseCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellID];
    }
    OKDeviceListCellModel *hardwareModel = self.deviceCells[indexPath.row];
    [cell setModel:hardwareModel];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    OKDeviceSettingsViewController *vc = [OKDeviceSettingsViewController deviceSettingsViewController];
    vc.deviceModel = self.deviceCells[indexPath.row].devcie;
    [self.navigationController pushViewController:vc animated:YES];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 65;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 75;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.deviceCells.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (void)reloadDeviceList {
    [self.deviceCells removeAllObjects];

    NSArray <OKDeviceModel *>*devices = [OKDevicesManager sharedInstance].devices.allValues;

    for (OKDeviceModel *device in devices) {
        OKDeviceListCellModel *deviceCell = [[OKDeviceListCellModel alloc] init];
        deviceCell.devcie = device;
        deviceCell.name = device.deviceInfo.label.length ? device.deviceInfo.label : device.deviceInfo.ble_name;
        deviceCell.imageName = @"device_bixinkey";
        deviceCell.titleColor = titleColor;
        [self.deviceCells addObject:deviceCell];
    }
    [self.tableView reloadData];
}

@end
