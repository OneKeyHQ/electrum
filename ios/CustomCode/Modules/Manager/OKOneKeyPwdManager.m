//
//  OKOneKeyPwdManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKOneKeyPwdManager.h"
#import "KeyChainSaveUUID.h"

#define kSaveOneKeyPassword         @"kSaveOneKeyPassword"


@implementation OKOneKeyPwdManager

static dispatch_once_t once;

+ (OKOneKeyPwdManager *)sharedInstance {
    static OKOneKeyPwdManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKOneKeyPwdManager alloc] init];
    });
    return _sharedInstance;
}

- (void)saveOneKeyPassWord:(NSString *)pwd
{
    NSString *pwdSecret =  [AESCrypt encrypt:pwd password:@""];
    [KeyChainSaveUUID save:kSaveOneKeyPassword data:pwdSecret];
}

- (void)deleteOneKeyPwd
{
    [KeyChainSaveUUID delete_:kSaveOneKeyPassword];
}

- (NSString *)getOneKeyPassWord
{
    NSString *pwdSecret = [KeyChainSaveUUID load:kSaveOneKeyPassword];
    NSString *pwd =  [AESCrypt decrypt:pwdSecret password:@""];
    return pwd;
}
@end
