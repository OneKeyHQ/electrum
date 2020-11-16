//
//  OKLocalizableManager.m
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKLocalizableManager.h"
@interface OKLocalizableManager()

@end

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
    NSString *languageStr = [OKStorageManager loadFromUserDefaults:kOnekey_language];
    if ([languageStr isKindOfClass:[NSString class]] && languageStr.length != 0 && ![languageStr isEqualToString:kOnekey_languageSys]) {
        return languageStr;
    }
    NSString *language = [NSLocale preferredLanguages].firstObject;
    if ([language hasPrefix:@"en-"]) {
        language = @"en";
    }else if ([language hasPrefix:@"zh-"]) {
        language = @"zh-Hans";
    }
    return language;
}

+ (AppLanguageType)getCurrentLanguageType {
    NSString *languageStr = [OKStorageManager loadFromUserDefaults:kOnekey_language];
    if ([languageStr isEqualToString:kOnekey_languageSys]) {
        return AppLanguageTypeFollowSys;
    }
    return [self getLanguageType:[self getCurrentLanguageString]];
}

+ (AppLanguageType)getLanguageType:(NSString *)languageString {
    if ([languageString isEqualToString:kOnekey_languageSys]) {
        return AppLanguageTypeFollowSys;
    }else{
        if ([languageString hasPrefix:@"en"]) {
            return AppLanguageTypeEn;
        } else if ([languageString hasPrefix:@"zh"]) {
            return AppLanguageTypeZh_Hans;
        }else {//其它情况使用英文
            return AppLanguageTypeEn;
        }
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
        case AppLanguageTypeFollowSys:
            languageString = kOnekey_languageSys;
        default:
            break;
    }
    return languageString;
}

- (void)setupAppLanguage:(AppLanguageType)languageType {
    NSString *languageString = [OKLocalizableManager getLanguageString:languageType];
    NSString *bundleName = languageString;
    if ([languageString isEqualToString:kOnekey_languageSys]) {
        NSArray  *languages = [NSLocale preferredLanguages];
        NSString *language = [languages objectAtIndex:0];
        if ([language hasPrefix:@"en"]) {
            bundleName = @"en";
        } else if ([language hasPrefix:@"zh"]) {
            bundleName = @"zh-Hans";
        }else {//其它情况使用英文
            bundleName = @"en";
        }
    }
    
    NSString *path = [[NSBundle mainBundle] pathForResource:bundleName ofType:@"lproj"];
    self.languageBundle = [NSBundle bundleWithPath:path];
    self.languageType = languageType;
    AppLanguageType currentType = [OKLocalizableManager getCurrentLanguageType];
    if (currentType == languageType) {
        return;
    }
    if (![languageString isEqualToString:kOnekey_languageSys]) {
        [OKStorageManager saveToUserDefaults:languageString key:kOnekey_language];
    }else{
        [OKStorageManager saveToUserDefaults:kOnekey_languageSys key:kOnekey_language];
    }
}
@end
