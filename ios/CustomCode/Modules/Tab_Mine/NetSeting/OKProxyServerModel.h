//
//  OKProxyServerModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/25.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKProxyServerModel : NSObject
@property (nonatomic,assign) BOOL proxyOn;
@property (nonatomic,copy) NSString* type;
@property (nonatomic,copy) NSString* ipAddress;
@property (nonatomic,copy) NSString* port;
@property (nonatomic,copy) NSString* userName;
@property (nonatomic,copy) NSString* pwd;
@end

NS_ASSUME_NONNULL_END
