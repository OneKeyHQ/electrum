//
//  OKBluetoothViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/18.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKBluetoothViewController : BaseViewController
@property (nonatomic, strong) NSMutableArray *dataSource;
+ (instancetype)bluetoothViewController;
@end

NS_ASSUME_NONNULL_END
