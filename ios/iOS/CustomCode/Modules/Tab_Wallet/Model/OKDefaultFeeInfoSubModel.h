//
//  OKDefaultFeeInfoSubModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
/**
 {"size":65,"feerate":20.010000000000002,"fiat":"1.56 CNY","time":250,"fee":"0.00001301"}
 */
@interface OKDefaultFeeInfoSubModel : NSObject
@property (nonatomic,copy) NSString* size;
@property (nonatomic,copy) NSString* feerate;
@property (nonatomic,copy) NSString* fiat;
@property (nonatomic,copy) NSString* time;
@property (nonatomic,copy) NSString* fee;
@end

NS_ASSUME_NONNULL_END
