//
//  OKPythonExecute.h
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Python.h>
NS_ASSUME_NONNULL_BEGIN

@interface OKPythonExecute : NSObject
@property(nonatomic,copy,readonly) NSString *moduleName; //具体的模块名
@property(nonatomic,copy,readonly) NSString *moduleDirName; //模块的路径名
@property (nonatomic,assign,readonly) BOOL isRuning;
- (instancetype)initWithModuleDirName:(NSString*)moduleDirName moduleName:(NSString*)moduleName;

- (void)executeWithClass:(NSString*)className methodName:(NSString*)methodName parameter:(NSDictionary*)parameter success:(void(^)(id result))success fail:(void(^)(NSError* error))fail;

@end

NS_ASSUME_NONNULL_END
