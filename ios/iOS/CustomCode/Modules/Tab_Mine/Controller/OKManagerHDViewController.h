//
//  OKManagerHDViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseTableViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKManagerHDViewController : BaseTableViewController
@property (nonatomic,copy)NSString *walletName;
+ (instancetype)managerHDViewController;
@end

NS_ASSUME_NONNULL_END
