//
//  OKSocketTypeSelectController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/25.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef void (^ConfirmBtnClick)(NSString * _Nullable type);
NS_ASSUME_NONNULL_BEGIN

@interface OKSocketTypeSelectController : UIViewController
+ (instancetype)socketTypeSelectController:(ConfirmBtnClick)block;
@end

NS_ASSUME_NONNULL_END
