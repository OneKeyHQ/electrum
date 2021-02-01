//
//  OKDeviceModel.h
//  OneKey
//
//  Created by liuzhijie on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKDeviceInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKDeviceModel : NSObject
@property (nonatomic, strong) OKDeviceInfoModel *deviceInfo;
@property (nonatomic, strong, readonly) NSMutableDictionary *json;
@property (nonatomic, assign) BOOL verifiedDevice;

- (instancetype)initWithJson:(NSDictionary *)json;
- (BOOL)updateWithDict:(NSDictionary *)newDict; // 如果不同用 newDict 更新原来对象，返回 YES，如果相同不需要改变返回 NO
@end

NS_ASSUME_NONNULL_END
