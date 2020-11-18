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


@interface OKPwdViewController : BaseViewController
@property (nonatomic,assign)OKPwdUseType pwdUseType;
@property (nonatomic,copy)NSString *words;
+ (instancetype)pwdViewController;
@end

NS_ASSUME_NONNULL_END
