//
//  OKSendCoinViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKSendCoinViewController : BaseViewController
@property (nonatomic,copy)NSString *address;
+ (instancetype)sendCoinViewController;
@end

NS_ASSUME_NONNULL_END
