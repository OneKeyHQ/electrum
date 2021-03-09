//
//  OKToken.h
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKToken : NSObject
@property (nonatomic, assign) NSInteger chainId;
@property (nonatomic, assign) NSInteger decimals;
@property (nonatomic, copy) NSString *address;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *symbol;
@property (nonatomic, copy) NSString *logoURI;
@end


NS_ASSUME_NONNULL_END
