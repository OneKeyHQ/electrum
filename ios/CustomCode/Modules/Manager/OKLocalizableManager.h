//
//  OKLocalizableManager.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MyLocalizedString(key, comment) \
[kLocalizableManager.languageBundle localizedStringForKey:(key) value:@"" table:nil]

#define MyLocalizedStringFromTable(key, tbl, comment) \
[kLocalizableManager.languageBundle localizedStringForKey:(key) value:@"" table:(tbl)]

typedef NS_ENUM(NSInteger, AppLanguageType) {
    AppLanguageTypeEn = 0,
    AppLanguageTypeZh_Hans, // 中文
};
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
