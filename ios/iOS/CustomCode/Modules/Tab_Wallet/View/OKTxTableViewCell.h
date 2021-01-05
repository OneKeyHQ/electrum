//
//  OKTxTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

@class OKTxTableViewCellModel;

NS_ASSUME_NONNULL_BEGIN
@interface OKTxTableViewCell : UITableViewCell
@property (nonatomic,copy) OKTxTableViewCellModel* model;
@end

NS_ASSUME_NONNULL_END
