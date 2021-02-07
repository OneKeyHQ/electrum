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
@property (nonatomic, strong) NSURLSession *proxySession;
@property (nonatomic, strong) NSDictionary *proxyDict;
@end

@implementation OKURLSchemeHandler

- (void)webView:(nonnull WKWebView *)webView startURLSchemeTask:(nonnull id<WKURLSchemeTask>)urlSchemeTask {

    NSURLSession *session;
    NSURL *url = urlSchemeTask.request.URL;
    if ([self shouldUseProxyWithUrl:url]) {
        session = self.proxySession;
    } else {
        session = [NSURLSession sharedSession];
    }

    __weak id<WKURLSchemeTask> weakUrlSchemeTask = urlSchemeTask;
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:urlSchemeTask.request
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

- (NSURLSession *)proxySession {
    if (!_proxySession) {

        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
        configuration.connectionProxyDictionary = self.proxyDict;
        NSString *username = @"onekey";
        NSString *password = @"libbitcoinconsensus";
        NSString *authHeader = [self proxyAuthHeaderWithUsername:username andPassword:password];
        [configuration setHTTPAdditionalHeaders:@{
            @"Proxy-Authorization": authHeader
        }];

        _proxySession = [NSURLSession sessionWithConfiguration:configuration];
    }
    return _proxySession;
}

- (NSDictionary *)proxyDict {
    if (!_proxyDict) {
        NSString *proxyHost = @"cdn.onekey.so";
        NSNumber *proxyPort = @(443);
        _proxyDict = @{
            (NSString *)kCFNetworkProxiesHTTPEnable: @(1),
            (NSString *)kCFNetworkProxiesHTTPProxy: proxyHost,
            (NSString *)kCFNetworkProxiesHTTPPort: proxyPort,

            @"HTTPSEnable" : @(1),
            @"HTTPSProxy": proxyHost,
            @"HTTPSPort": proxyPort,
        };
    }
    return _proxyDict;
}

- (BOOL)shouldUseProxyWithUrl:(NSURL *)url {
    return NO;
}

- (NSString *)proxyAuthHeaderWithUsername:(NSString *)username andPassword:(NSString *)password {

    NSString *authString = [NSString stringWithFormat:@"%@:%@", username, password];
    NSData *authData = [authString dataUsingEncoding:NSUTF8StringEncoding];
    NSString *authHeader = [NSString stringWithFormat: @"Basic %@",
                            [authData base64EncodedStringWithOptions:0]];
    return authHeader;
}

@end
