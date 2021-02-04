//
//  OKVerifyOnTheDeviceController.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "BaseViewController.h"

typedef enum {
    OKVerifyOnTheDeviceTypeBackupSetPin,
    OKVerifyOnTheDeviceTypeBackupActiveSuccess,
    OKVerifyOnTheDeviceTypeNormalActiveSuccess
}OKVerifyOnTheDeviceType;

NS_ASSUME_NONNULL_BEGIN

@interface OKVerifyOnTheDeviceController : BaseViewController
+ (instancetype)verifyOnTheDeviceController:(OKVerifyOnTheDeviceType)type;
@property (nonatomic,copy)NSString *deviceName;
@property (nonatomic,copy)NSString *words;
@property (nonatomic,assign)OKMatchingFromWhere where;
@end

NS_ASSUME_NONNULL_END
