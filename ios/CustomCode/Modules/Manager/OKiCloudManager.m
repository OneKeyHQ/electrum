//
//  OKiCloudManager.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKiCloudManager.h"
#import "OKDocument.h"

@implementation OKiCloudManager

+ (BOOL)iCloudEnable {
    
    NSFileManager *manager = [NSFileManager defaultManager];
    
    NSURL *url = [manager URLForUbiquityContainerIdentifier:nil];

    if (url != nil) {
        
        return YES;
    }
    
    NSLog(@"iCloud 不可用");
    return NO;
}

+ (void)downloadWithDocumentURL:(NSURL*)url callBack:(downloadBlock)block {
    
    OKDocument *iCloudDoc = [[OKDocument alloc]initWithFileURL:url];
    
    [iCloudDoc openWithCompletionHandler:^(BOOL success) {
        if (success) {
            
            [iCloudDoc closeWithCompletionHandler:^(BOOL success) {
                NSLog(@"关闭成功");
            }];
            
            if (block) {
                block(iCloudDoc.myData);
            }
            
        }
    }];
}
@end
