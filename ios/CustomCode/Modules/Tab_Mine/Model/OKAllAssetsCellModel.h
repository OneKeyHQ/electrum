//
//  OKAllAssetsCellModel.h
//  OneKey
//
//  Created by zj on 2021/3/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKAllAssetsCellModel : NSObject
@property (nonatomic, copy)NSString *coin;
@property (nonatomic, copy)NSString *balance;
@property (nonatomic, copy)NSString *fiat;
@property (nonatomic, copy)NSString *address;
@end

@interface OKAllAssetsSectionModel : NSObject <NSCopying>
@property (nonatomic, copy)NSString *name;
@property (nonatomic, copy)NSString *btc;
@property (nonatomic, copy)NSString *fiat;
@property (nonatomic, copy)NSString *label;
@property (nonatomic, copy)NSArray <OKAllAssetsCellModel *> *wallets;
@end

NS_ASSUME_NONNULL_END
