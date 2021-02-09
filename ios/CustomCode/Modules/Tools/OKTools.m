//
//  OKTools.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTools.h"
#import "KeyChainSaveUUID.h"

@interface  OKTools()
@property (nonatomic, strong) UIActivityIndicatorView *indicatorView;;
@end


@implementation OKTools
+ (OKTools *)sharedInstance {
    static OKTools *_sharedInstance = nil;
    static dispatch_once_t pred;
    dispatch_once(&pred, ^{
        _sharedInstance = [[OKTools alloc] init];
    });
    return _sharedInstance;
}
- (UIActivityIndicatorView *)indicatorView {
    if (_indicatorView == nil) {
        _indicatorView = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
        _indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        _indicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6];
    }
    return _indicatorView;
}
- (void)showIndicatorView {
    [self showLoadingViewEnabled:NO alpha:0.6];
}

- (void)hideIndicatorView {
    dispatch_async(dispatch_get_main_queue(), ^{
        OKKeyWindow.subviews.firstObject.userInteractionEnabled = YES;
        [self.indicatorView stopAnimating];
        [self.indicatorView removeFromSuperview];
    });
}

- (void)showLoadingViewEnabled:(BOOL)userInteractionEnabled alpha:(CGFloat)alpha {
    dispatch_async(dispatch_get_main_queue(), ^{
        OKKeyWindow.subviews.firstObject.userInteractionEnabled = userInteractionEnabled;
        self.indicatorView.frame = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        self.indicatorView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        [self.indicatorView startAnimating];
        [OKKeyWindow addSubview:self.indicatorView];
        self.indicatorView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:alpha];
    });
}



- (void)pasteboardCopyString:(NSString *)string msg:(NSString *)msg {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    if (string != nil) {
        pasteboard.string = string;
    }
    if ([msg isKindOfClass:[NSString class]] && msg.length != 0) {
        [kTools tipMessage:msg];
    } else {
        [kTools tipMessage:MyLocalizedString(@"Copied to pasteboard", nil)];
    }
}
- (void)tipMessage:(NSString *)msg {
        dispatch_main_async_safe(
            if (msg == nil || ![msg isKindOfClass:[NSString class]] ||msg.length == 0) {
            return;
            }
            UIWindow *window = [[[UIApplication sharedApplication] windows] firstObject];
            if ([window viewWithTag:20201029] != nil) {
            return;
            }
            __block UIView *view = [[UIView alloc] initWithFrame:[UIScreen mainScreen].bounds];
            [view setTag:20201029];
            [view setUserInteractionEnabled:NO];
            [window addSubview:view];
            CSToastStyle *style = [CSToastStyle new];
            style = [[CSToastStyle alloc] initWithDefaultStyle];
            style.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
            [view makeToast:msg duration:2.0 position:CSToastPositionCenter style:style];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [view removeFromSuperview];
            view = nil;
        });)
}
- (void)debugTipMessage:(NSString *)msg {
#ifdef DEBUG
    [self tipMessage:[@"DEBUG: " stringByAppendingString:msg]];
#endif
}

- (NSString *)immutableUUID { // 不可变的UUID
    if (!_immutableUUID) {
        _immutableUUID = [KeyChainSaveUUID getDeviceIDInKeychain];
    }
    return _immutableUUID;
}
- (BOOL)okJumpOpenURL:(NSString *)urlStr {
    NSURL *url = [NSURL URLWithString:urlStr];
    if ([[UIDevice currentDevice] systemVersion].doubleValue < 10.0) {
        if ([[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url];
            return YES;
        } else {
            return NO;
        }
    } else {
        if ([[UIApplication sharedApplication] respondsToSelector:@selector(openURL:options:completionHandler:)]) {
            [[UIApplication sharedApplication] openURL:url options:@{UIApplicationOpenURLOptionsSourceApplicationKey:@YES} completionHandler:nil];
            return YES;
        } else {
            return NO;
        }
    }
}
- (NSString *)getAppVersionString {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}

- (NSString *)getAppDisplayName {
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
}

- (NSString *)getAppBundleID {
    return [[NSBundle mainBundle] bundleIdentifier];
}

- (NSDecimalNumber *)decimalNumberHandlerWithValue:(NSDecimalNumber *)value roundingMode:(NSRoundingMode)mode scale:(NSInteger)scale {
    NSDecimalNumberHandler *handler = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:mode scale:scale raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:NO];
    return [value decimalNumberByRoundingAccordingToBehavior:handler];
}

- (int)findNumFromStr:(NSString *)string
{
    // Intermediate
    NSMutableString *numberString = [[NSMutableString alloc] init];
    NSString *tempStr = @"";
    NSScanner *scanner = [NSScanner scannerWithString:string];
    NSCharacterSet *numbers = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];

    while (![scanner isAtEnd]) {
        // Throw away characters before the first number.
        [scanner scanUpToCharactersFromSet:numbers intoString:NULL];

        // Collect numbers.
        [scanner scanCharactersFromSet:numbers intoString:&tempStr];
        if (tempStr != nil) {
            [numberString appendString:tempStr];
        }
        tempStr = @"";
        break;
    }
    // Result.
    int number = [numberString intValue];

    return number;
}

- (void)alertTips:(NSString *)title desc:(NSString *)desc confirm:(void(^)(void))cblock cancel:(void(^)(void))cancel vc:(UIViewController *)vc conLabel:(NSString *)conLabel isOneBtn:(BOOL)oneBtn
{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:desc preferredStyle:UIAlertControllerStyleAlert];
    if (oneBtn == NO) {
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:MyLocalizedString(@"cancel", nil) style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            if (cancel) {
                cancel();
            }
        }];
        [alert addAction:cancelAction];
    }
    UIAlertAction *updateAction = [UIAlertAction actionWithTitle:conLabel style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (cblock) {
            cblock();
        }
    }];
    [alert addAction:updateAction];
    [vc presentViewController:alert animated:YES completion:nil];
}



#define USER_APP_PATH                 @"/User/Applications/"
- (BOOL)isJailBreak
{
    if ([[NSFileManager defaultManager] fileExistsAtPath:USER_APP_PATH]) {
        NSArray *applist = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:USER_APP_PATH error:nil];
        return YES;
    }
    return NO;
}

- (BOOL)isNotchScreen {
    if (@available(iOS 11.0, *)) {
        CGFloat height = [[UIApplication sharedApplication] delegate].window.safeAreaInsets.bottom;
        return (height > 0);
    } else {
        return NO;
    }
}

- (BOOL)clearDataWithFilePath:(NSString *)path{
    NSArray *subPathArr = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:path error:nil];
    NSString *filePath = nil;
    NSError *error = nil;
    for (NSString *subPath in subPathArr)
    {
        filePath = [path stringByAppendingPathComponent:subPath];
        [[NSFileManager defaultManager] removeItemAtPath:filePath error:&error];
        if (error) {
            return NO;
        }
    }
    return YES;
}


#pragma mark - 比较版本
+ (NSInteger)compareVersion:(NSString *)version1 version2:(NSString *)version2//要完成的子函数
  {
      if(version1==version2)// 如果相等，直接返回
          return 0;
      NSInteger i=0,j=0,s1=version1.length,s2=version2.length,start1=0,start2=0,result1,result2;
      while(start1<s1||start2<s2)//start1表示第一个字符串中当前数字从哪里开始，start2同理
      {//只要有一个字符串还有数字，那么就继续比较下去
          if(start1<s1&&start2<s2)//如果两个字符串都有数字
          {
              while(i<s1)//i记录'.'的位置，或者到达字符串最末端i=version1.size()
              {
                  if([version1 characterAtIndex:i]=='.')
                      break;
                  i++;
              }
              while(j<s2)//j同理，记录'.'的位置或者version2.size()
              {
                  if([version2 characterAtIndex:j]=='.')
                      break;
                  j++;
              }
              result1 = [self str2int:[version1 substringWithRange:NSMakeRange(start1, i-start1)]];//取出当前子字符串，转化为数字，存储在result1中
              result2= [self str2int:[version2 substringWithRange:NSMakeRange(start2, j-start2)]];//取出当前子字符串，转化为数字，存储在result2中
              if(result1>result2)//如果第一个大于第二个，那么返回1
                  return 1;
              else if(result1<result2)//如果第一个小于第二个，那么返回-1
                  return -1;
              else//如果当前这两个数字相等，那么继续比较下去
              {
                  start1=i+1;//更新start1的值
                  i=start1;//更新i的值
                  start2=j+1;//更新start2的值
                  j=start2;//更新j的值
              }
          }
          else if(start1<s1)//如果只有第一个字符串还有数字
          {
              while(i<s1)
              {
                  if([version1 characterAtIndex:i]=='.')
                      break;
                  i++;
              }
              result1 = [self str2int:[version1 substringWithRange:NSMakeRange(start1, i-start1)]];
              if(result1>0)//如果大于0，那么立马返回1，不用再比较了
                  return 1;
              else//如果等于0，那么继续比较下去
              {
                  start1=i+1;
                  i=start1;
              }
          }
          else if(start2<s2)//如果只有第二个字符串有数字
          {
              while(j<s2)
              {
                  if([version2 characterAtIndex:j]=='.')
                      break;
                  j++;
              }
              result2 = [self str2int:[version2 substringWithRange:NSMakeRange(start2, j-start2)]];
              if(result2>0)//如果大于0，那么停止比较，返回-1
                  return -1;
              else
              {
                  start2=j+1;
                  j=start2;
              }
          }
      }
      return 0;
}
+ (NSInteger)str2int:(NSString *)iStr//把字符串转为数字，返回得到的数字
{
      NSInteger res=0;
      for(NSInteger j=0;j<iStr.length;j++)
          res=10*res+[iStr characterAtIndex:j]-'0';
      return res;
}
@end
