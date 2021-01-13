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
