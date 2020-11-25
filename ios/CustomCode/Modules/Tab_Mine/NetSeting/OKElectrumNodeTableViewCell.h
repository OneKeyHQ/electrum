//
//  OKElectrumNodeTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKElectrumNodeModel;
@interface OKElectrumNodeTableViewCell : UITableViewCell
@property (nonatomic,strong)OKElectrumNodeModel *model;
@end

NS_ASSUME_NONNULL_END
