//
//  OKBlueManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/19.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BabyBluetooth.h"
#import "OKDeviceInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef enum {
    OKMatchingTypeNone, //其它
    OKMatchingTypeActivation, //激活
    OKMatchingTypeBackup2Hw,  //备份到硬件
    OKMatchingTypeTransfer,   //转账
    OKMatchingTypeReceiveCoin, //收款
    OKMatchingTypeSignatureData, //签名数据
}OKMatchingType;

typedef enum {
    OKMatchingFromWhereNav, //导航
    OKMatchingFromWhereDis  //模态
}OKMatchingFromWhere;


#define kOKBlueManager (OKBlueManager.sharedInstance)

#define kPRIMARY_SERVICE                     @"0001"
#define kWRITE_CHARACTERISTIC                @"0002"
#define kREAD_CHARACTERISTIC                 @"0003"
#define kDEVICEINFOCHARACTERISTIC            @"Firmware Revision String"
#define kDEVICESOFTWARECHARACTERISTIC        @"Software Revision String"
#define kDEVICEINFOSERVICE                   @"Device Information"
#define kIOSMINIMUMBLUETOOTHVERSION          @"1.1.8"
#define kBleUpdatedURL                       @"https://firmware.onekey.so/"
#define kIOSFirmwareSysVersin                @"2.0.4"
@interface OKPeripheralInfo : NSObject

@property (nonatomic, strong) NSNumber     *RSSI;
@property (nonatomic, strong) CBPeripheral *peripheral;
@property (nonatomic, strong) NSDictionary *advertisementData;

@end


@protocol OKBabyBluetoothManageDelegate <NSObject>

@optional

/**
 蓝牙被关闭
 */
- (void)systemBluetoothClose;


/**
 蓝牙已开启
 */
- (void)sysytemBluetoothOpen;


/**
 扫描到的设备回调

 @param peripheralInfoArr 扫描到的蓝牙设备数组
 */
- (void)getScanResultPeripherals:(NSArray *)peripheralInfoArr;


/**
 连接成功
 */
- (void)connectSuccess;


/**
 连接失败
 */
- (void)connectFailed;


/**
 当前断开的设备

 @param peripheral 断开的peripheral信息
 */
- (void)disconnectPeripheral:(CBPeripheral *)peripheral;


/**
 读取蓝牙数据

 @param valueData 蓝牙设备发送过来的data数据
 */
- (void)readData:(NSData *)valueData;

- (void)subscribeComplete:(NSDictionary *)jsonDict characteristic:(CBCharacteristic *)ch;


@end

typedef void(^ConnectedComplete)(BOOL isSuccess);

@interface OKBlueManager : NSObject
//外设的服务UUID值
@property (nonatomic, copy) NSString *serverUUIDString;
//外设的写入UUID值
@property (nonatomic, copy) NSString *writeUUIDString;
//外设的读取UUID值
@property (nonatomic, copy) NSString *readUUIDString;

@property (nonatomic,copy)NSString *currentReadDataStr;
@property (nonatomic,strong)CBCharacteristic *deviceCharacteristic;
@property (nonatomic,strong)CBCharacteristic *softwareCharacteristic;
+ (OKBlueManager *)sharedInstance;
- (BOOL)isBluetoothLowVersion;
- (NSString *)getStrValueInUD;
- (void)saveStrValueInUD:(NSString *)bleUUID;
- (BOOL)isConnectedCurrentDevice;
- (BOOL)isConnectedName:(NSString *)name;
- (void)startScanAndConnectWithName:(NSString *)name complete:(ConnectedComplete)complete;
@property (nonatomic,copy)NSString *currentDeviceID;
@property (nonatomic,strong)CBPeripheral *currentPeripheral;
@property (nonatomic, weak) id<OKBabyBluetoothManageDelegate> delegate;
- (CBCentralManager *)centralManager;

/**
 开始扫描周边蓝牙设备
 */
- (void)startScanPeripheral;


/**
 停止扫描
 */
- (void)stopScanPeripheral;


/**
 连接所选取的蓝牙外设

 @param peripheral 所选择蓝牙外设的perioheral
 */
-(void)connectPeripheral:(CBPeripheral *)peripheral;


/**
 获取当前连接成功的蓝牙设备数组

 @return 返回当前所连接成功蓝牙设备数组
 */
- (NSArray *)getCurrentPeripherals;


/**
 获取设备的服务跟特征值
 当已连接成功时调用有效
 */
- (void)searchServerAndCharacteristicUUID;


/**
 断开当前连接的所有蓝牙设备
 */
- (void)disconnectAllPeripherals;


/**
 断开所选择的蓝牙设备

 @param peripheral 所选择蓝牙外设的perioheral
 */
- (void)disconnectLastPeripheral:(CBPeripheral *)peripheral;

/**
 向蓝牙设备发送数据

 @param msgData 数据data值
 */
- (void)write:(NSData *)msgData;

- (void)characteristicWrite:(NSString *)str;
- (NSString *)characteristicRead;
-(NSNotificationCenter *) getNotificationCenter;
@end

NS_ASSUME_NONNULL_END
