//
//  OKPwdViewController.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
typedef enum {
    OKPwdUseTypeInitPassword,
    OKPwdUseTypeUpdatePassword
}OKPwdUseType;

NS_ASSUME_NONNULL_BEGIN

typedef void(^SetPwdBlock)(NSString *pwd);
@interface OKPwdViewController : BaseViewController
@property (nonatomic,copy)NSString *oldPwd;
+ (instancetype)setPwdViewControllerPwdUseType:(OKPwdUseType)useType setPwd:(SetPwdBlock)setPwd;
@end

NS_ASSUME_NONNULL_END
