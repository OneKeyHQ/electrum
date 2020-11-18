//
//  OKExportTipsViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

typedef void (^ConfirmBtnClick)(void);
NS_ASSUME_NONNULL_BEGIN

@interface OKExportTipsViewController : BaseViewController

+ (instancetype)exportTipsViewController:(ConfirmBtnClick)btnClick;

@end

NS_ASSUME_NONNULL_END
