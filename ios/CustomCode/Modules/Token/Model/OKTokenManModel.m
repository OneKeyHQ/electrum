//
//  OKTokenManModel.m
//  OneKey
//
//  Created by zj on 2021/2/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenManModel.h"
#import "OKTokenManager.h"

@implementation OKTokenManModel
- (NSInteger)indexOfTableViewIndexTitle:(NSString *)title {

    for (int i = 0; i < self.more.count; i++) {
        if ([self.more[i].symbol.uppercaseString hasPrefix:title]) {
            NSLog(@"%d",i);
            return i + self.hot.count + 2;
        }
    }
    return 0;
}

- (nullable NSArray<NSString *> *)sectionIndexTitles {
    NSMutableSet *set = [[NSMutableSet alloc] init];
    for (OKToken *token in self.more) {
        NSString *prefix = [token.symbol substringToIndex:1];
        if (prefix.length) {
            if ([prefix isEqualToString:@"0"] || prefix.integerValue > 0) {
                [set addObject:@"#"];
            } else {
                [set addObject:prefix.uppercaseString];
            }
        }
    }

    NSMutableArray *alphabet = [@[] mutableCopy];
    if ([set containsObject:@"#"]) {
        [alphabet addObject: @"#"];
    }
    for (char c = 'A'; c <= 'Z'; c++) {
        NSString *chara = [NSString stringWithFormat:@"%c", c];
        if ([set containsObject:chara]) {
            [alphabet addObject: chara];
        }
    }
    return alphabet;
}

- (NSArray *)data {
    NSMutableArray *mutData = [[NSMutableArray alloc] init];
    [mutData addObject:@"热门代币"];
    [mutData addObjectsFromArray:self.hot];
    [mutData addObject:@"更多"];
    [mutData addObjectsFromArray:self.more];
    return mutData;
}

- (NSArray<OKToken *> *)hot {
    NSArray *tokens = [OKTokenManager sharedInstance].tokens;
    if (tokens.count < 10) {
        return tokens;
    }
    return [tokens subarrayWithRange:NSMakeRange(0, 10)];
}

- (NSArray<OKToken *> *)more {
    NSArray *tokens = [OKTokenManager sharedInstance].tokens;
    NSArray<OKToken *> *moreArr = tokens.count < 50 ? tokens : [tokens subarrayWithRange:NSMakeRange(0, 50)];
    moreArr = [moreArr arrayByAddingObjectsFromArray:kOKTokenManager.customTokens];
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"symbol" ascending:YES];
    return [moreArr sortedArrayUsingDescriptors:[NSArray arrayWithObjects:sortDescriptor, nil]];
}

@end
