//
//  OKWalletListTableViewCell.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKWalletListTableViewCellModel;
@interface OKWalletListTableViewCell : UITableViewCell
@property (nonatomic,strong)OKWalletListTableViewCellModel *model;
@end

NS_ASSUME_NONNULL_END
