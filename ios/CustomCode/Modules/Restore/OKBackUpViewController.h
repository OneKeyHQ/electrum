//
//  OKBackUpViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKBackUpViewController : BaseViewController
@property (nonatomic,strong)NSArray *words;
+ (instancetype)backUpViewController;
@end

NS_ASSUME_NONNULL_END
