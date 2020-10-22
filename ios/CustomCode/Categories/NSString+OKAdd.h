//
//  NSString+BXAdd.h
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (OKAdd)
+(const char *)ok_stringToChar:(NSString *)string;
+(NSString *)ok_charToString:(const char *)cString;
+ (NSMutableAttributedString *)lineSpacing:(CGFloat)lineSpacing content:(NSString *)content;
@end

NS_ASSUME_NONNULL_END
