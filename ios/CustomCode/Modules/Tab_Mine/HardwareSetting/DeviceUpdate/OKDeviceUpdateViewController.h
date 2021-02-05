//
//  OKDeviceUpdateViewController.h
//  OneKey
//
//  Created by liuzj on 09/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


typedef NS_ENUM(NSUInteger, OKDeviceUpdateType) {
    OKDeviceUpdateTypeFramework = 0,
    OKDeviceUpdateTypeBluetooth,
    OKDeviceUpdateTypeAlreadyUpToDate,
};

typedef NS_ENUM(NSUInteger, OKDeviceFirmwareInstallMode) {
    OKDeviceFirmwareInstallModeNormal = 0,
    OKDeviceFirmwareInstallModeBootloader,
    OKDeviceFirmwareInstallModeBLEDFU,
};

typedef void(^OKDeviceUpdateCellCallback)(OKDeviceUpdateType type, NSString *url);

@interface OKDeviceUpdateCell : UITableViewCell
@property (nonatomic, assign) OKDeviceUpdateType updateType;
@property (nonatomic, copy) NSString* versionDesc;
@property (nonatomic, copy) NSString* updateDesc;
@property (nonatomic, copy) NSString* updateUrl;
@property (nonatomic, copy) OKDeviceUpdateCellCallback updateClickCallback;
@end

@interface OKDeviceAlreadyUpToDateCell : UITableViewCell
@end

@interface OKDeviceUpdateViewController : BaseViewController
@property (nonatomic, assign) OKDeviceFirmwareInstallMode mode;
@property (nonatomic, strong) NSString *deviceId;
+ (instancetype)controllerWithStoryboard;

@end

NS_ASSUME_NONNULL_END
