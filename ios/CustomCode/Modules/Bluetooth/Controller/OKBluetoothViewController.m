//
//  OKBluetoothViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/18.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKBluetoothViewController.h"
#import "OKBluetoothViewCell.h"
#import "OKBluetoothViewCellModel.h"
#import "OKBlueManager.h"
#import "BabyBluetooth.h"

@interface  OKBluetoothViewController()<UITableViewDelegate,UITableViewDataSource,OKBabyBluetoothManageDelegate>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *tipsLabel;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIButton *refreshBtn;
- (IBAction)writeBtnClick:(UIButton *)sender;
- (IBAction)readBtnClick:(UIButton *)sender;

@end

@implementation OKBluetoothViewController

- (NSArray *)dataSource {
    if (!_dataSource) {
        _dataSource = [NSMutableArray new];
    }
    return _dataSource;
}
+ (instancetype)bluetoothViewController
{
    return [[UIStoryboard storyboardWithName:@"Bluetooth" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBluetoothViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self setupUI];
    kOKBlueManager.delegate = self;
}

- (void)setupUI
{
    self.title = MyLocalizedString(@"pairing", nil);
    self.titleLabel.text = MyLocalizedString(@"Open your hardware wallet and hold it close to your phone", nil);
    self.tipsLabel.text = MyLocalizedString(@"Locate the following devices", nil);
    [self.bgView setLayerRadius:20];
    [self.refreshBtn addTarget:self action:@selector(refreshBtnClick) forControlEvents:UIControlEventTouchUpInside];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.tableView.tableFooterView = [UIView new];
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.dataSource.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKBluetoothViewCell";
    OKBluetoothViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKBluetoothViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    OKPeripheralInfo *info =  self.dataSource[indexPath.row];
    OKBluetoothViewCellModel *model = [OKBluetoothViewCellModel new];
    model.blueName = info.peripheral.name;
    cell.model = model;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKPeripheralInfo *peripheralInfo = self.dataSource[indexPath.row];
    [kOKBlueManager connectPeripheral:peripheralInfo.peripheral];
}
- (void)refreshBtnClick
{
    [kOKBlueManager startScanPeripheral];
}

//#pragma mark OKBabyBluetoothManageDelegate
- (void)systemBluetoothClose {
    // 系统蓝牙被关闭、提示用户去开启蓝牙
    NSLog(@"系统蓝牙被关闭、提示用户去开启蓝牙");
}

- (void)sysytemBluetoothOpen {
    // 系统蓝牙已开启、开始扫描周边的蓝牙设备
    [kOKBlueManager startScanPeripheral];
}

- (void)getScanResultPeripherals:(NSArray *)peripheralInfoArr {
    // 这里获取到扫描到的蓝牙外设数组、添加至数据源中
    if (self.dataSource.count>0) {
        [self.dataSource removeAllObjects];
    }

    [self.dataSource addObjectsFromArray:peripheralInfoArr];
    [self.tableView reloadData];
}

- (void)connectSuccess {
    // 连接成功 写入UUID值【替换成自己的蓝牙设备UUID值】
    kOKBlueManager.serverUUIDString = @"0001";
    kOKBlueManager.writeUUIDString = @"0002";
    kOKBlueManager.readUUIDString = @"0003";

}

- (void)connectFailed {
    // 连接失败、做连接失败的处理
}

- (void)readData:(NSData *)valueData {
    // 获取到蓝牙设备发来的数据
    NSLog(@"蓝牙发来的数据 = %@",valueData);
    NSLog(@"hexStringForData = %@",[NSData hexStringForData:valueData]);
}

- (IBAction)disconnectAction:(UIButton *)sender {
    // 断开连接
    // 1、可以选择断开所有设备
    // 2、也选择断开当前peripheral
    [kOKBlueManager disconnectAllPeripherals];
}

- (void)disconnectPeripheral:(CBPeripheral *)peripheral {
    // 获取到当前断开的设备 这里可做断开UI提示处理
    NSLog(@"断开连接了");

}

- (IBAction)readBtnClick:(UIButton *)sender {

}

- (IBAction)writeBtnClick:(UIButton *)sender {
    [kOKBlueManager write:[NSData dataForHexString:@"3f2323000000000000"]];
}
@end
