//
//  OKReadyToStartViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKReadyToStartViewController : BaseViewController
+ (instancetype)readyToStartViewController;
@property (nonatomic,copy)NSString *pwd;
@property (nonatomic,assign)OKWalletType type;
@property (nonatomic,copy)NSString *words;
@end

NS_ASSUME_NONNULL_END
