//
//  OKTxTableViewCellModel.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
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
@end

NS_ASSUME_NONNULL_END
