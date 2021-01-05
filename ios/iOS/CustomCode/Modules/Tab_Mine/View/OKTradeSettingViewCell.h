//
//  OKTradeSettingViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
#import "OKTradeSettingViewCellModel.h"
NS_ASSUME_NONNULL_BEGIN

@protocol OKTradeSettingViewCellDelegate <NSObject>

- (void)switchClick:(OKTradeSettingViewCellModel *)model on:(BOOL)on;

@end


@interface OKTradeSettingViewCell : UITableViewCell
@property (nonatomic,strong)OKTradeSettingViewCellModel *model;
@property (nonatomic,weak)id<OKTradeSettingViewCellDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
