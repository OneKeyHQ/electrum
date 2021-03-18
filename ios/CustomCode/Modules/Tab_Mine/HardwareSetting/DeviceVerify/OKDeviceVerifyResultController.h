//
//  OKDeviceVerifyResultController.h
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, OKDeviceVerifyResult) {
    OKDeviceVerifyResultPass,
    OKDeviceVerifyResultFail,
    OKDeviceVerifyResultNetworkError
};

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceVerifyResultController : BaseViewController
@property (nonatomic, assign) OKDeviceVerifyResult verifyResult;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, copy) void(^doneCallback)(void);
+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
