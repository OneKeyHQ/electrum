//
//  OKComMarco.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#ifndef OKComMarco_h
#define OKComMarco_h

#define SCREEN_WIDTH [UIScreen mainScreen].bounds.size.width
#define SCREEN_HEIGHT [UIScreen mainScreen].bounds.size.height

#define VALIDATE_STRING(str) (IsNilOrNull(str) ? @"" : str)
#define VALIDATE_ARRAY(arr)  (IsNilOrNull(arr) ? [NSArray array] : arr)
#define VALIDATE_NUMBER(number) (IsNilOrNull(number) ? @0 : number)

#define XXWeakSelf(type)  __weak typeof(type) weak##type = type;
#define XXStrongSelf(type)  __strong typeof(type) type = weak##type;

#define APP_TABBAR_HEIGHT                               CGRectGetHeight(self.tabBarController.tabBar.bounds)
#define APP_NAVIGATIONBAR_HEIGHT                        44.f
#define APP_STATUSBAR_HEIGHT                            CGRectGetHeight([UIApplication sharedApplication].statusBarFrame)
#define APP_STATUSBAR_AND_NAVIGATIONBAR_HEIGHT          (APP_STATUSBAR_HEIGHT + APP_NAVIGATIONBAR_HEIGHT)

#define OKKeyWindow     [[UIApplication sharedApplication] keyWindow]
#define OKUserDefaults  [NSUserDefaults standardUserDefaults]


#define kFontLite @"PingFang-SC-Light"
#define kFontPingFang @"PingFangSC-Regular"
#define kFontPingFangBold @"PingFangSC-Semibold"
#define kFontDINAlt @"DIN Alternate"
#define kFontPingFangMediumBold @"PingFangSC-Medium"

#pragma mark 屏幕的高和宽，物理宽度
// 屏幕尺寸
#define kScreenBounds [UIScreen mainScreen].bounds
#define kScreenSize [UIScreen mainScreen].bounds.size
#define kScreenWidth [UIScreen mainScreen].bounds.size.width
#define kScreenHeight [UIScreen mainScreen].bounds.size.height
#define OKWeakSelf(type)  __weak typeof(type) weak##type = type;
#define OKStrongSelf(type)  __strong typeof(type) type = weak##type;

#ifndef weakify
#if DEBUG
#if __has_feature(objc_arc)
#define weakify(object) autoreleasepool{} __weak __typeof__(object) weak##_##object = object;
#else
#define weakify(object) autoreleasepool{} __block __typeof__(object) block##_##object = object;
#endif
#else
#if __has_feature(objc_arc)
#define weakify(object) try{} @finally{} {} __weak __typeof__(object) weak##_##object = object;
#else
#define weakify(object) try{} @finally{} {} __block __typeof__(object) block##_##object = object;
#endif
#endif
#endif

#ifndef strongify
#if DEBUG
#if __has_feature(objc_arc)
#define strongify(object) autoreleasepool{} __typeof__(object) object = weak##_##object;
#else
#define strongify(object) autoreleasepool{} __typeof__(object) object = block##_##object;
#endif
#else
#if __has_feature(objc_arc)
#define strongify(object) try{} @finally{} __typeof__(object) object = weak##_##object;
#else
#define strongify(object) try{} @finally{} __typeof__(object) object = block##_##object;
#endif
#endif
#endif


/** 设备类型 */
#define kDevice_iPhone4 (kScreenHeight <= 480.0)   //包括iPhone4 , iPhone4s
#define kDevice_iPhone5 ((kScreenHeight > 480.0) && (kScreenHeight <= 568.0))   //包括iPhone5,iPhone5s
#define kDevice_iPhone6 ((kScreenHeight > 568.0) && (kScreenHeight <= 667.0))   //iPhone6
#define kDevice_iPhone6Plus ((kScreenHeight > 667.0) && (kScreenHeight <= 736.0))   //iPhone6Plus
#define kDevice_iPad (kScreenHeight == 480.0)  //iPad
#define kRatio (kScreenWidth/375)

//NSLOG宏
#ifdef DEBUG
#define LRString [NSString stringWithFormat:@"%s", __FILE__].lastPathComponent
#define YYLog(...) printf("%s: %s 第%d行: %s\n\n",[[NSString lr_stringDate] UTF8String], [LRString UTF8String] ,__LINE__, [[NSString stringWithFormat:__VA_ARGS__] UTF8String]);

#else
#define YYLog(...)
#endif

#define IS_IPHONE (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
#define IS_IOS_11  ([[[UIDevice currentDevice] systemVersion] floatValue] >= 11.f)
#define IS_IPHONE_X (IS_IOS_11 && IS_IPHONE && (MIN([UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height) >= 375 && MAX([UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height) >= 812))

#define KDevice_SafeArea_Bottom  (IS_IPHONE_X ? 34 : 0)
#define kDevice_Is_iPhoneX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)
#define kDevice_Is_iPhoneXR ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(828, 1792), [[UIScreen mainScreen] currentMode].size) : NO)
#define kDevice_Is_iPhoneXSMAX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1242, 2688), [[UIScreen mainScreen] currentMode].size) : NO)

#define kDevice_Is_iPad ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? ([[[UIScreen mainScreen]currentMode] size].width >= 1536.f ? YES : NO) : NO)

#define kNeedSafeArea (@available(iOS 11.0, *))&&kDevice_Is_iPhoneX

#define WIDTHSCALE(awidth) [UIScreen mainScreen].bounds.size.width / 375 * awidth
#define HEIGHTSCALE(aheight) [[UIScreen mainScreen] bounds].size.height / 667 * aheight

/**
 *  object is not nil and null
 */
#define NotNilAndNull(_ref)  (((_ref) != nil) && (![(_ref) isEqual:[NSNull null]]))

/**
 *  object is nil or null
 */
#define IsNilOrNull(_ref)   (((_ref) == nil) || ([(_ref) isEqual:[NSNull null]]) || ([(_ref) isEqual:[NSNull class]]))

/**
 *  string is nil or null or empty
 */
#define IsStrEmpty(_ref)    (((_ref) == nil) || ([(_ref) isEqual:[NSNull null]]) ||([(_ref)isEqualToString:@""]))

/**
 *  Array is nil or null or empty
 */
#define IsArrEmpty(_ref)    (((_ref) == nil) || ([(_ref) isEqual:[NSNull null]]) ||([(_ref) count] == 0))

#define ALPHANUM @"abcdefghijklmnopqrstuvwxyz12345"

#define JUSTNUM @"0123456789"


#ifndef dispatch_queue_async_safe
#define dispatch_queue_async_safe(queue, block)\
if (strcmp(dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL), dispatch_queue_get_label(queue)) == 0) {\
block\
} else {\
dispatch_async(queue, ^{block});\
}
#endif

#ifndef dispatch_main_async_safe
#define dispatch_main_async_safe(block) dispatch_queue_async_safe(dispatch_get_main_queue(), block)
#endif

#pragma mark -- 组请求
#define asyncGroupCreate    dispatch_group_t group = dispatch_group_create();\
                            dispatch_queue_t queue = dispatch_get_global_queue(0, 0);
#define asyncTask(task)     dispatch_group_enter(group);\
                            dispatch_group_async(group, queue, ^{task});
#define asyncGroupLeave     dispatch_group_leave(group);
#define asyncAwaitAll(task)  dispatch_group_notify(group, dispatch_get_main_queue(), ^{task});

#endif /* OKComMarco_h */
