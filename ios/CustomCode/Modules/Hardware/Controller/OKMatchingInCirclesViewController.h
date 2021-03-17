//
//  OKMatchingInCirclesViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "BaseViewController.h"


NS_ASSUME_NONNULL_BEGIN

@interface OKMatchingInCirclesViewController : BaseViewController
+ (instancetype)matchingInCirclesViewController;
@property (nonatomic,assign)OKMatchingType type;
@property (nonatomic,assign)OKMatchingFromWhere where;
@property (nonatomic,assign)BOOL isHwRestore;
@property (nonatomic,copy)NSString *words;
// 当 type = OKMatchingTypeTransfer 时使用
@property (nonatomic, copy, nullable)NSString *addressForTransfer;
@property (nonatomic,copy)NSString *tokenCoinType;

@end

NS_ASSUME_NONNULL_END
