//
//  OKAssetTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
#import "OKAssetTableViewCellModel.h"
NS_ASSUME_NONNULL_BEGIN
@class OKAssetTableViewCellModel;
@interface OKAssetTableViewCell : UITableViewCell

@property (nonatomic,strong)OKAssetTableViewCellModel *model;

@end

NS_ASSUME_NONNULL_END
