//
//  OKTokenManager.h
//  OneKey
//
//  Created by zj on 2021/3/8.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKToken.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKTokenManager : NSObject
@property (nonatomic, strong) NSArray <OKToken *>*tokens;
@property (nonatomic, strong) NSArray <OKToken *>*selectedTokens;
@property (nonatomic, strong) NSArray <OKToken *>*customTokens;
@property (nonatomic, strong) NSArray <NSString *>*currentAddress;

+ (OKTokenManager *)sharedInstance;

- (void)updateTokenList; // 更新大列表
- (void)addToken:(OKToken *)token;
- (void)addToken:(NSString *)address symbol:(NSString *)symbol;
- (void)delToken:(NSString *)address;

- (NSArray <OKToken *> *)tokensFilterWith:(NSString *)text;
- (nullable OKToken *)tokensWithAddress:(NSString *)address;
@end

#define kOKTokenManager ([OKTokenManager sharedInstance])

NS_ASSUME_NONNULL_END
