//
//  OKBluetoothViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/18.
//  Copyright © 2020 OneKey. All rights reserved.
//

#define kPRIMARY_SERVICE        @"0001"
#define kWRITE_CHARACTERISTIC   @"0002"
#define kREAD_CHARACTERISTIC    @"0003"


#import "OKBluetoothViewController.h"
#import "OKBluetoothViewCell.h"
#import "OKBluetoothViewCellModel.h"
#import "BabyBluetooth.h"

@interface  OKBluetoothViewController()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *tipsLabel;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIButton *refreshBtn;
@property (nonatomic,strong)BabyBluetooth *baby;
@property (nonatomic,strong)NSMutableArray *peripherals;
@property (nonatomic,strong)CBService *service;
@property (nonatomic,strong)CBCharacteristic *readCharacteristic;
@property (nonatomic,strong)CBCharacteristic *writeCharacteristic;
@end

@implementation OKBluetoothViewController

+ (instancetype)bluetoothViewController
{
    return [[UIStoryboard storyboardWithName:@"Bluetooth" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBluetoothViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self setupUI];
    //初始化BabyBluetooth 蓝牙库
    _baby = [BabyBluetooth shareBabyBluetooth];
    //设置蓝牙委托
    [self babyDelegate];
    //设置委托后直接可以使用，无需等待CBCentralManagerStatePoweredOn状态
    [self refreshBtnClick];
}
-(void)babyDelegate{
    OKWeakSelf(self)
    [_baby setBlockOnDiscoverToPeripherals:^(CBCentralManager *central, CBPeripheral *peripheral, NSDictionary *advertisementData, NSNumber *RSSI) {
        [weakself.peripherals addObject:peripheral];
    
    }];
   
    [_baby setFilterOnDiscoverPeripherals:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
        if ([weakself validateBleName:peripheralName regular:@"^K[0-9]{4}$"]||[weakself validateBleName:peripheralName regular:@"^(B|b)(I|i)(X|x)(I|i)(N|n)(K|k)(E|e)(Y|y)[0-9]+$"]) {
            return YES;
        }
        return NO;
    }];
    
    [_baby setBlockOnCancelScanBlock:^(CBCentralManager *centralManager) {
        [weakself.tableView reloadData];
    }];
    
    [_baby setBlockOnConnected:^(CBCentralManager *central, CBPeripheral *peripheral) {

    }];
    
    [_baby setBlockOnDiscoverServices:^(CBPeripheral *peripheral, NSError *error) {
       
    }];
    
    [_baby setBlockOnReadValueForCharacteristic:^(CBPeripheral *peripheral, CBCharacteristic *characteristic, NSError *error) {
        
    }];
    [_baby setBlockOnDiscoverCharacteristics:^(CBPeripheral *peripheral, CBService *service, NSError *error) {
        if ([service.UUID isEqual:kPRIMARY_SERVICE]) {
            weakself.service = service;
            for (CBCharacteristic *c in service.characteristics){
                if ([c.UUID.UUIDString isEqualToString:kWRITE_CHARACTERISTIC]) {
                    weakself.writeCharacteristic = c;
                }else if ([c.UUID.UUIDString isEqualToString:kREAD_CHARACTERISTIC]) {
                    weakself.readCharacteristic = c;
                }
            }
        }
    }];
}

- (BOOL)validateBleName:(NSString *)textString regular:(NSString *)regularStr
{
    NSPredicate *numberPre = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regularStr];
    return [numberPre evaluateWithObject:textString];
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
    return self.peripherals.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKBluetoothViewCell";
    OKBluetoothViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKBluetoothViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    CBPeripheral *peripheral =  self.peripherals[indexPath.row];
    OKBluetoothViewCellModel *model = [OKBluetoothViewCellModel new];
    model.blueName = peripheral.name;
    cell.model = model;
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    CBPeripheral *peripheral = self.peripherals[indexPath.row];
    _baby.having(peripheral).and.then.connectToPeripherals().discoverServices().discoverCharacteristics().readValueForCharacteristic().discoverDescriptorsForCharacteristic().readValueForDescriptors().begin();
}

- (void)refreshBtnClick
{
    _baby.scanForPeripherals().begin().stop(6);
}
- (NSMutableArray *)peripherals
{
    if (!_peripherals) {
        _peripherals = [NSMutableArray array];
    }
    return _peripherals;
}
@end
