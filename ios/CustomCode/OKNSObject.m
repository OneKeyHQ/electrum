//
//  OKNSObject.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/29.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKNSObject.h"

@implementation OKNSObject

- (instancetype)init
{
    if (self = [super init]) {
    }
    return self;
}

- (void)onCallback:(NSString *)str1
{
    if (str1.length == 0 || str1 == nil) {
        return;
    }
    NSArray *array = [str1 componentsSeparatedByString:@"="];
    NSString *type = [array firstObject];
    NSString *dictStr = [array lastObject];
    if ([dictStr isEqualToString:@"{}"] || dictStr.length == 0 ||type.length == 0 || type == nil) {
        return;
    }
    NSDictionary *dict = [dictStr mj_JSONObject];
    if (dict == nil) {
        return;
    }
    if ([type isEqualToString:@"update_status"]) {
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiUpdate_status object:dict];
    }
}
@end
