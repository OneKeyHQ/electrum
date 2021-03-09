//
//  OKAddTokenSheet.h
//  OneKey
//
//  Created by zj on 2021/3/3.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OKToken.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKAddTokenSheet : UIView
@property (nonatomic, copy) void(^addTokenCallback)(void);
@property (nonatomic, copy) void(^cancel)(void);
@property (nonatomic, strong) OKToken *token;
+ (instancetype)getView;
@end

NS_ASSUME_NONNULL_END
