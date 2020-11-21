//
//  OKPyCommandsManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/27.
//  Copyright © 2020 OneKey. All rights reserved..
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
        
        
        PyObject *value =  Py_BuildValue("(s)", [kTools.immutableUUID UTF8String]);
//        PyObject *kwargs;
//        kwargs = Py_BuildValue("{s:s,s:s}", "user_dir", "lalalallalal","callback","xxxxxx");
        PyObject* pIns = PyObject_Call(pyConstract,value,NULL);//创建实例
//        Py_DECREF(kwargs);
        if (pIns == NULL) {
            PyErr_Print();
        }
        _sharedInstance.pyInstance = pIns;
        PyGILState_Release(state);
    });
    return _sharedInstance;
}

- (id)callInterface:(NSString *)method parameter:(NSDictionary *)parameter
{
    if (parameter == nil) {
        parameter = [NSDictionary dictionary];
    }
    PyGILState_STATE state = PyGILState_Ensure();
    PyObject *result = NULL;
    
    if ([method isEqualToString:kInterfaceGet_tx_info]) {
        NSString *tx_hash = [parameter safeStringForKey:@"tx_hash"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "s",[tx_hash UTF8String]);
    
    
    }else if([method isEqualToString:kInterfaceCreate_hd_wallet]){
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *seed = [parameter safeStringForKey:@"seed"];
        if (seed == NULL || seed.length == 0) {
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_hd_wallet UTF8String], "(s)",[password UTF8String]);
        }else{
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_hd_wallet UTF8String], "(s,s)",[password UTF8String],[seed UTF8String]);
        }
        
    }else if([method isEqualToString:kInterfaceCreate_derived_wallet]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_derived_wallet UTF8String], "(s,s,s)",[name UTF8String],[password UTF8String],[coin UTF8String]);

        
    }else if([method isEqualToString:kInterfaceCreate_create]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_create UTF8String], "(s,s)",[name UTF8String],[password UTF8String]);
        
    }else if([method isEqualToString:kInterfaceImport_Privkeys]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *privkeys = [parameter safeStringForKey:@"privkeys"];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s}", "password", [password UTF8String],"privkeys",[privkeys UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
    
     
    }else if([method isEqualToString:kInterfaceImport_Seed]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *seed = [parameter safeStringForKey:@"seed"];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s}", "password", [password UTF8String],"seed",[seed UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
        
        
    }else if([method isEqualToString:kInterfaceImport_Address]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *addresses = [parameter safeStringForKey:@"address"];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s}", "addresses", [addresses UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
        
        
        
    }else if([method isEqualToString:kInterfaceDelete_wallet]){
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *name = [parameter safeStringForKey:@"name"];
        PyObject *args =  Py_BuildValue("(s)", [password UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s}", "name", [name UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceDelete_wallet UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
        
        
    }else if([method isEqualToString:kInterfaceImport_xpub]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *master = [parameter safeStringForKey:@"master"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_create UTF8String], "(s,s)",[name UTF8String],[master UTF8String]);
        
        
    }else if([method isEqualToString:kInterfaceLoad_all_wallet]){
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "()",NULL);
    
        
        
    }else if([method isEqualToString:kInterfaceSelect_wallet]){
        NSString *selectName = [parameter safeStringForKey:@"name"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)",[selectName UTF8String]);
        
    
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
    
    
    
    }else if([method isEqualToString:kInterfaceMktx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceMktx UTF8String], "(s)", [tx UTF8String]);

    }else if([method isEqualToString:kInterfaceRemove_local_tx]){
        NSString *delete_tx = [parameter safeStringForKey:@"delete_tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceRemove_local_tx UTF8String], "(s)", [delete_tx UTF8String]);
    
    
    }else if([method isEqualToString:kInterfaceSign_tx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        NSString *password = [parameter safeStringForKey:@"password"];
        PyObject *args =  Py_BuildValue("(s)", [tx UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s}", "password", [password UTF8String],"path","");
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceSign_tx UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
        
        
    }else if([method isEqualToString:kInterfaceList_wallets]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceList_wallets UTF8String], "()", NULL);
     
        
    }else if([method isEqualToString:kInterfaceGet_currencies]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_currencies UTF8String], "()", NULL);
      
    }else if([method isEqualToString:kInterfaceSet_currency]){
        NSString *ccy = [parameter safeStringForKey:@"ccy"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceSet_currency UTF8String], "(s)",[ccy UTF8String]);
        
    }else if([method isEqualToString:kInterfaceSet_base_uint]){
        //比特币单位(BTC/mBTC/bits/sat)
        NSString *base_unit = [parameter safeStringForKey:@"base_unit"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceSet_base_uint UTF8String], "(s)",[base_unit UTF8String]);
    
        
    }else if([method isEqualToString:kInterfaceget_wallet_address_show_UI]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_wallet_address_show_UI UTF8String], "()");
        
        
    }else if([method isEqualToString:kInterfaceUpdate_password]){
        NSString *old_password = [parameter safeStringForKey:@"old_password"];
        NSString *new_password = [parameter safeStringForKey:@"new_password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceUpdate_password UTF8String], "(s,s)",[old_password UTF8String],[new_password UTF8String]);
        
        
    }else if([method isEqualToString:kInterfaceget_all_mnemonic]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_all_mnemonic UTF8String], "()");
        
        
    }else if([method isEqualToString:kInterfaceget_backup_info]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_backup_info UTF8String], "()");
        

    }else if([method isEqualToString:kInterfacedelete_backup_info]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfacedelete_backup_info UTF8String], "()");
        
        
    }else if([method isEqualToString:kInterfaceexport_seed]){
        NSString *password = [parameter safeStringForKey:@"password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceexport_seed UTF8String], "(s)",[password UTF8String]);

    }else if([method isEqualToString:kInterfaceget_all_wallet_balance]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_all_wallet_balance UTF8String], "()",NULL);
        
        
        
    }else if([method isEqualToString:kInterfacerename_wallet]){
        NSString * old_name = [parameter safeStringForKey:@"old_name"];
        NSString * new_name = [parameter safeStringForKey:@"new_name"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfacerename_wallet UTF8String], "(s,s)",[old_name UTF8String],[new_name UTF8String]);
        
        
    }else if([method isEqualToString:kInterfaceexport_privkey]){
        NSString * password = [parameter safeStringForKey:@"password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceexport_privkey UTF8String], "(s)",[password UTF8String]);

    }else if([method isEqualToString:kInterfaceget_exchange_currency]){
        NSString * type = [parameter safeStringForKey:@"type"];
        NSString * amount = [parameter safeStringForKey:@"amount"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_exchange_currency UTF8String], "(s,s)",[type UTF8String],[amount UTF8String]);
        
    }else if([method isEqualToString:kInterfaceBroadcast_tx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceBroadcast_tx UTF8String], "(s)", [tx UTF8String]);
    }
  
    if (result == NULL) {
//        PyErr_Print();
        if (PyErr_Occurred()) {
            PyObject* ptype,*pvalue,*ptraceback;
            PyObject* pystr;
            char *msg;
            PyErr_Fetch(&ptype,&pvalue,&ptraceback);
            pystr = PyObject_Str(pvalue);
            PyArg_Parse(pystr, "s", &msg);
            NSLog(@"错误信息  %s  method = %@ parameter = %@", msg ,method,parameter);
            // 释放GIL ！！！！！
            PyGILState_Release(state);
            dispatch_main_async_safe(
                [kTools tipMessage:[NSString stringWithCString:msg encoding:NSUTF8StringEncoding]];
            );
            
            return nil;
        }
    }
    id object;
    if (result != NULL) {
        object =  [self getFormatSpecifier:result];
    }
    PyGILState_Release(state);
    if (result != NULL) {
        Py_DECREF(result);
    }
    return object;
}


+ (void)setNetwork
{
    PyGILState_STATE state = PyGILState_Ensure();
    
    PyObject *pModule = PyImport_ImportModule([@"electrum.constants" UTF8String]);//导入模块
    if (pModule == NULL) {
        PyErr_Print();
    }
    PyObject *pyClass = PyObject_GetAttrString(pModule, [@"set_regtest" UTF8String]);//获取类
    PyObject *result = NULL;
    result = PyObject_CallFunction(pyClass, "()");
    if (result == NULL) {
        PyErr_Print();
    }
    char * resultCString1 = NULL;
    PyArg_Parse(result, "s", &resultCString1); //将python类型的返回值转换为c
    PyGILState_Release(state);
}

- (id)getFormatSpecifier:(PyObject *)result
{
    NSString *tp_name = [NSString stringWithCString:result->ob_type->tp_name encoding:NSUTF8StringEncoding];
    if ([tp_name isEqualToString:@"str"]) { //字符串
        char *  resultCString = NULL;
        PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
        if (resultCString == NULL) {
            return @"";
        }
        NSString *jsonStrResult = [NSString stringWithCString:resultCString encoding:NSUTF8StringEncoding];
        id object = [jsonStrResult mj_JSONObject];
        if (object == NULL) {
            return jsonStrResult;
        }else{
            return object;
        }
        
    }else if([tp_name isEqualToString:@"bool"]) { //布尔
        bool boolnum;
        PyArg_Parse(result, "i", &boolnum); //将python类型的返回值转换为c
        return @(boolnum);
        
    }else if ([tp_name isEqualToString:@"int"]){  //整型
        bool boolnum;
        PyArg_Parse(result, "i", &boolnum); //将python类型的返回值转换为c
        return @(boolnum);
    }else if ([tp_name isEqualToString:@"NULL"]){ //空
        return @"";
    }else if ([tp_name isEqualToString:@"NoneType"]){
        return @"";
    }
    return @"";
}



//数组等过于复杂的类型  传递json字符串
//else if ([tp_name isEqualToString:@"list"]){ //数组
//    for (int i = 0; i < PyList_Size(result); i++) {
//        char *string  = NULL;
//        PyObject *item = PyList_GetItem(result,i);
//        PyArg_Parse(item, "s" , &string);
//        NSLog(@"string == %s",string);
//    }
//}
@end
