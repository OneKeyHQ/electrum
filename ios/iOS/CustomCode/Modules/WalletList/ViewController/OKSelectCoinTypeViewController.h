//
//  OKSelectCoinTypeViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKSelectCoinTypeViewController : BaseViewController

+ (instancetype)selectCoinTypeViewController;
//添加类型
@property (nonatomic,assign)OKAddType addType;
@property (nonatomic,assign)OKWhereToSelectType where;
@end

NS_ASSUME_NONNULL_END
