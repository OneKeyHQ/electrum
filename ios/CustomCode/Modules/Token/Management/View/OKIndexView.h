//
//  OKIndexView.h
//  OneKey
//
//  Created by zj on 2021/2/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^OKIndexViewCallback)(NSString *title, NSInteger index);

@interface OKIndexView : UIView
@property (nonatomic, strong, readonly)UILabel *label;
@property (nonatomic, copy)NSArray <NSString *> *titles;
@property (nonatomic, copy)OKIndexViewCallback callback;
@end

NS_ASSUME_NONNULL_END
