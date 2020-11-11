//
//  WebViewVC.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "WebViewVC.h"
#import "UIBarButtonItem+CustomBarButtonItem.h"
#import <WebKit/WKWebView.h>
#import <WebKit/WebKit.h>

typedef NS_ENUM(NSInteger, WebViewLoadType) {
    WebViewLoadTypeURL = 0,
    WebViewLoadTypeHTML,
};

@interface WebViewVC () <WKNavigationDelegate, WKUIDelegate, UINavigationControllerDelegate>
{
    BOOL _autoTitle;
    NSString *_currentUrl;
}
//设置加载进度条
@property (nonatomic,strong) UIProgressView *progressView;
//网页加载的类型
@property(nonatomic,assign) WebViewLoadType loadType;
//保存的网址链接
@property (nonatomic, copy) NSString *URLString;
//保存rightBarButtonItemName
@property (nonatomic, copy) NSString *rightBarButtonItemName;
//保存rightBarButtonItemImage
@property (nonatomic, copy) NSString *rightBarButtonItemImage;

@property (nonatomic, strong) UIButton *closeBBI;
@property (nonatomic, strong) UIBarButtonItem *leftView;

@end

@implementation WebViewVC

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemTitle:(NSString *)rightItemTitle rightItemBlock:(RightItemClickBlock)rightItemBlock {
    return [self loadWebViewControllerWithTitle:title url:url rightItemTitle:rightItemTitle rightItemImage:nil rightItemBlock:rightItemBlock];
}

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemImage:(NSString *)rightItemImage rightItemBlock:(RightItemClickBlock)rightItemBlock {
    return [self loadWebViewControllerWithTitle:title url:url rightItemTitle:nil rightItemImage:rightItemImage rightItemBlock:rightItemBlock];
}

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemTitle:(NSString *)rightItemTitle rightItemImage:(NSString *)rightItemImage rightItemBlock:(RightItemClickBlock)rightItemBlock {
    WebViewVC *vc = [[WebViewVC alloc] init];
    [vc loadWebURLSring:url];
    if (title) {
        vc.navigationItem.title = title;
    }else{
        vc->_autoTitle = YES;
    }
    vc.rightBarButtonItemName = rightItemTitle;
    vc.rightBarButtonItemImage = rightItemImage;
    vc.rightItemClickBlock = rightItemBlock;
    vc.hidesBottomBarWhenPushed = YES;
    return vc;
}

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url rightItemTitle:(NSString *)rightItemTitle {
    return [self loadWebViewControllerWithTitle:title url:url rightItemTitle:rightItemTitle rightItemBlock:nil];
}

#pragma mark 添加rightItem
- (void)addRightItem {
    if ((self.rightBarButtonItemName && self.rightBarButtonItemName.length != 0) ||
        self.rightBarButtonItemImage
        ) {
        if (self.rightBarButtonItemImage) {
           self.navigationItem.rightBarButtonItem =  [UIBarButtonItem barButtonItemWithImage:[UIImage imageNamed:self.rightBarButtonItemImage] frame:CGRectMake(0, 0, 30, 44) target:self selector:@selector(rightItemClick)];
        }else{
            self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:self.rightBarButtonItemName style:UIBarButtonItemStylePlain target:self action:@selector(rightItemClick)];
            [self.navigationItem.rightBarButtonItem setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:15], NSForegroundColorAttributeName:HexColor(APP_MAIN_TITLE_COLOR)} forState:UIControlStateNormal];
            [self.navigationItem.rightBarButtonItem setTintColor:HexColor(APP_MAIN_TITLE_COLOR)];
        }
    }
}

- (void)changeNavagationbar{

    [self addRightItem];
}

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title url:(NSString *)url {
   WebViewVC *vc = [self loadWebViewControllerWithTitle:title url:url rightItemTitle:nil rightItemBlock:nil];
    
    return vc;
}

+ (WebViewVC *)loadWebViewControllerWithTitle:(NSString *)title html:(NSString *)html {
    WebViewVC *vc = [[WebViewVC alloc] init];
    [vc loadWebHTMLSring:html];
    vc.navigationItem.title = title;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setHidesBottomBarWhenPushed:YES];

    // 替换返回按钮
    if ([self.navigationController.viewControllers count] > 1) {
        self.navigationItem.leftBarButtonItem = self.leftView;
    };
    //添加到主控制器上
    [self.view addSubview:self.wkWebView];
    self.view.backgroundColor = [UIColor whiteColor];
    //加载web页面
    [self webViewloadURLType];
    
    // 设置代理
    _wkWebView.navigationDelegate = self;
    _wkWebView.DSUIDelegate = self;
    //self.navigationController.delegate = self;
    
    [self setNavigationBarBackgroundColorWithClearColor];
    
    if (_webTag != WebViewTagNativeDiscover) {
        //添加进度条
        [self.view addSubview:self.progressView];
    }
    
    if (@available(iOS 11.0, *)) { // 解决UIScrollView自动预留空白问题
        self.wkWebView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    } else {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self changeNavagationbar];

    switch (_webTag) {
        case WebViewTagNativeDiscover:
            break;
        default:
        {
            if (_isShouldReload) {
                [self.wkWebView reload];
            }
        }
            break;
    }
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    _isShouldReload = YES;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    if (self.statusBarLightContent) {
        return UIStatusBarStyleLightContent;
    } else {
        return UIStatusBarStyleDefault;
    }
}

//注意，观察的移除
- (void)dealloc{
    [self.wkWebView removeObserver:self forKeyPath:NSStringFromSelector(@selector(estimatedProgress))];
    [self.wkWebView removeObserver:self forKeyPath:@"title"];
}

- (void)backToPrevious {
    if (self.wkWebView.canGoBack) {
        [self.wkWebView goBack];
        [self.wkWebView reload];
    } else {
        [self closeAllUrl];
    }
}

- (void)reload {
    [self.wkWebView reload];
}

- (void)closeAllUrl {
    if (self.navigationController.viewControllers.count <= 2) {
        [[self.navigationController.viewControllers firstObject] setHidesBottomBarWhenPushed:NO];
    }
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)updateNavigationItems{
    if (self.wkWebView.canGoBack) {
        self.closeBBI.hidden = NO;
        if (self.webTag == WebViewTagMessageList) {
            self.navigationItem.rightBarButtonItem = nil;
        }
    } else {
        self.navigationController.interactivePopGestureRecognizer.enabled = YES;
        self.closeBBI.hidden = YES;
        [self addRightItem];
    }
}

- (BOOL)isCoinAssetsDetail {
    return [_currentUrl?:self.wkWebView.URL.absoluteString containsString:@"detail/"];
}

#pragma mark ================ WKNavigationDelegate ================
// 网页加载完成
-(void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation{
    // 获取加载网页的标题
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    [self updateNavigationItems];
    if (_loadedCallback) {
        _loadedCallback(YES);
    }
    //禁用webview长按后文字选择框和放大框
    [webView evaluateJavaScript:@"document.documentElement.style.webkitUserSelect='none'" completionHandler:nil];
    [webView evaluateJavaScript:@"document.documentElement.style.webkitTouchCallout='none'" completionHandler:nil];
}

//开始加载
-(void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation{
    //开始加载的时候，让加载进度条显示
    self.progressView.hidden = NO;
}

- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error{
    if (_loadedCallback) {
        _loadedCallback(NO);
    }

    if (error.code == NSURLErrorCancelled) {
        return;
    }

    NSString * pathString = [[NSBundle mainBundle] pathForResource:@"noNetwork" ofType:@"html" inDirectory:nil];
    NSString * urlString2 = [[NSString stringWithFormat:@"?locale=%@&code=%ld&url=%@", [OKLocalizableManager getCurrentLanguageString], ABS(error.code), _URLString]stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]];
    [self.wkWebView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:urlString2 relativeToURL:[NSURL fileURLWithPath:pathString]]]];
}

//内容返回时调用
-(void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation {
}

// 页面加载失败时调用
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation {
}

//服务器请求跳转的时候调用
-(void)webView:(WKWebView *)webView didReceiveServerRedirectForProvisionalNavigation:(WKNavigation *)navigation {
}

// 在收到响应后，决定是否跳转
- (void)webView:(WKWebView *)webView decidePolicyForNavigationResponse:(WKNavigationResponse *)navigationResponse decisionHandler:(void (^)(WKNavigationResponsePolicy))decisionHandler {
    decisionHandler(WKNavigationResponsePolicyAllow);
}

// 在发送请求之前，决定是否跳转
- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    [self updateNavigationItems];
    _currentUrl = navigationAction.request.URL.absoluteString;
    // 如果是跳转一个新页面
    if (navigationAction.targetFrame == nil) {
        [webView loadRequest:navigationAction.request];
    }
    decisionHandler(WKNavigationActionPolicyAllow);
}

//KVO监听进度条
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    
    if ([keyPath isEqualToString:NSStringFromSelector(@selector(estimatedProgress))] && object == self.wkWebView) {
        [self.progressView setAlpha:1.0f];
        BOOL animated = self.wkWebView.estimatedProgress > self.progressView.progress;
        [self.progressView setProgress:self.wkWebView.estimatedProgress animated:animated];
        
        // Once complete, fade out UIProgressView
        if(self.wkWebView.estimatedProgress >= 1.0f) {
            [UIView animateWithDuration:0.3f delay:0.3f options:UIViewAnimationOptionCurveEaseOut animations:^{
                [self.progressView setAlpha:0.0f];
            } completion:^(BOOL finished) {
                [self.progressView setProgress:0.0f animated:NO];
            }];
        }
    }else if ([keyPath isEqualToString:@"title"])
    {
        if (object == self.wkWebView && _autoTitle) {
            self.navigationItem.title = self.wkWebView.title;
        }else {
            //            [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
        }
    }else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}

#pragma mark ================ 加载方式 ================
- (void)webViewloadURLType{
    switch (self.loadType) {
        case WebViewLoadTypeURL:{
            //加载网页
            NSMutableURLRequest *requestM =  [[NSMutableURLRequest alloc]initWithURL:[NSURL URLWithString:self.URLString] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:10];
            [self.wkWebView loadRequest:requestM];
            [self loadProcess];
            break;
        }
        case WebViewLoadTypeHTML:{
            [self loadHostPathURL:self.URLString];
            break;
        }
    }
}

- (void)loadProcess {
    NSURL *url = [NSURL URLWithString:self.URLString];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:5];
    [[[NSURLSession sharedSession] dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        switch (httpResponse.statusCode) {
            default:
                break;
        }
    }] resume];
}

- (void)loadHostPathURL:(NSString *)url{
    //获取JS所在的路径
    NSString *path = [[NSBundle mainBundle] pathForResource:url ofType:@"html"];
    //获得html内容
    NSString *html = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
    //加载js
    [self.wkWebView loadHTMLString:html?:url baseURL:[[NSBundle mainBundle] bundleURL]];
}

- (void)loadWebURLSring:(NSString *)string{
    self.URLString = string;
    self.loadType = WebViewLoadTypeURL;
}

- (void)loadWebHTMLSring:(NSString *)string{
    self.URLString = string;
    self.loadType = WebViewLoadTypeHTML;
}

#pragma mark ================ 懒加载 ================
- (WKWebView *)wkWebView{
    if (_wkWebView == nil) {
        WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
        WKUserContentController *userContentController = [[WKUserContentController alloc] init];

        WKUserScript *dsbridgeJs = [[WKUserScript alloc]initWithSource:[NSString stringWithContentsOfURL:[NSURL fileURLWithPath:[[NSBundle mainBundle]pathForResource:@"dsbridge" ofType:@"js"]] encoding:NSUTF8StringEncoding error:nil] injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:NO];

        [userContentController addUserScript:dsbridgeJs];
        
        config.userContentController = userContentController;

        CGFloat height = self.navigationbarTranslucent ? SCREEN_HEIGHT : (SCREEN_HEIGHT - APP_STATUSBAR_AND_NAVIGATIONBAR_HEIGHT - KDevice_SafeArea_Bottom);
        _wkWebView = [[DWKWebView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, height) configuration:config];
        _wkWebView.backgroundColor = [UIColor colorWithRed:240.0/255 green:240.0/255 blue:240.0/255 alpha:1.0];
        //kvo 添加进度监控
        [_wkWebView addObserver:self forKeyPath:NSStringFromSelector(@selector(estimatedProgress)) options:0 context:nil];
        [_wkWebView addObserver:self forKeyPath:@"title" options:NSKeyValueObservingOptionNew context:nil];
        //开启手势触摸
        _wkWebView.allowsBackForwardNavigationGestures = YES;
        // 设置 可以前进 和 后退
        //适应你设定的尺寸
        [_wkWebView sizeToFit];
    }
    return _wkWebView;
}

- (UIProgressView *)progressView{
    if (!_progressView) {
        _progressView = [[UIProgressView alloc]initWithProgressViewStyle:UIProgressViewStyleDefault];
        _progressView.frame = CGRectMake(0, 0, self.view.bounds.size.width, 3);
        // 设置进度条的色彩
        [_progressView setTrackTintColor:[UIColor colorWithRed:240.0/255 green:240.0/255 blue:240.0/255 alpha:1.0]];
        _progressView.progressTintColor = HexColor(RGB_THEME_GREEN);
    }
    return _progressView;
}

- (UIButton *)closeBBI {
    if (_closeBBI == nil) {
        _closeBBI = [[UIButton alloc] initWithFrame: CGRectMake(30, 0, 30, 30)];
        [_closeBBI addTarget:self action:@selector(closeAllUrl) forControlEvents:UIControlEventTouchUpInside];
    }
    NSString *bckImg = @"关闭";
    [_closeBBI setImage:[UIImage imageNamed:bckImg] forState:UIControlStateNormal];
    return _closeBBI;
}

- (UIBarButtonItem *)leftView {
    if (_leftView == nil) {
        UIView *bgView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 60, 30)];
        NSString *bckImg = @"left_arrow";
        UIButton *backButton = [[UIButton alloc] initWithFrame: CGRectMake(0, 0, 30, 30)];
        UIImage *image = [UIImage imageNamed:bckImg];
        [backButton setImage:image forState:UIControlStateNormal];
        [backButton setImage:image forState:UIControlStateHighlighted];
        [backButton addTarget:self action:@selector(backToPrevious) forControlEvents:UIControlEventTouchUpInside];
        [bgView addSubview:backButton];
        [bgView addSubview:self.closeBBI];
        _leftView = [[UIBarButtonItem alloc]initWithCustomView:bgView];;
    }
    return _leftView;
}

#pragma mark 右边item事件
- (void)rightItemClick
{
    if (self.rightItemClickBlock)
    {
        self.rightItemClickBlock();
    }
}

- (void)clearHistory {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    [_wkWebView.backForwardList performSelector:NSSelectorFromString(@"_removeAllItems")];
#pragma clang diagnostic pop
}
#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowFirstVc = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowFirstVc animated:YES];
}
@end
