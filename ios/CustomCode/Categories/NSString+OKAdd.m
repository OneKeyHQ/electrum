//
//  NSString+BXAdd.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "NSString+OKAdd.h"
#import <CommonCrypto/CommonDigest.h>

@implementation NSString (OKAdd)
- (NSString *)SHA256
{
    const char *s = [self cStringUsingEncoding:NSASCIIStringEncoding];
    NSData *keyData = [NSData dataWithBytes:s length:strlen(s)];

    uint8_t digest[CC_SHA256_DIGEST_LENGTH] = {0};
    CC_SHA256(keyData.bytes, (CC_LONG)keyData.length, digest);
    NSData *out = [NSData dataWithBytes:digest length:CC_SHA256_DIGEST_LENGTH];
    NSString *hash = [NSData hexStringForData:out];
    return hash;
}
+(const char *)ok_stringToChar:(NSString *)string
{
    return [string UTF8String];
}
+(NSString *)ok_charToString:(const char *)cString
{
    return [NSString stringWithUTF8String:cString];
}

// 十六进制转换为普通字符串的。
+ (NSString *)stringFromHexString:(NSString *)hexString {
    char *myBuffer = (char *)malloc((int)[hexString length] / 2 + 1);
    bzero(myBuffer, [hexString length] / 2 + 1);
    for (int i = 0; i < [hexString length] - 1; i += 2) {
        unsigned int anInt;
        NSString * hexCharStr = [hexString substringWithRange:NSMakeRange(i, 2)];
        NSScanner * scanner = [[NSScanner alloc] initWithString:hexCharStr];
        [scanner scanHexInt:&anInt];
        myBuffer[i / 2] = (char)anInt;
    }
    NSString *unicodeString = [NSString stringWithCString:myBuffer encoding:4];
    return unicodeString;
}

+ (NSMutableAttributedString *)lineSpacing:(CGFloat)lineSpacing content:(NSString *)content
{
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc]initWithString:content];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
    [paragraphStyle setLineSpacing:lineSpacing];
    return attributedString;
}
//根据宽度求高度  content 计算的内容  width 计算的宽度 font字体大小
- (CGFloat)getLabelHeightWithWidth:(CGFloat)width font: (CGFloat)font
{
    CGRect rect = [self boundingRectWithSize:CGSizeMake(width, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:font]} context:nil];

    return rect.size.height;
}
//根据高度度求宽度  text 计算的内容  Height 计算的高度 font字体大小
- (CGFloat)getWidthWithHeight:(CGFloat)height font:(CGFloat)font{

    CGRect rect = [self boundingRectWithSize:CGSizeMake(MAXFLOAT, height)
                                        options:NSStringDrawingUsesLineFragmentOrigin
                                     attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:font]}
                                        context:nil];
    return rect.size.width;
}

- (BOOL)isValid {
    return ([self isKindOfClass:[NSString class]] && self.length != 0);
}

- (BOOL)isNumbersOrLetters {
    NSString *verifyRegex = @"[0-9A-Za-z]";
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", verifyRegex];
    return [predicate evaluateWithObject:self];
}

- (BOOL)isNumbers {
    NSString *verifyRegex = @"[0-9]+";
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", verifyRegex];
    return [predicate evaluateWithObject:self];
}

- (BOOL)isEnglish {
    NSString *regex = @"[A-Za-z]+";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", regex];
    BOOL isMatch = [pred evaluateWithObject:self];
    return isMatch;
}

- (BOOL)isChinese { // 纯中文
    NSString *regex = @"^[\u4E00-\u9FA5]+$";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", regex];
    BOOL isMatch = [pred evaluateWithObject:self];
    return isMatch;
}

- (BOOL)containsChinese { // 包含中文
    BOOL contains = NO;
    for(NSInteger i = 0; i < [self length]; i++) {
        if ((int)[self characterAtIndex:i] > 127) {
            contains = YES;
            break;
        }
    }
    return contains;
}

+ (BOOL)isHexString:(NSString *)str { // 是否是十六进制字符串
    NSRegularExpression *regularExpression = [NSRegularExpression regularExpressionWithPattern:@"[A-Fa-f0-9]" options:NSRegularExpressionCaseInsensitive error:nil];
    NSUInteger num = [regularExpression numberOfMatchesInString:str options:NSMatchingReportProgress range:NSMakeRange(0, str.length)];
    if (num == str.length) {
        return YES;
    } else {
        return NO;
    }
}

- (NSURL *)toURL {
    return [NSURL URLWithString:self];
}

- (NSDictionary *)toDict {
    NSData *jsonData = [self dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;

    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingAllowFragments error:&err];
    if (err) {
        NSLog(@"ERROR: %@", err);
        return nil;
    }
    return dict;
}

- (NSArray<NSString *> *)split:(NSString *)sparator {
    return [self componentsSeparatedByString:sparator];
}

- (NSString *)localized {
    return MyLocalizedString(self, nil);
}
@end
