//
//  OKPyCommandsManager.m
//  OneKey
//
//  Created by bixin on 2020/10/27.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKPyCommandsManager.h"
@interface OKPyCommandsManager()
@property (nonatomic,assign)PyObject *pyClass;
@end

@implementation OKPyCommandsManager
static dispatch_once_t once;
+ (OKPyCommandsManager *)sharedInstance {
    static OKPyCommandsManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        PyGILState_STATE state = PyGILState_Ensure();
        _sharedInstance = [[OKPyCommandsManager alloc] init];
        PyObject *pModule = PyImport_ImportModule([@"api.android.console" UTF8String]);//导入模块
        if (pModule == NULL) {
               PyErr_Print();
        }
        PyObject *pyClass = PyObject_GetAttrString(pModule, [@"AndroidCommands" UTF8String]);//获取类
        _sharedInstance.pyClass = pyClass;
        PyObject *pyConstract = PyInstanceMethod_New(pyClass);
//        PyObject* pArgs = NULL;
//        pArgs = PyTuple_New(1);
//        PyTuple_SetItem(pArgs, 0, Py_BuildValue("O",self));
        PyObject* pIns = PyObject_CallObject(pyConstract,NULL);//创建实例
        if (pIns == NULL) {
            PyErr_Print();
        }
        _sharedInstance.pyInstance = pIns;
        PyGILState_Release(state);
    });
    return _sharedInstance;
}

- (NSDictionary *)callInterface:(NSString *)method parameter:(NSDictionary *)parameter
{
    if (parameter == nil) {
        parameter = [NSDictionary dictionary];
    }
    PyGILState_STATE state = PyGILState_Ensure();
    PyObject *result = NULL;
    
    if ([method isEqualToString:kInterfaceGet_tx_info]) {
        NSString *tx_hash = [parameter safeStringForKey:@"tx_hash"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "s",[tx_hash UTF8String]);
    
    
    }else if([method isEqualToString:kInterfaceLoad_wallet]){
        NSString *name = [parameter safeStringForKey:@"name"];
        PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)",[name UTF8String]);
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceSelect_wallet UTF8String], "(s)",[name UTF8String]);
    
    
    }else if([method isEqualToString:kInterfaceGet_all_tx_list]){
        NSString *search_type = [parameter safeStringForKey:@"search_type"];
        if (search_type == nil || search_type.length == 0) {
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_all_tx_list UTF8String], "");
        }else{
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_all_tx_list UTF8String], "(s)",[search_type UTF8String]);
        }
    
    
    }else if([method isEqualToString:kInterfaceGet_default_fee_status]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_default_fee_status UTF8String], "");
    
    
        
    }else if([method isEqualToString:kInterfaceGet_fee_by_feerate]){
        NSString *outputs = [parameter safeStringForKey:@"outputs"];
        NSString *message = [parameter safeStringForKey:@"message"];
        NSString *feerate = [parameter safeStringForKey:@"feerate"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_fee_by_feerate UTF8String], "(s,s,i)", [outputs UTF8String],[message UTF8String],[feerate longLongValue]);
    }
    
    
    if (result == NULL) {
         PyErr_Print();
    }
    char * resultCString = NULL;
    PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
    PyGILState_Release(state);
    NSLog(@"%s", resultCString);
    
    return nil;
}
@end
