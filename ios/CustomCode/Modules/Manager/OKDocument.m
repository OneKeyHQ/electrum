//
//  OKDocument.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDocument.h"

@implementation OKDocument
- (BOOL)loadFromContents:(id)contents ofType:(NSString *)typeName error:(NSError * _Nullable __autoreleasing *)outError {
    
    self.myData = contents;
    
    return YES;
}

- (nullable id)contentsForType:(NSString *)typeName error:(NSError * _Nullable __autoreleasing *)outError

{
    if(!self.myData)
    {
        self.myData = [[NSData alloc] init];
    }
    return self.myData;
}
@end
