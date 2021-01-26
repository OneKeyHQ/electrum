//
//  OKHardwareListBaseCell.h
//  OneKey
//
//  Created by liuzj on 07/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSUInteger, OKHardwareListBaseCellType) {
    OKHardwareListBaseCellTypeNone = 0,
    OKHardwareListBaseCellTypeDevice,
    OKHardwareListBaseCellTypeDeviceInfo,
    OKHardwareListBaseCellTypeDeviceLanguage,
    OKHardwareListBaseCellTypeDeviceUpdate,
    OKHardwareListBaseCellTypeDeviceAutoOff,
    OKHardwareListBaseCellTypeDeviceVerify,
    OKHardwareListBaseCellTypeDeviceChangePIN,
    OKHardwareListBaseCellTypeDeviceShowXPUB,
    OKHardwareListBaseCellTypeDeviceHidden,
    OKHardwareListBaseCellTypeDeviceReset,
    OKHardwareListBaseCellTypeDeviceDelete,
};


@interface OKHardwareListBaseCellModel : NSObject
@property (nonatomic, assign) OKHardwareListBaseCellType cellType;
@property (nonatomic, copy) NSString* imageName;
@property (nonatomic, copy) NSString* title;
@property (nonatomic, assign) NSUInteger titleColor;
@property (nonatomic, copy) NSString* details;
@property (nonatomic, assign) bool hideRightArrow;
@property (nonatomic, assign) NSUInteger tagTextColor;
@property (nonatomic, assign) NSUInteger tagBgColor;
@property (nonatomic, copy) NSString* tagText;
@end


@interface OKHardwareListBaseCell : UITableViewCell
@property (nonatomic, strong)OKHardwareListBaseCellModel *model;
@end

NS_ASSUME_NONNULL_END
