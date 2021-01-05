//
//  OKTxTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKTxTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* amount;
@property (nonatomic,copy) NSString* tx_hash;
@property (nonatomic,copy) NSString* date;
@property (nonatomic,copy) NSString* message;
@property (nonatomic,copy) NSString* confirmations;
@property (nonatomic,strong) NSNumber* is_mine;
@property (nonatomic,copy) NSString* history;
@property (nonatomic,copy) NSString* tx_status;
@property (nonatomic,copy) NSString* address;
@end

NS_ASSUME_NONNULL_END
