//
//  OKTokenAssetModel.h
//  OneKey
//
//  Created by xiaoliang on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN

@interface OKTokenAssetModel : NSObject
@property (nonatomic,copy)NSString *coin;
@property (nonatomic,copy)NSString *address;
@property (nonatomic,copy)NSString *balance;
@property (nonatomic,copy)NSString *fiat;
@end

NS_ASSUME_NONNULL_END
