//
//  OKDeviceConfirmController.h
//  OneKey
//
//  Created by liuzhijie on 2021/1/25.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceConfirmController : BaseViewController
@property (nonatomic, strong) NSString* titleText;
@property (nonatomic, strong) NSString* descText;
@property (nonatomic, strong) NSString* btnText;
@property (nonatomic, copy) void(^btnCallback)(void);
+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
