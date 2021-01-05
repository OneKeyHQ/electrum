//
//  OKWordCheckViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKWordCheckViewController : BaseViewController
@property (nonatomic,strong)NSArray *words;
@property (nonatomic,copy)NSString *walletName;
+ (instancetype)wordCheckViewController;
@end

NS_ASSUME_NONNULL_END
