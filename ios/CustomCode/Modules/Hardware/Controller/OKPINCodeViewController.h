//
//  OKPINCodeViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
typedef void(^PINCodeComplete)(NSString *pin);
@interface OKPINCodeViewController : BaseViewController
+ (instancetype)PINCodeViewController:(PINCodeComplete)complete;
@end

NS_ASSUME_NONNULL_END
