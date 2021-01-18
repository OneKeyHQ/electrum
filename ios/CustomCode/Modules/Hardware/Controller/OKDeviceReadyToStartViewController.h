//
//  OKDeviceReadyToStartViewController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceReadyToStartViewController : BaseViewController
+ (instancetype)deviceReadyToStartViewController;
@property (nonatomic,copy)NSString *deviceName;
@end

NS_ASSUME_NONNULL_END
