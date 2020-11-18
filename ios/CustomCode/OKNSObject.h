//
//  OKNSObject.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/29.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>
#import "BaseNavigationController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKNSObject : NSObject
@property (nonatomic, strong)UIViewController *walletVC;
- (void)onCallback:(NSString *)str1;
@end

NS_ASSUME_NONNULL_END
