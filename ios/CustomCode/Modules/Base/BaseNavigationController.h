//
//  BaseNavigationController.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BaseNavigationController : UINavigationController

@end


@interface UINavigationBar (OKAppreance)
- (nullable UIImageView *)ok_separator;
@end
NS_ASSUME_NONNULL_END
