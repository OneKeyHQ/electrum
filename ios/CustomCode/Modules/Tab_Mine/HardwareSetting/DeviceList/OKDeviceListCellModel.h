//
//  OKDeviceListHardwareModel.h
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKHardwareListBaseCell.h"
#import "OKDeviceModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceListCellModel : OKHardwareListBaseCellModel
@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) OKDeviceModel *devcie;
@end


NS_ASSUME_NONNULL_END
