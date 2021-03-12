//
//  OKChangeWalletCell.h
//  OneKey
//
//  Created by zj on 2021/3/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKChangeWalletCell : UITableViewCell
@property (nonatomic, weak)OKWalletInfoModel *model;
@end

@interface OKChangeWalletSubCell : UITableViewCell
@property (nonatomic, assign)OKWalletCoinType type;
@end

NS_ASSUME_NONNULL_END
