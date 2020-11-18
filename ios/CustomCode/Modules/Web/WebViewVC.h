//
//  WebViewVC.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

typedef NS_ENUM(NSInteger, WebViewTag) {
    WebViewTagMessageList = 1,
    WebViewTagNativeDiscover,
};

typedef void(^RightItemClickBlock)(void);

@interface WebViewVC : BaseViewController

@property (nonatomic, copy) RightItemClickBlock rightItemClickBlock;
@property (nonatomic, assign) BOOL statusBarLightContent;
@property (nonatomic, assign) BOOL navigationBarBackButtonWhite;
@property (nonatomic) WebViewTag webTag;
@property (assign, nonatomic) BOOL isShouldReload; // 跳转原生回来后刷新， 默认为YES，设置为NO后下一次重置为YES
@property (nonatomic, strong) DWKWebView *wkWebView;
@property (nonatomic, copy) void (^loadedCallback)(BOOL isSuccess);

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemTitle:(NSString *)rightItemTitle rightItemBlock:(RightItemClickBlock)rightItemBlock;
+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemImage:(NSString *)rightItemImage rightItemBlock:(RightItemClickBlock)rightItemBlock;
+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemTitle:(NSString *)rightItemTitle;
+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url;
+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title html:(NSString *)html;

- (void)reload;

- (void)backToPrevious;

- (void)clearHistory;

/**
 加载纯外部链接网页
 
 @param string URL地址
 */
- (void)loadWebURLSring:(NSString *)string;

/**
 加载本地网页
 
 @param string 本地HTML文件名
 */
- (void)loadWebHTMLSring:(NSString *)string;

@end
