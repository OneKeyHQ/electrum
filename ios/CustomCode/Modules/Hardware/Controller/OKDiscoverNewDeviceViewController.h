//
//  OKDiscoverNewDeviceViewController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDiscoverNewDeviceViewController : BaseViewController
@property (nonatomic,assign)OKMatchingFromWhere where;
+ (instancetype)discoverNewDeviceViewController;
@end

NS_ASSUME_NONNULL_END
