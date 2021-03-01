//
//  OKTokenManModel.m
//  OneKey
//
//  Created by zj on 2021/2/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenManModel.h"

@implementation OKTokenManModel
- (NSInteger)indexOfTableViewIndexTitle:(NSString *)title {

    for (int i = 0; i < self.more.count; i++) {
        if ([self.more[i].name hasPrefix:title]) {
            NSLog(@"%d",i);
            return i + self.hot.count + 2;
        }
    }
    return 0;
}

- (nullable NSArray<NSString *> *)sectionIndexTitles {
    NSMutableSet *set = [[NSMutableSet alloc] init];
    for (OKTokenModel *token in self.more) {
        NSString *prefix = [token.name substringToIndex:1];
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

-(NSArray *)data {
    if (!_data) {
        NSMutableArray *mutData = [[NSMutableArray alloc] init];
        [mutData addObject:@"热门代币"];
        [mutData addObjectsFromArray:self.hot];
        [mutData addObject:@"更多"];
        [mutData addObjectsFromArray:self.more];
        _data = mutData;
    }
    return _data;
}

- (NSArray<OKTokenModel *> *)hot {
    static int c = 0;
    NSMutableArray *mutData = [[NSMutableArray alloc] init];
    for (int i = 0; i < 10; i++) {
        OKTokenModel *token = [[OKTokenModel alloc] init];
        token.name = [NSString stringWithFormat:@"ETH%i", c++];
        token.address = @"fwefnqerkjnviueanviqcu";
        token.blance = @(222222);
        token.isOn = NO;
        [mutData addObject:token];
    }

    return mutData;
}

- (NSArray<OKTokenModel *> *)more {
    if (!_more) {
        static int c = 0;
        NSMutableArray *mutData = [@[] mutableCopy];

        NSMutableArray *alphabet = [@[@"1",@"5"] mutableCopy];
        for (char c = 'A'; c <= 'Z'; c++) {
            [alphabet addObject: [NSString stringWithFormat:@"%c", c]];
        }
        [alphabet removeObject:@"C"];
        [alphabet removeObject:@"L"];
        [alphabet removeObject:@"G"];
        [alphabet removeObject:@"H"];


        for (int i = 0; i < 50; i++) {
            OKTokenModel *token = [[OKTokenModel alloc] init];
            token.name = [NSString stringWithFormat:@"%@ BTC%i",alphabet[MIN(i/2, 20)], c++];
            token.address = @"fwefnqerkjnviueanviqcu";
            token.blance = @(222222);
            token.isOn = NO;
            [mutData addObject:token];
        }
        _more = mutData;
    }


    return _more;
}

@end
