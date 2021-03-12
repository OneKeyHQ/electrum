//
//  OKQRScanResultController.h
//  OneKey
//
//  Created by zj on 2021/3/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKCopyLabel : UILabel
@end

@interface OKQRScanResultController : BaseViewController
@property (nonatomic, copy)NSString *resultText;

+ (instancetype)controllerWithStoryboard;
@end

NS_ASSUME_NONNULL_END
