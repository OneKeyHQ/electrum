//
//  OKBiologicalViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/22.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
typedef void(^BiologicalViewBlock)(void);
@interface OKBiologicalViewController : BaseViewController
+ (instancetype)biologicalViewController:(NSString *)vcName pwd:(NSString *)pwd biologicalViewBlock:(BiologicalViewBlock)block;
@end

NS_ASSUME_NONNULL_END
