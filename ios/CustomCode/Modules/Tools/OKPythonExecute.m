//
//  OKPythonExecute.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKPythonExecute.h"
#import "OKPythonInterpreter.h"
#import "NSString+OKAdd.h"
@interface OKPythonExecute()
@property (nonatomic,strong) OKPythonInterpreter * interpreter;
@property (nonatomic,assign) PyObject * pyClass;
@property (nonatomic,assign) PyObject * pyModule;
@property (nonatomic,assign) PyObject * pyInstance;
@property(nonatomic,copy) NSString *currentClass;
@property(nonatomic,copy) void(^errorBlock) (NSError*error);
@property(nonatomic,copy) void(^successBlock)(id result);
@end

@implementation OKPythonExecute

- (instancetype)initWithModuleDirName:(NSString*)moduleDirName moduleName:(NSString*)moduleName {
    NSAssert(moduleDirName!=nil, @"模块路径名字不能为空");
    NSAssert(moduleName!=nil, @"模块名字不能为空");

    self = [super init];
    if (self) {
        _moduleDirName = moduleDirName;
        _moduleName = moduleName;
        //init pythonInterpreter
        _interpreter = [[OKPythonInterpreter alloc] init];
        
    }
    return self;
}

#pragma mark - public
- (void)executeWithClass:(NSString*)className methodName:(NSString*)methodName parameter:(NSDictionary*)parameter success:(void(^)(id result))success fail:(void(^)(NSError* error))fail {
    NSAssert(className.length!=0, @"类名不能为空");
    NSAssert(methodName!=nil, @"py方法名不能为空");
    NSAssert(parameter!=nil, @"参数不能为空");
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:parameter options:NSJSONWritingPrettyPrinted error:&error];
    NSString *paramterJsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    if (error) {
        [self p_handleWithError:error];
        return;
    }
    self.successBlock = [success copy];
    self.errorBlock = [fail copy];
    __weak typeof(self) weakSelf = self;
    [_interpreter beginTask:^{
        PyGILState_STATE state =  PyGILState_Ensure();
        //导入module
        BOOL loadSuccess = [weakSelf p_loadModuleWithClass:className];
        if(!loadSuccess) return ;
        // 执行方法
        [weakSelf p_executeMethod:methodName paramterJsonString:paramterJsonString];
        PyGILState_Release(state);
    } completion:^{
        
    }];
}


#pragma mark - private
- (BOOL)p_loadModuleWithClass:(NSString*)className {
    if ([_currentClass isEqualToString:className]) {
        return YES;
    }
    _currentClass = className;
    NSString *moudleName = [NSString stringWithFormat:@"%@.%@",_moduleDirName,_moduleName];
    const char * fileName = [moudleName UTF8String];

    
    PyObject * pModule = PyImport_ImportModule(fileName);//Python文件名
    if (!pModule) {
        PyErr_Print();
        [self p_handleWithError:[self p_errorMsg:@"导入module出错,请重试"]];
        return NO;
    }
    self.pyModule = pModule;
    self.pyClass = PyObject_GetAttrString(pModule, [className UTF8String]);
    if (!self.pyClass) {
        [self p_handleWithError:[self p_errorMsg:@"导入class出错,请重试"]];
        return NO;
    }
    self.pyInstance = PyInstanceMethod_New(self.pyClass);
    if (!self.pyInstance) {
        [self p_handleWithError:[self p_errorMsg:@"初始化对象出错,请重试"]];
        return NO;
    }
    
    return YES;
}


- (void)p_executeMethod:(NSString*)methodName paramterJsonString:(NSString*)paramterJsonString{

    PyObject *result = NULL;
    
    result = PyObject_CallMethod(self.pyInstance, [methodName UTF8String], "(N,s)",self.pyClass,[paramterJsonString UTF8String]);
    if (result == NULL) {
        [self p_handleWithError:[self p_errorMsg:@"初始化错误,请重试"]];
        return;
    }
    // 解析数据
    char * resultCString = NULL;
    PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
    if (resultCString == NULL) {
        [self p_handleWithError:[self p_errorMsg:@"无法解析数据，请重试"]];
        return;
    }
    NSError *error = nil;
    NSString *resultJsonString = [NSString ok_charToString:resultCString];
    resultJsonString = [self clearResponseString:resultJsonString];
    NSData *resultJsonData = [resultJsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:resultJsonData options:NSJSONReadingMutableContainers error:&error];
    if (error) {
        [self p_handleWithError:[self p_errorMsg:@"无法解析数据，请重试"]];
        return;
    }
    if ([resultDic isKindOfClass:[NSNull class]]) {
        [self p_handleWithError:[self p_errorMsg:@"无法解析数据，请重试"]];
        return;
    }
    if (self.successBlock) {
        self.successBlock(resultDic);
    }
}
- (NSString *)clearResponseString:(NSString *)str
{
    str = [str stringByReplacingOccurrencesOfString:@"\\n" withString:@""];
    str = [str stringByReplacingOccurrencesOfString:@"\\" withString:@""];
    str = [str substringWithRange:NSMakeRange(1, str.length - 2)];
    return str;
}

- (NSError*)p_errorMsg:(NSString*)errorMsg{
    return [NSError errorWithDomain:errorMsg code:-1 userInfo:nil];
}

- (void)p_handleWithError:(NSError*)error{

    if (error) {
        if (self.errorBlock) {
            self.errorBlock(error);
        }
    }
}

#pragma mark - getters
- (BOOL)isRuning{
    return _interpreter.running;
}
@end
