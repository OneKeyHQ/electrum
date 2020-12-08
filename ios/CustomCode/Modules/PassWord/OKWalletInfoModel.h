//
//  OKWalletInfoModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletInfoModel : NSObject
@property (nonatomic,copy) NSString* label;
@property (nonatomic,copy) NSString* device_id;
@property (nonatomic,copy) NSString* type;
@property (nonatomic,copy) NSString* addr;
@property (nonatomic,copy) NSString* name;
@property (nonatomic,copy) NSString* coinType;

@end

NS_ASSUME_NONNULL_END
