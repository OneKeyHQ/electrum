//
//  OKLocalizableManager.m
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKLocalizableManager.h"

@implementation OKLocalizableManager
static dispatch_once_t once;
+ (OKLocalizableManager *)sharedInstance {
    static OKLocalizableManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKLocalizableManager alloc] init];
        [_sharedInstance setupAppLanguage:[self getCurrentLanguageType]];
    });
    return _sharedInstance;
}

+ (NSString *)getCurrentLanguageString {
    NSString *languageStr = [OKStorageManager loadFromUserDefaults:@"onekey_language"];
    if ([languageStr isKindOfClass:[NSString class]] || languageStr.length != 0) {
        return languageStr;
    }
    // （iOS获取的语言字符串比较不稳定）目前框架只处理en、zh-Hans、zh-Hant三种情况，其他按照系统默认处理
    NSString *language = [NSLocale preferredLanguages].firstObject;
    if ([language hasPrefix:@"en-"]) {
        language = @"en";
    }else if ([language hasPrefix:@"zh-"]) {
        language = @"zh-Hans"; // 简体中文
    }
    return language;
}

+ (AppLanguageType)getCurrentLanguageType {
    return [self getLanguageType:[self getCurrentLanguageString]];
}

+ (AppLanguageType)getLanguageType:(NSString *)languageString {
    if ([languageString hasPrefix:@"en"]) {
        return AppLanguageTypeEn;
    } else if ([languageString hasPrefix:@"zh"]) {
        return AppLanguageTypeZh_Hans;
    }else{//其它情况使用英文
        return AppLanguageTypeEn;
    }
}

+ (NSString *)getLanguageString:(AppLanguageType)languageType {
    NSString *languageString = @"en";
    switch (languageType) {
        case AppLanguageTypeEn:
            languageString = @"en";
            break;
        case AppLanguageTypeZh_Hans:
            languageString = @"zh-Hans";
            break;
        default:
            break;
    }
    return languageString;
}

- (void)setupAppLanguage:(AppLanguageType)languageType {
    NSString *languageString = [OKLocalizableManager getLanguageString:languageType];
    NSString *bundleName = languageString;
    NSString *path = [[NSBundle mainBundle] pathForResource:bundleName ofType:@"lproj"];
    self.languageBundle = [NSBundle bundleWithPath:path];
    self.languageType = languageType;
    AppLanguageType currentType = [OKLocalizableManager getCurrentLanguageType];
    if (currentType == languageType) {
        return;
    }
    [OKStorageManager saveToUserDefaults:languageString key:@"onekey_language"];
}
@end
