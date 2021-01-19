//
//  OKDeviceInfoViewController.h
//  OneKey
//
//  Created by liuzj on 08/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKDeviceModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceInfoViewController : BaseViewController
@property (nonatomic, strong) OKDeviceModel* deviceModel;
+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
