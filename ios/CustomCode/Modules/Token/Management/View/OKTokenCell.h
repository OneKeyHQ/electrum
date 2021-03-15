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
@property (nonatomic, assign)BOOL isOn;
@property (nonatomic, strong)OKToken *model;
@property (nonatomic, strong)void(^tokenSwitched)(BOOL isOn, OKToken *model);
@end

NS_ASSUME_NONNULL_END
