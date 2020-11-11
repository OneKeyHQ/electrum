//
//  OKValidationPwdController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/6.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void(^ValidationComplete)(NSString *pwd);

@interface OKValidationPwdController : BaseViewController
@property (nonatomic,copy)ValidationComplete block;
+ (instancetype)validationPwdController;
+ (void)showValidationPwdPageOn:(UIViewController *)vc isDis:(BOOL)isDis complete:(ValidationComplete)complete;
@end

NS_ASSUME_NONNULL_END
