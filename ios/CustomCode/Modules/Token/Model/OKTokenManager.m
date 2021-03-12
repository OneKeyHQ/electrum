//
//  OKTokenManager.m
//  OneKey
//
//  Created by zj on 2021/3/8.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenManager.h"
@interface OKTokenManager()
@property (nonatomic, assign) BOOL needUpdateCurrentAddress;
@property (nonatomic, assign) BOOL needUpdateCcustomTokens;
@end

@implementation OKTokenManager

static dispatch_once_t once;
+ (OKTokenManager *)sharedInstance {
    static OKTokenManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKTokenManager alloc] init];
        [_sharedInstance updateTokenList];
    });
    return _sharedInstance;
}

- (void)addToken:(OKToken *)token {
    [self addToken:token.address symbol:token.symbol];
}

- (void)addToken:(NSString *)address symbol:(NSString *)symbol{
    if (!address.length || !symbol.length) {
        return;
    }
    [kPyCommandsManager callInterface:kInterface_add_token parameter:@{
        @"symbol": symbol,
        @"contract_addr": address
    }];
    self.needUpdateCurrentAddress = YES;
    self.needUpdateCcustomTokens = YES;
}

- (void)delToken:(NSString *)address {
    if (!address.length) {
        return;
    }
    [kPyCommandsManager callInterface:kInterface_delete_token parameter:@{
        @"contract_addr": address
    }];
    self.needUpdateCurrentAddress = YES;
    self.needUpdateCcustomTokens = YES;
}

- (void)updateTokenList {
    NSArray *array = [kPyCommandsManager callInterface:kInterface_get_all_token_info parameter:@{}];
    self.tokens = [OKToken mj_objectArrayWithKeyValuesArray:array];
}

- (NSArray <NSString *>*)currentAddress {
    if (!_currentAddress || self.needUpdateCurrentAddress) {
        NSArray *array = [kPyCommandsManager callInterface:kInterface_get_cur_wallet_token_address parameter:@{}];
        _currentAddress = [array ok_map:^id(id obj) {
            return ((NSString *)obj).lowercaseString;
        }];
        self.needUpdateCurrentAddress = NO;
    }
    return _currentAddress;
}

- (NSArray<OKToken *> *)customTokens {
    if (!_customTokens || self.needUpdateCcustomTokens) {
        NSArray *array = [kPyCommandsManager callInterface:kInterface_get_all_customer_token_info parameter:@{}];
        _customTokens = [OKToken mj_objectArrayWithKeyValuesArray:array];
        self.needUpdateCcustomTokens = NO;
    }
    return _customTokens;
}

- (NSArray<OKToken *> *)selectedTokens {
    if (!_selectedTokens || self.needUpdateCurrentAddress) {
        NSMutableArray *mutArray = [[NSMutableArray alloc] init];
        NSArray <NSString *>*currentAddress = [self currentAddress];

        for (OKToken *model in self.tokens) {
            for (NSString *address in currentAddress) {
                if ([model.address.lowercaseString isEqualToString:address]) {
                    [mutArray addObject:model];
                    break;
                }
            }
        }
        _selectedTokens = mutArray;
    }
    return _selectedTokens;
}

- (NSArray <OKToken *> *)tokensFilterWith:(NSString *)text {
    NSString *searchText = text.lowercaseString;
    NSMutableArray *mutArray = [[NSMutableArray alloc] init];
    for (OKToken *model in self.tokens) {
        if ([model.symbol.lowercaseString containsString:searchText] ||
            [model.name.lowercaseString containsString:searchText] ||
            [model.address.lowercaseString containsString:searchText]
            ) {
            [mutArray addObject:model];
        }
    }
    return mutArray;
}

- (OKToken *)tokensWithAddress:(NSString *)address {
    NSString *addr = address.lowercaseString;
    for (OKToken *model in self.tokens) {
        if ([model.address.lowercaseString isEqualToString:addr]) {
            return model;
        }
    }
    return nil;
}
@end
