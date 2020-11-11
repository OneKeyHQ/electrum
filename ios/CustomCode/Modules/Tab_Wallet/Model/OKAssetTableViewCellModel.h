//
//  OKAssetTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/4.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKAssetTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* iconImage;
@property (nonatomic,copy) NSString* coinType;
@property (nonatomic,copy) NSString* balance;
@property (nonatomic,copy) NSString* money;
@end

NS_ASSUME_NONNULL_END
