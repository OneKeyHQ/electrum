//
//  OKSendTxPreModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/9.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKSendTxPreModel : NSObject
@property (nonatomic,copy) NSString* coinType;
@property (nonatomic,copy) NSString* amount;
@property (nonatomic,copy) NSString* sendAddress;
@property (nonatomic,copy) NSString* rAddress;
@property (nonatomic,copy) NSString* walletName;
@property (nonatomic,copy) NSString* txType;
@property (nonatomic,copy) NSString* fee;
@end

NS_ASSUME_NONNULL_END
