//
//  OKHwNotiManager.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/15.
//  Copyright © 2021 Onekey. All rights reserved.
//
#import <Foundation/Foundation.h>
typedef NS_ENUM(NSInteger,OKHWNotiType) {
    OKHWNotiTypePin_Current = 1, //验证当前PIN
    OKHWNotiTypePin_New_First = 2, //设置新PIN
    OKHWNotiTypePass_Phrass = 3, //验证passphrass
    OKHWNotiTypeFactoryReset = 106, //恢复出厂设置
    OKHWNotiTypeKeyConfirm = 107, //按键确认
    OKHWNotiTypeSendCoinConfirm = 108,//发币确认
    OKHWNotiTypeVerify_Address_Confirm = 110 //校验地址确认
};

#define kHwNotiManager (OKHwNotiManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN
@class OKHwNotiManager;

@protocol  OKHwNotiManagerDelegate <NSObject>
@optional
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type;

@end

@interface OKHwNotiManager : NSObject
+ (OKHwNotiManager *)sharedInstance;
@property (nonatomic,weak)id<OKHwNotiManagerDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
