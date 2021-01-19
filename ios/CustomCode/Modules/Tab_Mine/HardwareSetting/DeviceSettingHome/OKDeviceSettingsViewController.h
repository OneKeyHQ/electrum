//
//  OKDeviceSettingsViewController.h
//  OneKey
//
//  Created by liuzj on 07/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class OKHardwareListBaseCellModel;
@class OKDeviceModel;

@interface OKDeviceSettingsModel : NSObject
@property (nonatomic, copy) NSString* sectionTitle;
@property (nonatomic, strong) NSArray <OKHardwareListBaseCellModel *>* cellModel;
@end

@interface OKDeviceSettingsViewController : BaseViewController
@property (nonatomic, strong) OKDeviceModel* deviceModel;
+ (instancetype)deviceSettingsViewController;
@end

NS_ASSUME_NONNULL_END
