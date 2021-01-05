//
//  OKShareView.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/26.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKShareView : UIView
+(OKShareView *)initViewWithImage:(UIImage *)image coinType:(NSString *)coinType address:(NSString *)address;
@end

NS_ASSUME_NONNULL_END
