//
//  OKWordWrongViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/4.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef void(^BlockComplete)(void);

NS_ASSUME_NONNULL_BEGIN

@interface OKWordWrongViewController : UIViewController
@property (nonatomic,copy)BlockComplete block;

+ (instancetype)wordWrongViewController:(BlockComplete)block;

@end

NS_ASSUME_NONNULL_END
