//
//  OKTxDetailViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKTxDetailViewController : BaseViewController
@property (nonatomic,copy)NSString *txDate;
@property (nonatomic,copy)NSString *tx_hash;
+ (instancetype)txDetailViewController;

@end

NS_ASSUME_NONNULL_END
