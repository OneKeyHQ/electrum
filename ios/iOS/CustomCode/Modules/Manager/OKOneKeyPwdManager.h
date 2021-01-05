//
//  OKOneKeyPwdManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>
#define kOneKeyPwdManager (OKOneKeyPwdManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN

@interface OKOneKeyPwdManager : NSObject
+ (OKOneKeyPwdManager *)sharedInstance;
- (void)saveOneKeyPassWord:(NSString *)pwd;
- (NSString *)getOneKeyPassWord;
- (void)deleteOneKeyPwd;
@end

NS_ASSUME_NONNULL_END
