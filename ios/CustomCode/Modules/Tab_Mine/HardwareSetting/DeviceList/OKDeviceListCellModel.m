//
//  OKDeviceListHardwareModel.m
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceListCellModel.h"

@implementation OKDeviceListCellModel

- (void)setName:(NSString *)name {
    _name = name;
    self.title = name;
}
@end


