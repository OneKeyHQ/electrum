//
//  OKTxViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKTxViewController : UIViewController
@property (nonatomic,copy)NSString *searchType;
+ (instancetype)txViewController;
@end

NS_ASSUME_NONNULL_END
