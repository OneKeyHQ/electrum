//
//  OKTokenManModel.h
//  OneKey
//
//  Created by zj on 2021/2/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "OKToken.h"
NS_ASSUME_NONNULL_BEGIN

@interface OKTokenManModel : NSObject
@property (nonatomic, strong)NSArray *data;
@property (nonatomic, strong)NSArray<OKToken *> *hot;
@property (nonatomic, strong)NSArray<OKToken *> *more;

- (NSInteger)indexOfTableViewIndexTitle:(NSString *)title;
- (nullable NSArray<NSString *> *)sectionIndexTitles;
@end

NS_ASSUME_NONNULL_END
