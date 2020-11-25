//
//  OKNetTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/23.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKNetTableViewCellModel;
@interface OKNetTableViewCell : UITableViewCell
@property (nonatomic,strong)OKNetTableViewCellModel *model;
@end
NS_ASSUME_NONNULL_END
