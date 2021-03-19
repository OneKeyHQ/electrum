//
//  OKNotiAssetModel.m
//  OneKey
//
//  Created by xiaoliang on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKNotiAssetModel.h"

@implementation OKNotiAssetModel
+(NSDictionary *)mj_objectClassInArray
{
    return @{@"tokens":[OKTokenAssetModel class]};
}
@end
