//
//  OKDeviceDeleteController.h
//  OneKey
//
//  Created by liuzj on 2021/1/13.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceDeleteController : UIViewController
@property (nonatomic, copy) void(^deleteDeviceCallback)(void);
@property (nonatomic, copy) void(^dismissCallback)(void); // Optional.

+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
