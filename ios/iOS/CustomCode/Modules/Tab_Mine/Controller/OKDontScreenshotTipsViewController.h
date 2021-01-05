//
//  OKDontScreenshotTipsViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

typedef void(^DontScreenBtnClick)(void);
NS_ASSUME_NONNULL_BEGIN

@interface OKDontScreenshotTipsViewController : BaseViewController
+ (instancetype)dontScreenshotTipsViewController:(DontScreenBtnClick)click;
@end

NS_ASSUME_NONNULL_END
