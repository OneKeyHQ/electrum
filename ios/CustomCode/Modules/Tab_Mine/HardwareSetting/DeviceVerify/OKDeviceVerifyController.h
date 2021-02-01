//
//  OKDeviceVerifyController.h
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^OKVerifyResultCallback)(BOOL isPass);

typedef NS_ENUM(NSUInteger, OKDeviceVerifyPhase) {
    OKDeviceVerifyPhaseBegin = 0,
    OKDeviceVerifyPhaseConnecting,
    OKDeviceVerifyPhaseFetching,
    OKDeviceVerifyPhaseSubmitting,
    OKDeviceVerifyPhaseDone,
};

@interface OKDeviceVerifyController : BaseViewController
+ (instancetype)controllerWithStoryboard;
@property (nonatomic, assign) OKDeviceVerifyPhase phase;
@property (nonatomic, strong) NSString *deviceId;
@property (nonatomic, copy) OKVerifyResultCallback resultCallback;
@end

NS_ASSUME_NONNULL_END
