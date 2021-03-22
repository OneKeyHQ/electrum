//
//  OKWallertTranferConnect.m
//  OneKey
//

#import "OKWallertTranferConnect.h"
#import "OKBluetoothViewCell.h"
#import "OKBluetoothViewCellModel.h"
#import "OKActivateDeviceSelectViewController.h"
#import "OKSetDeviceNameViewController.h"
#import "OKBlueManager.h"
#import "OKDeviceInfoModel.h"
#import "OKDiscoverNewDeviceViewController.h"
#import "OKSetDeviceNameViewController.h"
#import "OKSpecialEquipmentViewController.h"
#import "OKReceiveCoinViewController.h"
#import "OKSendCoinViewController.h"
#import "OKSignatureViewController.h"
#import "OKDeviceUpdateViewController.h"
#import "OKDeviceIdInconsistentViewController.h"

@interface OKWallertTranferConnect ()<OKBabyBluetoothManageDelegate>

@property (nonatomic,strong)NSMutableArray *dataSource;
@property (nonatomic,strong)NSTimer *terminalTimer;
@property (nonatomic,assign)NSInteger count;

@end

@implementation OKWallertTranferConnect

- (NSMutableArray *)dataSource {
    if (!_dataSource) {
        _dataSource = [NSMutableArray new];
    }
    return _dataSource;
}

- (void)startConnect {
    kOKBlueManager.delegate = self;
    self.terminalTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(terminalTimerTickTock) userInfo:nil repeats:YES];
    [[NSRunLoop currentRunLoop] addTimer:self.terminalTimer forMode:NSRunLoopCommonModes];
    [self startScanPeripheral];
}

- (void)stopConnect {
    [self.terminalTimer invalidate];
    self.terminalTimer = nil;
    [kOKBlueManager stopScanPeripheral];
}

- (void)terminalTimerTickTock
{
    _count ++;
    if (_count == 3) {
        _count = 0;
        [self.terminalTimer invalidate];
        self.terminalTimer = nil;
        OKDeviceModel *model = [[OKDevicesManager sharedInstance]getDeviceModelWithID:kWalletManager.currentWalletInfo.device_id];
            if (model == nil) { //数据被删除
                if ([kOKBlueManager isConnectedCurrentDevice]) {
                    [kOKBlueManager disconnectAllPeripherals];
                }
                [self connectFailed];
            }else{
                if ([kOKBlueManager isConnectedName:model.deviceInfo.ble_name] && kOKBlueManager.currentDeviceID != nil) {
                    NSDictionary *dict = [[[OKDevicesManager sharedInstance]getDeviceModelWithID:kOKBlueManager.currentDeviceID]json];
                    [self subscribeComplete:dict characteristic:nil];
                }else{
                    CBPeripheral *temp;
                    for (OKPeripheralInfo *infoModel in self.dataSource) {
                        if ([model.deviceInfo.ble_name isEqualToString:infoModel.peripheral.name]) {
                            temp = infoModel.peripheral;
                        }
                    }
                    if (temp != nil) {
                        [kOKBlueManager connectPeripheral:temp];
                    }else{
                        NSString *msg = MyLocalizedString(@"Time out for connecting Bluetooth device. Please make sure your device has Bluetooth enabled and is at your side", nil);
                        [kOKBlueManager disconnectAllPeripherals];
                        if (self.connectFailedCallback) {
                            self.connectFailedCallback(msg);
                        }
                    }
                }
            }
    }
}


- (void)startScanPeripheral
{
    [kOKBlueManager stopScanPeripheral];
    [self.dataSource removeAllObjects];
    [kOKBlueManager startScanPeripheral];
}

- (void)dealloc
{
    [self stopConnect];
}


#pragma mark OKBabyBluetoothManageDelegate
- (void)systemBluetoothClose {
    NSString *msg = MyLocalizedString(@"Bluetooth of the system has been turned off. Please turn it on", nil);
    if (self.connectFailedCallback) {
        self.connectFailedCallback(msg);
    }
}

- (void)sysytemBluetoothOpen {
    // 系统蓝牙已开启、开始扫描周边的蓝牙设备
    [kOKBlueManager startScanPeripheral];
}

- (void)disconnectPeripheral:(CBPeripheral *)peripheral
{
    NSString *msg = [NSString stringWithFormat:@"%@%@",peripheral.name,MyLocalizedString(@"connection is broken", nil)];
    if (self.connectFailedCallback) {
        self.connectFailedCallback(msg);
    }
}

- (void)getScanResultPeripherals:(NSArray *)peripheralInfoArr {
    NSLog(@"peripheralInfoArr == %@",peripheralInfoArr);

    OKWeakSelf(self)
    // 这里获取到扫描到的蓝牙外设数组、添加至数据源中
    if (self.dataSource.count>0) {
        [weakself.dataSource removeAllObjects];
    }
    [weakself.dataSource addObjectsFromArray:peripheralInfoArr];
    if (![weakself.dataSource containsObject:kOKBlueManager.currentPeripheral] && kOKBlueManager.currentPeripheral != nil) {
        OKPeripheralInfo *currentPeripheralInfo = [[OKPeripheralInfo alloc] init];
        currentPeripheralInfo.peripheral = kOKBlueManager.currentPeripheral;
        [weakself.dataSource addObject:currentPeripheralInfo];
    }
}

- (void)connectSuccess {
    NSLog(@"connectSuccess");
}

- (void)readData:(NSData *)valueData {
    // 获取到蓝牙设备发来的数据
    NSLog(@"蓝牙发来的数据 = %@",valueData);
    NSLog(@"hexStringForData = %@",[NSData hexStringForData:valueData]);
}

- (void)connectFailed {
    // 连接失败、做连接失败的处理
    NSString *msg = MyLocalizedString(@"Bluetooth connection failed, please try again", nil);
    if (self.connectFailedCallback) {
        self.connectFailedCallback(msg);
    }
}

- (void)subscribeComplete:(NSDictionary *)jsonDict characteristic:(nonnull CBCharacteristic *)ch
{
    OKWeakSelf(self)
    NSString *chUUID = [NSString stringWithFormat:@"%@",ch.UUID];
    if ([chUUID isEqualToString:kDEVICEINFOCHARACTERISTIC]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself connectFailed];
        });
        return;
    }
    if (jsonDict == nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *msg = MyLocalizedString(@"This operation is not supported if the current device is not active, or if the special device is backed up", nil);
            if (weakself.connectFailedCallback) {
                weakself.connectFailedCallback(msg);
            }
        });
        return;
    }
    OKDeviceModel *deviceModel  = [[OKDeviceModel alloc]initWithJson:jsonDict];
    kOKBlueManager.currentDeviceID = deviceModel.deviceInfo.device_id;
    [[OKDevicesManager sharedInstance] addDevices:deviceModel];

    if (deviceModel.bootloaderMode) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself connectFailed];
        });
        return;
    }
    BOOL isFirmwareH = [OKTools compareVersion:kIOSFirmwareSysVersin version2:deviceModel.deviceInfo.deviceSysVersion] == 1?YES:NO;
    if (isFirmwareH) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself connectFailed];
        });
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        if (deviceModel.deviceInfo.initialized && !deviceModel.deviceInfo.backup_only&& [deviceModel.deviceInfo.device_id isEqualToString:kWalletManager.currentWalletInfo.device_id]) {
            if (weakself.connectSuccessCallback) {
                weakself.connectSuccessCallback();
            }
        }else{
            if (![deviceModel.deviceInfo.device_id isEqualToString:kWalletManager.currentWalletInfo.device_id]) {
                if (weakself.connectSuccessCallback) {
                    weakself.connectSuccessCallback();
                }
            }else{
                NSString *msg = MyLocalizedString(@"This operation is not supported if the current device is not active, or if the special device is backed up", nil);
                [kOKBlueManager disconnectAllPeripherals];
                if (weakself.connectFailedCallback) {
                    weakself.connectFailedCallback(msg);
                }
                return;
            }
        }
    });
}

@end
