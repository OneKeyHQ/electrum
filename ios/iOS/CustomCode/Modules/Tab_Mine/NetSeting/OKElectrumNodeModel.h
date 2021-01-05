//
//  OKElectrumNodeModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKElectrumNodeModel : NSObject

@property (nonatomic,copy) NSString* ip;
@property (nonatomic,copy) NSString* s;
@property (nonatomic,copy) NSString* t;
@property (nonatomic,copy) NSString* pruning;
@property (nonatomic,copy) NSString* version;

@end

NS_ASSUME_NONNULL_END
