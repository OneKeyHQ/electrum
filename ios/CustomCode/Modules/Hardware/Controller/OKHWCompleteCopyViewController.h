//
//  OKHWCompleteCopyViewController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKHWCompleteCopyViewController : BaseViewController
@property (nonatomic,assign)OKMnemonicLengthType type;
@property (nonatomic,copy)NSString *deviceName;
+ (instancetype)hwCompleteCopyViewController;
@end

NS_ASSUME_NONNULL_END
