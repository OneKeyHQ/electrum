//
//  OKVersion.m
//  OneKey
//
//  Created by zj on 2021/2/4.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKVersion.h"

@interface OKVersion()
@property (nonatomic, strong) NSArray<NSNumber *> *versionArray;
@end

@implementation OKVersion

+ (instancetype)versionWithString:(NSString *)versionString {
    NSArray<NSNumber *> *versionArray = [OKVersion arrayWithVersionString:versionString];
    OKVersion *version = [[OKVersion alloc] initWithVersionArray:versionArray];
    return version;
}

+ (instancetype)versionWithArray:(NSArray *)versionArray {
    OKVersion *version = [[OKVersion alloc] initWithVersionArray:versionArray];
    return version;
}

- (instancetype)initWithVersionArray:(NSArray<NSNumber *> *)versionArray {
    self = [super init];
    if (self) {
        _versionArray = versionArray;
    }
    return self;
}

+ (NSArray<NSNumber *> *)arrayWithVersionString:(NSString *)versionString {

    NSArray *versionStrComponents = [versionString split:@"."];
    NSArray *versionNumComponents = [versionStrComponents ok_map:^id(id obj) {
        return @([obj integerValue]);
    }];
    return versionNumComponents;
}

- (BOOL)versionEqualTo:(OKVersion *)version {
    // A == B
    NSInteger maxLen = MAX(self.versionArray.count, version.versionArray.count);
    NSArray *versionArray_A = [self.versionArray ok_padding:@(0) to:maxLen];
    NSArray *versionArray_B = [version.versionArray ok_padding:@(0) to:maxLen];

    for (int i = 0; i < maxLen; i++) {
        NSInteger A = [versionArray_A[i] integerValue];
        NSInteger B = [versionArray_B[i] integerValue];
        if (A != B) {
            return NO;
        }
    }
    return YES;
}

- (BOOL)versionGreaterThen:(OKVersion *)version {
    // A > B
    NSInteger maxLen = MAX(self.versionArray.count, version.versionArray.count);
    NSArray *versionArray_A = [self.versionArray ok_padding:@(0) to:maxLen];
    NSArray *versionArray_B = [version.versionArray ok_padding:@(0) to:maxLen];

    for (int i = 0; i < maxLen; i++) {
        NSInteger A = [versionArray_A[i] integerValue];
        NSInteger B = [versionArray_B[i] integerValue];
        if (A == B) {
            continue;;
        }
        return A > B;
    }
    return NO; // A == B
}

- (BOOL)versionLessThen:(OKVersion *)version {
    if ([self versionEqualTo:version]) {
        return NO;
    }
    return [version versionGreaterThen:self];
}

+ (BOOL)versionString:(NSString *)A lessThen:(NSString *)B {
    OKVersion *VerA = [OKVersion versionWithString:A];
    OKVersion *VerB = [OKVersion versionWithString:B];
    return [VerA versionLessThen:VerB];
}
@end
