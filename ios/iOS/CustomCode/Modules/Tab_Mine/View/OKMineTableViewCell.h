//
//  OKMineTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/20.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

@protocol OKMineTableViewCellDelegate <NSObject>

- (void)mineTableViewCellModelDelegateSwitch:(UISwitch *_Nullable)s;

@end



NS_ASSUME_NONNULL_BEGIN
@class OKMineTableViewCellModel;
@interface OKMineTableViewCell : UITableViewCell
@property (nonatomic,strong)OKMineTableViewCellModel *model;
@property (nonatomic,weak)id<OKMineTableViewCellDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
