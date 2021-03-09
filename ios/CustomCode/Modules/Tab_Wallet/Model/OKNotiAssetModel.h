//
//  OKNotiAssetModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKTokenAssetModel.h"
NS_ASSUME_NONNULL_BEGIN

@interface OKNotiAssetModel : NSObject
@property (nonatomic,copy)NSString *coin;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *balance;
@property (nonatomic,copy)NSString *fiat;
@property (nonatomic,copy)NSString *sum_fiat;
@property (nonatomic,strong)NSArray *tokens;

@end

NS_ASSUME_NONNULL_END
