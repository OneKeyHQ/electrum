//
//  OKWordCheckViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKWordCheckViewController : BaseViewController
@property (nonatomic,strong)NSArray *words;
+ (instancetype)wordCheckViewController;
@end

NS_ASSUME_NONNULL_END
