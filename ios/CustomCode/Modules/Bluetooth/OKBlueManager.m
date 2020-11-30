//
//  OKBlueManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/19.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKBlueManager.h"

@implementation OKPeripheralInfo

@end

@interface OKBlueManager()

@property (nonatomic,strong)BabyBluetooth *babyBluetooth;
@property (nonatomic,strong)CBPeripheral *currentPeripheral;
@property (nonatomic,strong)CBService *service;
@property (nonatomic,strong)CBCharacteristic *readCharacteristic;
@property (nonatomic,strong)CBCharacteristic *writeCharacteristic;
@property (nonatomic, strong)NSMutableArray  *peripheralArr;
@property (nonatomic,copy)NSString *currentReadDataStr;

@end

@implementation OKBlueManager
static dispatch_once_t once;
+ (OKBlueManager *)sharedInstance {
    static OKBlueManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKBlueManager alloc] init];
    });
    return _sharedInstance;
}


- (NSMutableArray *)peripheralArr {
    if (!_peripheralArr) {
        _peripheralArr = [NSMutableArray new];
    }
    return _peripheralArr;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        [self initBabyBluetooth];
    }
    return self;
}


- (void)initBabyBluetooth {
    self.babyBluetooth = [BabyBluetooth shareBabyBluetooth];
    [self babyBluetoothDelegate];
}


#pragma mark 蓝牙配置
- (void)babyBluetoothDelegate {
    __weak typeof(self) weakSelf = self;
    
    // 1-系统蓝牙状态
    [self.babyBluetooth setBlockOnCentralManagerDidUpdateState:^(CBCentralManager *central) {
        dispatch_async(dispatch_get_main_queue(), ^{
            // 从block中取到值，再回到主线程
            if ([weakSelf respondsToSelector:@selector(systemBluetoothState:)]) {
                [weakSelf systemBluetoothState:central.state];
            }
        });
    }];
    
    // 2-设置查找设备的过滤器
    [self.babyBluetooth setFilterOnDiscoverPeripherals:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
        // 最常用的场景是查找某一个前缀开头的设备
        if ([weakSelf validateBleName:peripheralName regular:@"^K[0-9]{4}$"]||[weakSelf validateBleName:peripheralName regular:@"^(B|b)(I|i)(X|x)(I|i)(N|n)(K|k)(E|e)(Y|y)[0-9]+$"]) {
            return YES;
        }
        return NO;
    }];
    
    // 查找的规则
    [self.babyBluetooth setFilterOnDiscoverPeripheralsAtChannel:channelOneKeyPeripheral
                                                         filter:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
                                                             // 最常用的场景是查找某一个前缀开头的设备
            if ([weakSelf validateBleName:peripheralName regular:@"^K[0-9]{4}$"]||[weakSelf validateBleName:peripheralName regular:@"^(B|b)(I|i)(X|x)(I|i)(N|n)(K|k)(E|e)(Y|y)[0-9]+$"]) {
                return YES;
            }
                                                             return NO;
                                                         }];
    
    //设置连接规则
    [self.babyBluetooth setFilterOnConnectToPeripheralsAtChannel:channelOneKeyPeripheral
                                                          filter:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
                                                              return NO;
                                                          }];
    
    //2.1-设备连接过滤器
    [self.babyBluetooth setFilterOnConnectToPeripherals:^BOOL(NSString *peripheralName, NSDictionary *advertisementData, NSNumber *RSSI) {
        //不自动连接
        return NO;
    }];
    
    //3-设置扫描到设备的委托
    [self.babyBluetooth setBlockOnDiscoverToPeripherals:^(CBCentralManager *central, CBPeripheral *peripheral, NSDictionary *advertisementData, NSNumber *RSSI) {
        dispatch_async(dispatch_get_main_queue(), ^{
            // 从block中取到值，再回到主线程
            if ([weakSelf respondsToSelector:@selector(scanResultPeripheral: advertisementData: rssi:)]) {
                [weakSelf scanResultPeripheral:peripheral advertisementData:advertisementData rssi:RSSI];
            }
        });
    }];
    
    BabyRhythm *rhythm = [[BabyRhythm alloc]init];
    //4-设置设备连接成功的委托,同一个baby对象，使用不同的channel切换委托回调
    [self.babyBluetooth setBlockOnConnectedAtChannel:channelOneKeyPeripheral
                                               block:^(CBCentralManager *central, CBPeripheral *peripheral) {
                                                   NSLog(@"【OKBabyBluetooth】->连接成功 %@",peripheral.name);
                                                   dispatch_async(dispatch_get_main_queue(), ^{
                                                       // 从block中取到值，再回到主线程
                                                       if ([weakSelf respondsToSelector:@selector(connectSuccess)]) {
                                                           [weakSelf connectSuccess];
                                                       }
                                                   });
                                               }];
    
    // 5-设置设备连接失败的委托
    [self.babyBluetooth setBlockOnFailToConnectAtChannel:channelOneKeyPeripheral
                                                   block:^(CBCentralManager *central, CBPeripheral *peripheral, NSError *error) {
                                                       NSLog(@"【OKBabyBluetooth】->连接失败");
                                                       dispatch_async(dispatch_get_main_queue(), ^{
                                                           // 从block中取到值，再回到主线程
                                                           if ([weakSelf respondsToSelector:@selector(connectFailed)]) {
                                                               [weakSelf connectFailed];
                                                           }
                                                       });
                                                   }];
    
    // 6-设置设备断开连接的委托
    [self.babyBluetooth setBlockOnDisconnectAtChannel:channelOneKeyPeripheral
                                                block:^(CBCentralManager *central, CBPeripheral *peripheral, NSError *error) {
                                                    NSLog(@"【OKBabyBluetooth】->设备：%@断开连接",peripheral.name);
                                                    dispatch_async(dispatch_get_main_queue(), ^{
                                                        // 从block中取到值，再回到主线程
                                                        if ([weakSelf respondsToSelector:@selector(disconnectPeripheral:)]) {
                                                            [weakSelf disconnectPeripheral:peripheral];
                                                        }
                                                    });
                                                }];
    
    // 7-设置发现设备的Services的委托
    [self.babyBluetooth setBlockOnDiscoverServicesAtChannel:channelOneKeyPeripheral
                                                      block:^(CBPeripheral *peripheral, NSError *error) {
                                                          [rhythm beats];
                                                      }];
    
    // 8-设置发现设service的Characteristics的委托
    [self.babyBluetooth setBlockOnDiscoverCharacteristicsAtChannel:channelOneKeyPeripheral
                                                             block:^(CBPeripheral *peripheral, CBService *service, NSError *error) {
    NSString *serviceUUID = [NSString stringWithFormat:@"%@",service.UUID];
        if ([serviceUUID isEqualToString:weakSelf.serverUUIDString]) {
            for (CBCharacteristic *ch in service.characteristics) {
                // 写数据的特征值
                NSString *chUUID = [NSString stringWithFormat:@"%@",ch.UUID];
                if ([chUUID isEqualToString:weakSelf.writeUUIDString]) {
                    weakSelf.writeCharacteristic = ch;
                }
                // 读数据的特征值
                if ([chUUID isEqualToString:weakSelf.readUUIDString]) {
                    weakSelf.readCharacteristic = ch;
                    [weakSelf.currentPeripheral setNotifyValue:YES
                                             forCharacteristic:weakSelf.readCharacteristic];
                }
                                                                         
            }
        }
    }];
    
    // 9-设置读取characteristics的委托
    [self.babyBluetooth setBlockOnReadValueForCharacteristicAtChannel:channelOneKeyPeripheral
                                                                block:^(CBPeripheral *peripheral, CBCharacteristic *characteristics, NSError *error) {
                                                                    dispatch_async(dispatch_get_main_queue(), ^{
                                                                        // 从block中取到值，再回到主线程
                                                                        if ([weakSelf respondsToSelector:@selector(readData:)]) {
                                                                            [weakSelf readData:characteristics.value];
                                                                        }
                                                                    });
                                                                }];
    
    // 设置发现characteristics的descriptors的委托
    [self.babyBluetooth setBlockOnDiscoverDescriptorsForCharacteristicAtChannel:channelOneKeyPeripheral
                                                                          block:^(CBPeripheral *peripheral, CBCharacteristic *characteristic, NSError *error) {
        NSLog(@"setBlockOnDiscoverDescriptorsForCharacteristicAtChannel = %@",characteristic);
        
    }];
    
    // 设置读取Descriptor的委托
    [self.babyBluetooth setBlockOnReadValueForDescriptorsAtChannel:channelOneKeyPeripheral
                                                             block:^(CBPeripheral *peripheral, CBDescriptor *descriptor, NSError *error) {
        NSLog(@"descriptor %@",descriptor);
    }];
    
    
    [self.babyBluetooth setBlockOnDidUpdateNotificationStateForCharacteristicAtChannel:channelOneKeyPeripheral block:^(CBCharacteristic *characteristic, NSError *error) {
        NSLog(@"setBlockOnDidUpdateNotificationStateForCharacteristicAtChannel = %@",characteristic);
    }];
    
    
    // 读取rssi的委托
    [self.babyBluetooth setBlockOnDidReadRSSI:^(NSNumber *RSSI, NSError *error) { }];
    
    // 设置beats break委托
    [rhythm setBlockOnBeatsBreak:^(BabyRhythm *bry) { }];
    
    // 设置beats over委托
    [rhythm setBlockOnBeatsOver:^(BabyRhythm *bry) { }];
    
    // 扫描选项->CBCentralManagerScanOptionAllowDuplicatesKey:忽略同一个Peripheral端的多个发现事件被聚合成一个发现事件
    NSDictionary *scanForPeripheralsWithOptions = @{CBCentralManagerScanOptionAllowDuplicatesKey:@YES};
    /*连接选项->
     CBConnectPeripheralOptionNotifyOnConnectionKey :当应用挂起时，如果有一个连接成功时，如果我们想要系统为指定的peripheral显示一个提示时，就使用这个key值。
     CBConnectPeripheralOptionNotifyOnDisconnectionKey :当应用挂起时，如果连接断开时，如果我们想要系统为指定的peripheral显示一个断开连接的提示时，就使用这个key值。
     CBConnectPeripheralOptionNotifyOnNotificationKey:
     当应用挂起时，使用该key值表示只要接收到给定peripheral端的通知就显示一个提
     */
    NSDictionary *connectOptions = @{CBConnectPeripheralOptionNotifyOnConnectionKey:@YES,
                                     CBConnectPeripheralOptionNotifyOnDisconnectionKey:@YES,
                                     CBConnectPeripheralOptionNotifyOnNotificationKey:@YES};
    
    [self.babyBluetooth setBabyOptionsAtChannel:channelOneKeyPeripheral
                  scanForPeripheralsWithOptions:scanForPeripheralsWithOptions
                   connectPeripheralWithOptions:connectOptions
                 scanForPeripheralsWithServices:nil
                           discoverWithServices:nil
                    discoverWithCharacteristics:nil];
    
    // 连接设备
    [self.babyBluetooth setBabyOptionsWithScanForPeripheralsWithOptions:scanForPeripheralsWithOptions
                                           connectPeripheralWithOptions:nil
                                         scanForPeripheralsWithServices:nil
                                                   discoverWithServices:nil
                                            discoverWithCharacteristics:nil];
}

#pragma mark 对蓝牙操作
/// 蓝牙状态
- (void)systemBluetoothState:(CBManagerState)state  API_AVAILABLE(ios(10.0)) {
    if (state == CBManagerStatePoweredOn) {
        if ([self.delegate respondsToSelector:@selector(sysytemBluetoothOpen)]) {
            [self.delegate sysytemBluetoothOpen];
        }
    }else if (state == CBManagerStatePoweredOff) {
        if ([self.delegate respondsToSelector:@selector(systemBluetoothClose)]) {
            [self.delegate systemBluetoothClose];
        }
    }
}

/// 开始扫描
- (void)startScanPeripheral {
    self.babyBluetooth.scanForPeripherals().begin();
}

/// 扫描到的设备[由block回主线程]
- (void)scanResultPeripheral:(CBPeripheral *)peripheral advertisementData:(NSDictionary *)advertisementData rssi:(NSNumber *)RSSI {
    for (OKPeripheralInfo *peripheralInfo in self.peripheralArr) {
        if ([peripheralInfo.peripheral.identifier isEqual:peripheral.identifier]) {
            return;
        }
    }
    
    OKPeripheralInfo *peripheralInfo = [[OKPeripheralInfo alloc] init];
    peripheralInfo.peripheral = peripheral;
    peripheralInfo.advertisementData = advertisementData;
    peripheralInfo.RSSI = RSSI;
    [self.peripheralArr addObject:peripheralInfo];
    
    if ([self.delegate respondsToSelector:@selector(getScanResultPeripherals:)]) {
        [self.delegate getScanResultPeripherals:self.peripheralArr];
    }
}


/// 停止扫描
- (void)stopScanPeripheral {
    [self.peripheralArr removeAllObjects];
    [self.babyBluetooth cancelScan];
}


/// 连接设备
-(void)connectPeripheral:(CBPeripheral *)peripheral {
    NSLog(@"connectPeripheral");
    // 断开之前的所有连接
    [self.babyBluetooth cancelAllPeripheralsConnection];
    self.currentPeripheral = peripheral;
    self.babyBluetooth.having(peripheral).and.channel(channelOneKeyPeripheral).
    then.connectToPeripherals().discoverServices().
    discoverCharacteristics().readValueForCharacteristic().
    discoverDescriptorsForCharacteristic().
    readValueForDescriptors().begin();
}


/// 连接成功[由block回主线程]
- (void)connectSuccess {
    if ([self.delegate respondsToSelector:@selector(connectSuccess)]) {
        [self.delegate connectSuccess];
    }
}


/// 连接失败[由block回主线程]
- (void)connectFailed {
    if ([self.delegate respondsToSelector:@selector(connectFailed)]) {
        [self.delegate connectFailed];
    }
}


/// 获取当前断开的设备[由block回主线程]
- (void)disconnectPeripheral:(CBPeripheral *)peripheral {
    if ([self.delegate respondsToSelector:@selector(disconnectPeripheral:)]) {
        [self.delegate disconnectPeripheral:peripheral];
    }
}


/// 获取当前连接
- (NSArray *)getCurrentPeripherals {
    return [self.babyBluetooth findConnectedPeripherals];
}


///获取设备的服务跟特征值[当已连接成功时]
- (void)searchServerAndCharacteristicUUID {
    self.babyBluetooth.having(self.currentPeripheral).and.channel(channelOneKeyPeripheral).
    then.connectToPeripherals().discoverServices().discoverCharacteristics()
    .readValueForCharacteristic().discoverDescriptorsForCharacteristic().
    readValueForDescriptors().begin();
}


///断开所有连接
- (void)disconnectAllPeripherals {
    [self.babyBluetooth cancelAllPeripheralsConnection];
}


///断开当前连接
- (void)disconnectLastPeripheral:(CBPeripheral *)peripheral {
    [self.babyBluetooth cancelPeripheralConnection:peripheral];
}


///发送数据
- (void)write:(NSData *)msgData {
    if (self.writeCharacteristic == nil) {
        NSLog(@"【OKBabyBluetooth】->数据发送失败");
        return;
    }
    
    //若最后一个参数是CBCharacteristicWriteWithResponse
    //则会进入setBlockOnDidWriteValueForCharacteristic委托
    [self.currentPeripheral writeValue:msgData
                     forCharacteristic:self.writeCharacteristic
                                  type:CBCharacteristicWriteWithoutResponse];
}


///读取数据
- (void)readData:(NSData *)valueData {
    self.currentReadDataStr = [NSData hexStringForData:valueData];
    if ([self.delegate respondsToSelector:@selector(readData:)]) {
        [self.delegate readData:valueData];
    }
}


- (void)characteristicWrite:(NSString *)str
{
    if (self.writeCharacteristic) {
        NSData *data = [NSData dataForHexString:str];
        if (data == nil) {
            return;
        }
        [self.currentPeripheral writeValue:data forCharacteristic:self.writeCharacteristic type:CBCharacteristicWriteWithResponse];
    }
}
- (NSString *)characteristicRead
{
    if (self.currentReadDataStr) {
        return self.currentReadDataStr;
    }
    return nil;
}

- (BOOL)validateBleName:(NSString *)textString regular:(NSString *)regularStr
{
    NSPredicate *numberPre = [NSPredicate predicateWithFormat:@"SELF MATCHES %@",regularStr];
    return [numberPre evaluateWithObject:textString];
}
- (NSString *)getStrValueInUD
{
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    return [ud stringForKey:kSavedUUIDForLastBlueTooth];
}
- (void)saveStrValueInUD:(NSString *)bleUUID
{
    [[NSUserDefaults standardUserDefaults]setObject:bleUUID forKey:kSavedUUIDForLastBlueTooth];
    [[NSUserDefaults standardUserDefaults]synchronize];
}
- (NSNotificationCenter *) getNotificationCenter {
    return [NSNotificationCenter defaultCenter];
}
@end
