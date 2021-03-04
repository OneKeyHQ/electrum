//
//  OKButtonView.h
//  OneKey
//
//  Created by zj on 2021/3/3.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKButtonView : UIView
@property (nonatomic, copy) void(^buttonClick)(void);
@end

NS_ASSUME_NONNULL_END
