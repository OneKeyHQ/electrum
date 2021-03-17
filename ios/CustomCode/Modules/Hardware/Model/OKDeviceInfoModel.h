//
//  OKDeviceInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/13.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceInfoModel : NSObject
@property (nonatomic, copy) NSString *vendor;
@property (nonatomic, assign) NSInteger major_version; // 主版本号
@property (nonatomic, assign) NSInteger minor_version; // 次版本号
@property (nonatomic, assign) NSInteger patch_version; // 修订号    硬件的软件版本(俗称固件，在2.0.1 之前使用)
@property (nonatomic, assign) BOOL bootloader_mode; // 设备当时是不是在bootloader模式
@property (nonatomic, copy) NSString *device_id; // 设备唯一标识，设备恢复出厂设置这个值会变
@property (nonatomic, assign) BOOL pin_protection; // 是否开启了PIN码保护，
@property (nonatomic, assign) BOOL passphrase_protection; // 这个用来支持创建隐藏钱包
@property (nonatomic, copy) NSString *language;
@property (nonatomic, copy) NSString *label; // 激活钱包时，使用的名字
@property (nonatomic, assign) BOOL initialized; // 当时设备是否激活
@property (nonatomic, copy) NSString *revision;
@property (nonatomic, copy) NSString *bootloader_hash;
@property (nonatomic, assign) BOOL imported;
@property (nonatomic, assign) BOOL unlocked;
@property (nonatomic, assign) BOOL firmware_present;
@property (nonatomic, assign) BOOL needs_backup;
@property (nonatomic, assign) NSInteger flags;
@property (nonatomic, copy) NSString *model;
@property (nonatomic, assign) NSInteger fw_major;
@property (nonatomic, assign) NSInteger fw_minor;
@property (nonatomic, assign) NSInteger fw_patch;
@property (nonatomic, copy) NSString *fw_vendor;
@property (nonatomic, copy) NSString *fw_vendor_keys;
@property (nonatomic, assign) BOOL unfinished_backup;
@property (nonatomic, assign) BOOL no_backup;
@property (nonatomic, assign) BOOL recovery_mode;
@property (nonatomic, assign) BOOL sd_card_present;
@property (nonatomic, assign) BOOL sd_protection;
@property (nonatomic, assign) BOOL wipe_code_protection;
@property (nonatomic, copy) NSString *session_id;
@property (nonatomic, assign) BOOL passphrase_always_on_device;
@property (nonatomic, assign) NSInteger auto_lock_delay_ms; // 自动关机时间
@property (nonatomic, assign) NSInteger display_rotation;
@property (nonatomic, assign) BOOL experimental_features;
@property (nonatomic, assign) NSInteger offset; // 升级时断点续传使用的字段
@property (nonatomic, copy) NSString *ble_name;
@property (nonatomic, copy) NSString *ble_ver; // 蓝牙固件版本
@property (nonatomic, assign) BOOL ble_enable;
@property (nonatomic, assign) BOOL se_enable;
@property (nonatomic, copy) NSString *se_ver; // se的版本
@property (nonatomic, assign) BOOL backup_only; // 是否是特殊设备，只用来备份，没有额外功能支持
@property (nonatomic, copy) NSString *onekey_version; // 硬件的软件版本（俗称固件），仅供APP使用（从2.0.1开始加入）
@property (nonatomic, strong)NSArray *capabilities;
@property (nonatomic, copy) NSString *serial_num; // since 2.0.7

@property (nonatomic, copy) NSString *deviceSysVersion; // 合成的系统固件版本号 i.e. @"1.2.3"
@property (nonatomic, assign) BOOL verifiedDevice;
@end

NS_ASSUME_NONNULL_END
