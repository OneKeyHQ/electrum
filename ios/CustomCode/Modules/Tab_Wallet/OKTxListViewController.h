//
//  OKTxListViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/14.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
@class  OKAssetTableViewCellModel;
@interface OKTxListViewController : BaseViewController
@property (nonatomic,strong)OKAssetTableViewCellModel *model;
@end

NS_ASSUME_NONNULL_END
