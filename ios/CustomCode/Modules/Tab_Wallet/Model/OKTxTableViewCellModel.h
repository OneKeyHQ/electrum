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
@property (nonatomic,copy) NSString* addressStr;
@property (nonatomic,copy) NSString* imageStr;
@property (nonatomic,copy) NSString* timeStr;
@property (nonatomic,copy) NSString* amountStr;
@property (nonatomic,copy) NSString* statusStr;

@end

NS_ASSUME_NONNULL_END
