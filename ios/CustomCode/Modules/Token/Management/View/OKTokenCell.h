//
//  OKTokenCell.h
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OKToken.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKTokenCell : UITableViewCell
@property (nonatomic, assign)BOOL isTop;
@property (nonatomic, assign)BOOL isBottom;
@property (nonatomic, strong)OKTokenModel *model;
@end

NS_ASSUME_NONNULL_END
