//
//  OKURLSchemeHandler.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKURLSchemeHandler.h"
#include <CFNetwork/CFProxySupport.h>

@interface OKURLSchemeHandler ()
@property (nonatomic, strong) NSURLSession *session;
@end

@implementation OKURLSchemeHandler


- (void)webView:(nonnull WKWebView *)webView startURLSchemeTask:(nonnull id<WKURLSchemeTask>)urlSchemeTask {
    
    NSString *proxyHost = @"cdn.onekey.so";
    NSNumber *proxyPort = @(443);
    
    NSDictionary *proxyDict = @{
        (NSString *)kCFNetworkProxiesHTTPEnable: @(1),
        (NSString *)kCFNetworkProxiesHTTPProxy: proxyHost,
        (NSString *)kCFNetworkProxiesHTTPPort: proxyPort,

        @"HTTPSEnable" : @(1),
        @"HTTPSProxy": proxyHost,
        @"HTTPSPort": proxyPort,
    };
    
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];

    NSURL *url = urlSchemeTask.request.URL;
    if ([self shouldUseProxyWithUrl:url]) {
        configuration.connectionProxyDictionary = proxyDict;
        NSString *username = @"onekey";
        NSString *password = @"libbitcoinconsensus";
        NSString *authHeader = [self proxyAuthHeaderWithUsername:username andPassword:password];
        [configuration setHTTPAdditionalHeaders:@{
            @"Proxy-Authorization": authHeader
        }];
        
    } else {
        configuration.connectionProxyDictionary = @{};
    }
    

    NSURLSession *defaultSession = [NSURLSession sessionWithConfiguration:configuration];

    __weak id<WKURLSchemeTask> weakUrlSchemeTask = urlSchemeTask;
    NSURLSessionDataTask *dataTask = [defaultSession dataTaskWithRequest:urlSchemeTask.request
                                                       completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (!weakUrlSchemeTask) {
            return;
        }
        
        if (error) {
            [weakUrlSchemeTask didFailWithError:error];
            return;
        }
        
        if (response) {
            [weakUrlSchemeTask didReceiveResponse:response];
        }
        
        if (data) {
            [weakUrlSchemeTask didReceiveData:data];
        }
        
        [weakUrlSchemeTask didFinish];
    }];
    [dataTask resume];
}

- (void)webView:(nonnull WKWebView *)webView stopURLSchemeTask:(nonnull id<WKURLSchemeTask>)urlSchemeTask {
    return;
}

- (BOOL)shouldUseProxyWithUrl:(NSURL *)url {
    NSString *host = url.host;
    if ([host containsString:@"onekey"] ||
        [host containsString:@"zendesk"] ||
        [host containsString:@"zdassets"]
        ) {
        return YES;
    }
    return YES;
}

- (NSString *)proxyAuthHeaderWithUsername:(NSString *)username andPassword:(NSString *)password {
    
    NSString *authString = [NSString stringWithFormat:@"%@:%@", username, password];
    NSData *authData = [authString dataUsingEncoding:NSUTF8StringEncoding];
    NSString *authHeader = [NSString stringWithFormat: @"Basic %@",
                            [authData base64EncodedStringWithOptions:0]];
    return authHeader;
}

@end


