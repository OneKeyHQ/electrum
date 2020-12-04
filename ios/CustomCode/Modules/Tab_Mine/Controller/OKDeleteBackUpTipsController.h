//
//  OKDeleteBackUpTipsController.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/2.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^DeleteBackUpTipBlock)(void);
@interface OKDeleteBackUpTipsController : UIViewController
+ (instancetype)deleteBackUpTipsController:(DeleteBackUpTipBlock)block;
@end

NS_ASSUME_NONNULL_END
