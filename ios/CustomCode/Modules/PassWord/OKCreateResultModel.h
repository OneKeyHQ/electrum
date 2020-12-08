//
//  OKCreateResultModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKCreateResultModel : NSObject
@property (nonatomic,copy) NSString * seed;
@property (nonatomic,strong) NSArray * wallet_info;
@property (nonatomic,strong) NSArray * derived_info;
@end

NS_ASSUME_NONNULL_END
