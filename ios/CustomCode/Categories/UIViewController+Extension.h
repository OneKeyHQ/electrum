//
//  UIViewController+Extension.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/6.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIViewController (Extension)
/**  在主线程执行操作*/
- (void)performSelectorOnMainThread:(void(^)(void))block;

/**  退出 presentViewController  count：次数*/
- (void)dismissViewControllerWithCount:(NSInteger)count animated:(BOOL)animated;


/**  退出 presentViewController 到指定的控制器*/
- (void)dismissToViewControllerWithClassName:(NSString *)className animated:(BOOL)animated;

@end

NS_ASSUME_NONNULL_END
