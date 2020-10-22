//
//  NSString+BXAdd.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "NSString+OKAdd.h"

@implementation NSString (OKAdd)
+(const char *)ok_stringToChar:(NSString *)string
{
    return [string UTF8String];
}
+(NSString *)ok_charToString:(const char *)cString
{
    return [NSString stringWithUTF8String:cString];
}
+ (NSMutableAttributedString *)lineSpacing:(CGFloat)lineSpacing content:(NSString *)content
{
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc]initWithString:content];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, content.length)];
    [paragraphStyle setLineSpacing:lineSpacing];
    return attributedString;
}
@end
