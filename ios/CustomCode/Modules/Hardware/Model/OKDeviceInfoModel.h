//
//  OKDeviceInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/13.
//  Copyright © 2021 Onekey. All rights reserved.
//

/**
 {
     "auto_lock_delay_ms" = 1800000;
     "backup_only" = 0;
     "ble_enable" = 1;
     "ble_name" = K8543;
     "ble_ver" = "1.1.8";
     "bootloader_hash" = b532d75a3c385d73ba58b82991e836d126eab45bb387100bc6b4f74805b09fb0;
     capabilities =     (
         Bitcoin,
         "Bitcoin_like",
         Crypto,
         Ethereum,
         Lisk,
         NEM,
         Stellar,
         U2F
     );
     "device_id" = 398F8DF436D2C78513E9580C;
     initialized = 1;
     label = "BIXIN KEY";
     language = chinese;
     "major_version" = 1;
     "minor_version" = 9;
     model = 1;
     "needs_backup" = 0;
     "no_backup" = 0;
     "passphrase_protection" = 1;
     "patch_version" = 8;
     "pin_protection" = 1;
     revision = 450a9d866b388d9fbb3d0e65d8b1211e59c31273;
     "se_enable" = 0;
     "se_ver" = "1.0.0.4";
     "unfinished_backup" = 0;
     unlocked = 1;
     vendor = "trezor.io";
     "wipe_code_protection" = 0;
 }
 
 */


#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceInfoModel : NSObject
@property (nonatomic,copy)NSString *auto_lock_delay_ms;
@property (nonatomic,copy)NSString *backup_only;
@property (nonatomic,copy)NSString *ble_enable;
@property (nonatomic,copy)NSString *ble_name;
@property (nonatomic,copy)NSString *ble_ver;
@property (nonatomic,copy)NSString *bootloader_hash;
@property (nonatomic,strong)NSArray *capabilities;
@property (nonatomic,copy)NSString *device_id;
@property (nonatomic,copy)NSString *initialized;
@property (nonatomic,copy)NSString *label;
@property (nonatomic,copy)NSString *language;
@property (nonatomic,copy)NSString *major_version;
@property (nonatomic,copy)NSString *minor_version;
@property (nonatomic,copy)NSString *model;
@property (nonatomic,copy)NSString *needs_backup;
@property (nonatomic,copy)NSString *passphrase_protection;
@property (nonatomic,copy)NSString *patch_version;
@property (nonatomic,copy)NSString *pin_protection;
@property (nonatomic,copy)NSString *revision;
@property (nonatomic,copy)NSString *se_enable;
@property (nonatomic,copy)NSString *se_ver;
@property (nonatomic,copy)NSString *unfinished_backup;
@property (nonatomic,copy)NSString *unlocked;
@property (nonatomic,copy)NSString *vendor;
@property (nonatomic,copy)NSString *wipe_code_protection;
@end

NS_ASSUME_NONNULL_END
