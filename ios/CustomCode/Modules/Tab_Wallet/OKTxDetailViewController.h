//
//  OKTxDetailViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKTxTableViewCellModel;
@interface OKTxDetailViewController : BaseViewController
@property (nonatomic,copy)NSString *txDate;
@property (nonatomic,copy)NSString *tx_hash;
@property (nonatomic,strong)OKTxTableViewCellModel *model;
+ (instancetype)txDetailViewController;

@end

NS_ASSUME_NONNULL_END
