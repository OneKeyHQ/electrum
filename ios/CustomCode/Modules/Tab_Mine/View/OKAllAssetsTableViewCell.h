//
//  OKAllAssetsTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
@class OKAllAssetsCellModel;
NS_ASSUME_NONNULL_BEGIN

@interface OKAllAssetsTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIView *separator;
@property (nonatomic,strong)OKAllAssetsCellModel *model;
@end

NS_ASSUME_NONNULL_END
