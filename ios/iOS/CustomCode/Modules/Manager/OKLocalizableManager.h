//
//  OKLocalizableManager.h
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

#define MyLocalizedString(key, comment) \
[kLocalizableManager.languageBundle localizedStringForKey:(key) value:@"" table:nil]

#define MyLocalizedStringFromTable(key, tbl, comment) \
[kLocalizableManager.languageBundle localizedStringForKey:(key) value:@"" table:(tbl)]
#define kOnekey_language            @"onekey_language"
#define kOnekey_languageSys         @"kOnekey_languageSys"

typedef NS_ENUM(NSInteger, AppLanguageType) {
    AppLanguageTypeFollowSys = 0,
    AppLanguageTypeEn,
    AppLanguageTypeZh_Hans,
};

#define kLanguageTypeKeyEn          @"en"
#define kLanguageTypeKeyCh          @"zh-Hans"




NS_ASSUME_NONNULL_BEGIN
#define kLocalizableManager (OKLocalizableManager.sharedInstance)
@interface OKLocalizableManager : NSObject
@property (nonatomic) AppLanguageType languageType;
@property (nonatomic, strong) NSBundle *languageBundle;
+ (OKLocalizableManager *)sharedInstance;
+ (NSString *)getCurrentLanguageString;
+ (AppLanguageType)getCurrentLanguageType;
- (void)setupAppLanguage:(AppLanguageType)languageType;
@end

NS_ASSUME_NONNULL_END
