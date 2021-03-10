//
//  OKTokenSelectController.h
//  OneKey
//
//  Created by zj on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OKAllAssetsCellModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKTokenSelectController : BaseViewController
@property (nonatomic, strong)NSArray *data;
@property (nonatomic, copy) void(^selectCallback)(OKAllAssetsCellModel *selected);

+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
