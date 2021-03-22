//
//  OKWallertTranferConnect.h
//  OneKey
//
//  Created by xuxiwen on 2021/3/22.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWallertTranferConnect : NSObject

typedef void (^ConnectSuccessBlock)(void);
typedef void (^ConnectFailedBlock)(NSString *msg);

@property (nonatomic, copy) ConnectSuccessBlock connectSuccessCallback;
@property (nonatomic, copy) ConnectFailedBlock connectFailedCallback;

- (void)startConnect;
- (void)stopConnect;

@end

NS_ASSUME_NONNULL_END
