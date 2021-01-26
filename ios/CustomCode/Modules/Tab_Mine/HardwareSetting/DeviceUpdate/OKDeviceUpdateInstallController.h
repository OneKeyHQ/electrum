//
//  OKDeviceUpdateDownloadController.h
//  OneKey
//
//  Created by liuzj on 2021/1/12.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OKDeviceUpdateViewController.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, OKDeviceUpdateInstallPhase) {
    OKDeviceUpdateInstallPhaseBegin = 0,
    OKDeviceUpdateInstallPhaseDownloading,
    OKDeviceUpdateInstallPhaseInstalling,
    OKDeviceUpdateInstallPhaseDone,
};

@interface OKDeviceUpdateInstallController : BaseViewController
@property (nonatomic, copy) NSString* framewareDownloadURL;
@property (assign, nonatomic) OKDeviceUpdateInstallPhase phase;
@property (assign, nonatomic) OKDeviceUpdateType type;
@property (nonatomic, copy) void(^doneCallback)(BOOL sucess); // Optional.

+ (instancetype)controllerWithStoryboard;

@end

NS_ASSUME_NONNULL_END
