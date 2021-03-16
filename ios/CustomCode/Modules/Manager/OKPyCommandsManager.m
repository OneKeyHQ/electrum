//
//  OKPyCommandsManager.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/27.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKPyCommandsManager.h"

#define OKPY_METHOD_CASE(methodName)\
    else if([method isEqualToString:methodName])
#define OKPY_SIMPLE_CALL_WITH_NOARGS \
    result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "()",NULL);

@interface OKPyCommandsManager()
@property (nonatomic,assign)PyObject *pyClass;
//硬件实例
@property (nonatomic,assign)PyObject *pyHwClass;
@property (nonatomic,strong)NSArray *noTipsInterface;
@end

@implementation OKPyCommandsManager
static dispatch_once_t once;
+ (OKPyCommandsManager *)sharedInstance {
    static OKPyCommandsManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        PyGILState_STATE state = PyGILState_Ensure();
        _sharedInstance = [[OKPyCommandsManager alloc] init];
        PyObject *pModule = PyImport_ImportModule([@"electrum_gui.android.console" UTF8String]);//导入模块
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



        PyObject *pHwModule = PyImport_ImportModule([@"trezorlib.customer_ui" UTF8String]);//导入模块
        if (pHwModule == NULL) {
               PyErr_Print();
        }
        PyObject *pyHwClass = PyObject_GetAttrString(pHwModule, [@"CustomerUI" UTF8String]);//获取类
        _sharedInstance.pyHwClass = pyHwClass;
        PyObject *pyHwConstract = PyInstanceMethod_New(pyHwClass);
        PyObject* pHwIns = PyObject_CallObject(pyHwConstract, NULL);//创建实例
        if (pHwIns == NULL) {
            PyErr_Print();
        }
        _sharedInstance.pyHwInstance = pHwIns;

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
        NSString *coin = [parameter safeStringForKey:@"coin"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s,s)",[tx_hash UTF8String],[coin UTF8String]);


    }else if([method isEqualToString:kInterfaceCreate_hd_wallet]){

        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *seed = [parameter safeStringForKey:@"seed"];
        NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
        NSString *create_coin = [parameter safeStringForKey:@"create_coin"];
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        if (seed.length == 0) {
            kwargs = Py_BuildValue("{s:s,s:i,s:s}", "password", [password UTF8String], "purpose", purpose ?: 84,"create_coin",[create_coin UTF8String]);
        }else{
            kwargs = Py_BuildValue("{s:s,s:s,s:i,s:s}", "password", [password UTF8String],"seed",[seed UTF8String], "purpose", purpose ?: 84,"create_coin",[create_coin UTF8String]);
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_hd_wallet UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if([method isEqualToString:kInterfaceCreate_derived_wallet]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_derived_wallet UTF8String], "(s,s,s,i)",[name UTF8String],[password UTF8String],[coin UTF8String],purpose ?: 84);


    }else if([method isEqualToString:kInterfaceCreate_create]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:i}", "password", [password UTF8String],"coin",[coin UTF8String], "purpose", purpose ?: 49);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if([method isEqualToString:kInterfaceImport_Privkeys]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *privkeys = [parameter safeStringForKey:@"privkeys"];
        NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
        NSString *coin = [[parameter safeStringForKey:@"coin"] lowercaseString];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:i,s:s}", "password", [password UTF8String],"privkeys",[privkeys UTF8String], "purpose", purpose ?: 49,"coin",[coin UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


    }else if([method isEqualToString:kInterfaceImport_Seed]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *seed = [parameter safeStringForKey:@"seed"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:i,s:s}", "password", [password UTF8String],"seed",[seed UTF8String], "purpose", purpose ?: 49,"coin",[coin UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


    }else if([method isEqualToString:kInterfaceImport_Address]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *addresses = [parameter safeStringForKey:@"address"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:s}","name",[name UTF8String], "addresses", [addresses UTF8String],"coin",[coin UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if ([method isEqualToString:kInterfaceImport_KeyStore]){
        NSString *keystores = [parameter safeStringForKey:@"keystores"];
        NSString *keystore_password = [parameter safeStringForKey:@"keystore_password"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *password = [parameter safeStringForKey:@"password"];
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s,s:s}","keystores",[keystores UTF8String], "keystore_password", [keystore_password UTF8String],"coin",[coin UTF8String],"name",[name UTF8String],"password",[password UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceCreate_create UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


    }else if([method isEqualToString:kInterfaceDelete_wallet]){
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *hd = [parameter safeStringForKey:@"hd"];
        PyObject *args =  Py_BuildValue("(s)", [password UTF8String]);
        PyObject *kwargs;
        if (hd.length > 0) {
            kwargs = Py_BuildValue("{s:s,s:i}", "name", [name UTF8String],"hd",[hd boolValue]);
        }else{
            kwargs = Py_BuildValue("{s:s}", "name", [name UTF8String]);
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceDelete_wallet UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


    }else if([method isEqualToString:kInterfaceImport_xpub]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *master = [parameter safeStringForKey:@"master"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceCreate_create UTF8String], "(s,s)",[name UTF8String],[master UTF8String]);


    }else if([method isEqualToString:kInterfaceLoad_all_wallet]){
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "()",NULL);



    }else if([method isEqualToString:kInterface_switch_wallet]){
        NSString *selectName = [parameter safeStringForKey:@"name"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)",[selectName UTF8String]);


    }else if([method isEqualToString:kInterfaceGet_all_tx_list]){
        NSString *search_type = [parameter safeStringForKey:@"search_type"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        NSString *contract_address = [parameter safeStringForKey:@"contract_address"];
        if (coin.length == 0 || coin == nil) {
            coin = @"btc";
        }
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        if (contract_address.length > 0) {
            kwargs = Py_BuildValue("{s:s,s:s,s:s}", "coin", [coin UTF8String],"search_type",[search_type UTF8String],"contract_address",[contract_address UTF8String]);
        }else{
            if (search_type.length == 0) {
                kwargs = Py_BuildValue("{s:s}", "coin", [coin UTF8String]);
            }else{
                kwargs = Py_BuildValue("{s:s,s:s}", "coin", [coin UTF8String],"search_type",[search_type UTF8String]);
            }
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceGet_all_tx_list UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if([method isEqualToString:kInterfaceGet_default_fee_status]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceGet_default_fee_status UTF8String], "");


    }else if([method isEqualToString:kInterfaceis_watch_only]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceis_watch_only UTF8String], "");


    }else if([method isEqualToString:kInterfaceGet_fee_by_feerate]){
        NSString *coin = [parameter safeStringForKey:@"coin"];
        if (coin.length == 0 || coin == nil) {
            coin = @"btc";
        }
        NSString *outputs = [parameter safeStringForKey:@"outputs"];
        NSString *message = [parameter safeStringForKey:@"message"];
        NSString *feerate = [parameter safeStringForKey:@"feerate"];
        PyObject *args =  Py_BuildValue("(s)", [coin UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:i}", "outputs", [outputs UTF8String],"message",[message UTF8String],"feerate",[feerate longLongValue]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceGet_fee_by_feerate UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


    }else if([method isEqualToString:kInterfaceMktx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceMktx UTF8String], "(s)", [tx UTF8String]);

    }else if([method isEqualToString:kInterfaceRemove_local_tx]){
        NSString *delete_tx = [parameter safeStringForKey:@"delete_tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceRemove_local_tx UTF8String], "(s)", [delete_tx UTF8String]);


    }else if([method isEqualToString:kInterfaceSign_tx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *path = kBluetooth_iOS;
        PyObject *args =  Py_BuildValue("(s)", [tx UTF8String]);
        PyObject *kwargs;
        if (password.length > 0) {
            kwargs = Py_BuildValue("{s:s}", "password", [password UTF8String]);
        }else{
            kwargs = Py_BuildValue("{s:s}","path",[path UTF8String]);
        }
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
        NSString *name = [parameter safeStringForKey:@"name"];
        if (name.length == 0 || name == nil) {
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_backup_info UTF8String], "()");
        }else{
            result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_backup_info UTF8String], "(s)",[name UTF8String]);
        }


    }else if([method isEqualToString:kInterfacedelete_backup_info]){
        NSString *name = [parameter safeStringForKey:@"name"];
        if (name == nil || name.length == 0) {
            result = PyObject_CallMethod(self.pyInstance, [kInterfacedelete_backup_info UTF8String], "()");
        }else{
            result = PyObject_CallMethod(self.pyInstance, [kInterfacedelete_backup_info UTF8String], "(s)",[name UTF8String]);
        }


    }else if([method isEqualToString:kInterfaceexport_seed]){
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *name = [parameter safeStringForKey:@"name"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceexport_seed UTF8String], "(s,s)",[password UTF8String],[name UTF8String]);

    }else if([method isEqualToString:kInterface_get_all_wallet_balance]){
        result = PyObject_CallMethod(self.pyInstance, [kInterface_get_all_wallet_balance UTF8String], "()",NULL);

    }else if([method isEqualToString:kInterfaceget_default_fee_info]){
        NSString *feerate = [parameter safeStringForKey:@"feerate"];
        NSString *coin = [parameter safeStringForKey:@"coin"]?:@"btc";
        NSString *eth_tx_info = [parameter safeStringForKey:@"eth_tx_info"];
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        if (eth_tx_info.length != 0) {
            kwargs = Py_BuildValue("{s:s,s:s}", "coin", [coin UTF8String],"eth_tx_info",[eth_tx_info UTF8String]);
        }else{
            if (feerate.length == 0 || feerate == nil) {
                kwargs = Py_BuildValue("{s:s}", "coin", [coin UTF8String]);
            }else{
                kwargs = Py_BuildValue("{s:s,s:s}", "feerate", [feerate UTF8String],"coin",[coin UTF8String]);
            }
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceget_default_fee_info UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);


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


    }else if([method isEqualToString:kInterfacerecovery_confirmed]){
        NSArray *nameList = parameter[@"name_list"];
        NSString * name_list = [nameList mj_JSONString];
        NSString * hw = [parameter safeStringForKey:@"hw"];
        if (hw == nil || hw.length == 0) {
            result = PyObject_CallMethod(self.pyInstance, [kInterfacerecovery_confirmed UTF8String], "(s)",[name_list UTF8String]);
        }else{
            result = PyObject_CallMethod(self.pyInstance, [kInterfacerecovery_confirmed UTF8String], "(s,i)",[name_list UTF8String],[hw boolValue]);
        }

    }else if([method isEqualToString:kInterfaceget_default_server]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_default_server UTF8String], "()",NULL);


    }else if([method isEqualToString:kInterfaceset_sync_server_host]){
        NSString *ip = [parameter safeStringForKey:@"ip"];
        NSString *port = [parameter safeStringForKey:@"port"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_sync_server_host UTF8String], "(s,s)",[ip UTF8String],[port UTF8String]);

    }else if([method isEqualToString:kInterfaceget_exchanges]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_exchanges UTF8String], "()",NULL);

    }else if([method isEqualToString:kInterfaceset_exchange]){
        NSString *exchange = [parameter safeStringForKey:@"exchange"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_exchange UTF8String], "(s)",[exchange UTF8String]);

    }else if([method isEqualToString:kInterfaceget_sync_server_host]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_sync_server_host UTF8String], "()",NULL);

    }else if([method isEqualToString:kInterfaceset_syn_server]){
        BOOL flag = [[parameter safeStringForKey:@"flag"]boolValue];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_syn_server UTF8String], "(i)",flag);


    }else if([method isEqualToString:kInterfaceset_rbf]){
        BOOL status_rbf = [[parameter safeStringForKey:@"status_rbf"]boolValue];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_rbf UTF8String], "(i)",status_rbf);

    }else if([method isEqualToString:kInterfaceset_unconf]){
        BOOL x = [[parameter safeStringForKey:@"x"]boolValue];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_rbf UTF8String], "(i)",x);


    }else if([method isEqualToString:kInterfaceget_server_list]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_server_list UTF8String], "()",NULL);


    }else if([method isEqualToString:kInterfaceset_server]){
        NSString *host = [parameter safeStringForKey:@"host"];
        NSString *port = [parameter safeStringForKey:@"port"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_server UTF8String], "(s,s)",[host UTF8String],[port UTF8String]);

    }else if([method isEqualToString:kInterfaceset_proxy]){
        NSString *proxy_mode = [parameter safeStringForKey:@"proxy_mode"];
        NSString *proxy_host = [parameter safeStringForKey:@"proxy_host"];
        NSString *proxy_port = [parameter safeStringForKey:@"proxy_port"];
        NSString *proxy_user = [parameter safeStringForKey:@"proxy_user"];
        NSString *proxy_password = [parameter safeStringForKey:@"proxy_password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_proxy UTF8String], "(s,s,s,s,s)",[proxy_mode UTF8String],[proxy_host UTF8String],[proxy_port UTF8String],[proxy_user UTF8String],[proxy_password UTF8String]);


    }else if([method isEqualToString:kInterfaceparse_pr]){
        NSString *data = [parameter safeStringForKey:@"data"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceparse_pr UTF8String], "(s)",[data UTF8String]);

    }else if([method isEqualToString:kInterfacecheck_password]){
        NSString *password = [parameter safeStringForKey:@"password"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfacecheck_password UTF8String], "(s)",[password UTF8String]);

    }else if([method isEqualToString:kInterfaceverify_legality]){
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *data = [parameter safeStringForKey:@"data"];
        NSString *flag = [parameter safeStringForKey:@"flag"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        PyObject *args =  Py_BuildValue("(s)", [data UTF8String]);
        PyObject *kwargs;
        if (password.length == 0) {
            kwargs = Py_BuildValue("{s:s,s:s}", "flag", [flag UTF8String],"coin",[coin UTF8String]);
        }else{
            kwargs = Py_BuildValue("{s:s,s:s,s:s}", "flag", [flag UTF8String],"password",[password UTF8String],"coin",[coin UTF8String]);
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceverify_legality UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if([method isEqualToString:kInterfaceBroadcast_tx]){
        NSString *tx = [parameter safeStringForKey:@"tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceBroadcast_tx UTF8String], "(s)", [tx UTF8String]);

    }else if([method isEqualToString:kInterfaceget_feature]){
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_feature UTF8String], "(s)",[path UTF8String]);


    }else if([method isEqualToString:kInterfacecreate_hw_derived_wallet]){
        NSString *is_creating = [parameter safeStringForKey:@"is_creating"];
        if ([is_creating isEqualToString:@"0"]) {
            NSString *path = kBluetooth_iOS;
            PyObject *args =  Py_BuildValue("()", NULL);
            PyObject *kwargs;
            kwargs = Py_BuildValue("{s:s}", "path", [path UTF8String]);
            PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfacecreate_hw_derived_wallet UTF8String]);
            result = PyObject_Call(myobject_method, args, kwargs);
        }else{
            NSString *path = kBluetooth_iOS;
            NSInteger purpose = [[parameter objectForKey:@"purpose"] integerValue];
            NSString *type = @"p2wpkh";
            NSString *coin = [parameter safeStringForKey:@"coin"];
            if (purpose == OKBTCAddressTypeNormal) {
                type = @"p2pkh";
            } else if (purpose == OKBTCAddressTypeSegwit) {
                type = @"p2wpkh-p2sh";
            }
            PyObject *args =  Py_BuildValue("()", NULL);
            PyObject *kwargs;
            kwargs = Py_BuildValue("{s:s,s:s,s:s}", "path", [path UTF8String],"_type",[type UTF8String],"coin",[coin UTF8String]);
            PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfacecreate_hw_derived_wallet UTF8String]);
            result = PyObject_Call(myobject_method, args, kwargs);
        }

    }else if ([method isEqualToString:kInterfaceimport_create_hw_wallet]){
        NSString *name = [parameter safeStringForKey:@"name"];
        NSString *m  = [parameter safeStringForKey:@"m"];
        NSString *n = [parameter safeStringForKey:@"n"];
        NSString *xpubs = [parameter safeStringForKey:@"xpubs"];
        NSString *coin = [parameter safeStringForKey:@"coin"];
        BOOL hd = [[parameter safeStringForKey:@"hd"] boolValue];
        NSString *path = kBluetooth_iOS;
        PyObject *args =  Py_BuildValue("(s)", [name UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:i,s:i,s:s,s:s,s:i,s:s}", "m", [m integerValue],"n",[n integerValue],"path",[path UTF8String],"xpubs",[xpubs UTF8String],"hd",hd,"coin",[coin UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceimport_create_hw_wallet UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
    }else if ([method isEqualToString:kInterfaceset_pin]){
        NSString *pin = [parameter safeStringForKey:@"pin"];
        result = PyObject_CallMethod(self.pyHwInstance, [kInterfaceset_pin UTF8String], "(s)",[pin UTF8String]);
    }else if ([method isEqualToString:kInterfaceinit]){
        NSString *path = kBluetooth_iOS;
        NSString *label = [parameter safeStringForKey:@"label"];
        NSString *stronger_mnemonic = [parameter safeStringForKey:@"stronger_mnemonic"];
        PyObject *args =  Py_BuildValue("(s)", [path UTF8String]);
        PyObject *kwargs;
        NSString *language = @"english";
        if ([OKLocalizableManager getCurrentLanguage] == AppCurrentLanguage_Zh_Hans) {
            language = @"zh-CN";
        }else{
            language = @"english";
        }
        if (stronger_mnemonic.length == 0) {
            kwargs = Py_BuildValue("{s:s,s:s}", "label", [label UTF8String],"language",[language UTF8String]);
        }else{
            kwargs = Py_BuildValue("{s:s,s:i,s:s}", "label", [label UTF8String],"stronger_mnemonic",[stronger_mnemonic boolValue],"language",[language UTF8String]);
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceinit UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
    }else if ([method isEqualToString:kInterfacereset_pin]){
        NSString *path  = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfacereset_pin UTF8String], "(s)",[path UTF8String]);
    }else if([method isEqualToString:kInterfacebixin_load_device]){
        NSString *path = kBluetooth_iOS;
        NSString *mnemonics = [parameter safeStringForKey:@"mnemonics"];
        NSString *label = [parameter safeStringForKey:@"label"];
        PyObject *args =  Py_BuildValue("(s)", [path UTF8String]);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s}", "label", [label UTF8String],"mnemonics",[mnemonics UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfacebixin_load_device UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
    }else if ([method isEqualToString:kInterfacebixin_backup_device]){
        NSString *path  = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfacebixin_backup_device UTF8String], "(s)",[path UTF8String]);
    }else if ([method isEqualToString:kInterfacehardware_verify]){
        NSString *msg = [parameter safeStringForKey:@"msg"];
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfacehardware_verify UTF8String], "(s,s)",[msg UTF8String],[path UTF8String]);

    }else if ([method isEqualToString:kInterface_set_response]){
        NSString *data = [parameter safeStringForKey:@"data"];
        PyObject *b = PyObject_GetAttrString(PyImport_ImportModule("trezorlib.transport.bluetooth_ios"), "BlueToothIosHandler");
        PyObject_CallMethod(b, [kInterface_set_response UTF8String], "(s)", [data UTF8String]);
    }else if ([method isEqualToString:kInterface_set_write_success_flag]){
        PyObject *b = PyObject_GetAttrString(PyImport_ImportModule("trezorlib.transport.bluetooth_ios"), "BlueToothIosHandler");
        PyObject_CallMethod(b, [kInterface_set_write_success_flag UTF8String], "()", NULL);
    }else if ([method isEqualToString:kInterfaceshow_address]){
        NSString *address = [parameter safeStringForKey:@"address"];
        NSString *path = kBluetooth_iOS;
        NSString *coin = [parameter safeStringForKey:@"coin"];
        PyObject *args =  Py_BuildValue("()",NULL);
        PyObject *kwargs;
        kwargs = Py_BuildValue("{s:s,s:s,s:s}", "address", [address UTF8String],"path",[path UTF8String],"coin",[coin UTF8String]);
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfaceshow_address UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);
    }else if ([method isEqualToString: kInterfacefirmware_update]){
        NSString *filename = [parameter safeStringForKey:@"filename"];
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfacefirmware_update UTF8String], "(s,s)",[filename UTF8String],[path UTF8String]);
    }else if ([method isEqualToString:kInterface_wipe_device]){
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterface_wipe_device UTF8String], "(s)", [path UTF8String]);
    }else if ([method isEqualToString:kInterface_get_xpub_from_hw]){
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterface_get_xpub_from_hw UTF8String], "(s)", [path UTF8String]);
    }else if ([method isEqualToString:kInterface_set_cancel_flag]){
        PyObject *b = PyObject_GetAttrString(PyImport_ImportModule("trezorlib.transport.bluetooth_ios"), "BlueToothIosHandler");
        PyObject_CallMethod(b, [kInterface_set_cancel_flag UTF8String], "()", NULL);
    }else if ([method isEqualToString:kInterface_set_user_cancel]){
        result = PyObject_CallMethod(self.pyHwInstance, [kInterface_set_user_cancel UTF8String], "()", NULL);
    }else if ([method isEqualToString:kInterfacesign_message]){
        NSString *address = [parameter safeStringForKey:@"address"];
        NSString *message = [parameter safeStringForKey:@"message"];
        NSString *path = kBluetooth_iOS;
        result = PyObject_CallMethod(self.pyInstance, [kInterfacesign_message UTF8String], "(s,s,s)",[address UTF8String],[message UTF8String],[path UTF8String]);

    }else if ([method isEqualToString:kInterfaceverify_message]){

        NSString *address = [parameter safeStringForKey:@"address"];
        NSString *message = [parameter safeStringForKey:@"message"];
        NSString *signature = [parameter safeStringForKey:@"signature"];
        NSString *path = kBluetooth_iOS;
        PyObject *args =  Py_BuildValue("()",NULL);
        PyObject *kwargs;
        if (path.length == 0) {
            kwargs = Py_BuildValue("{s:s,s:s,s:s}", "address", [address UTF8String],"message",[message UTF8String],"signature",[signature UTF8String]);
        }else{
            kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s}", "address", [address UTF8String],"message",[message UTF8String],"signature",[signature UTF8String],"path",[path UTF8String]);
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [method UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if ([method isEqualToString:kInterfaceget_tx_info_from_raw]){
        NSString *raw_tx = [parameter safeStringForKey:@"raw_tx"];
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceget_tx_info_from_raw UTF8String], "(s)",[raw_tx UTF8String]);
    }else if ([method isEqualToString:kInterfaceset_recovery_flag]){
        result = PyObject_CallMethod(self.pyInstance, [kInterfaceset_recovery_flag UTF8String], "()",NULL);

    }else if ([method isEqualToString:kInterfacesign_eth_tx]){

        NSString *to_addr = [parameter safeStringForKey:@"to_addr"];
        NSString *value = [parameter safeStringForKey:@"value"];
        NSString *path = [parameter safeStringForKey:@"path"];
        NSString *password = [parameter safeStringForKey:@"password"];
        NSString *contract_addr = [parameter safeStringForKey:@"contract_addr"];
        NSString *gas_price = [parameter safeStringForKey:@"gas_price"];
        NSString *gas_limit = [parameter safeStringForKey:@"gas_limit"];
        PyObject *args =  Py_BuildValue("()");
        PyObject *kwargs;
        if (path.length == 0) {
            if (contract_addr.length == 0) {
                kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s,s:s}", "to_addr", [to_addr UTF8String],"value",[value UTF8String],"password",[password UTF8String],"gas_price",[gas_price UTF8String],"gas_limit",[gas_limit UTF8String]);
            }else{
                kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s,s:s,s:s}", "to_addr", [to_addr UTF8String],"value",[value UTF8String],"password",[password UTF8String],"contract_addr",[contract_addr UTF8String],"gas_price",[gas_price UTF8String],"gas_limit",[gas_limit UTF8String]);
            }
        }else{
            if (contract_addr.length == 0) {
                kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s,s:s}", "to_addr", [to_addr UTF8String],"value",[value UTF8String],"gas_price",[gas_price UTF8String],"gas_limit",[gas_limit UTF8String],"path",[kBluetooth_iOS UTF8String]);
            }else{
                kwargs = Py_BuildValue("{s:s,s:s,s:s,s:s,s:s,s:s}", "to_addr", [to_addr UTF8String],"value",[value UTF8String],"contract_addr",[contract_addr UTF8String],"gas_price",[gas_price UTF8String],"gas_limit",[gas_limit UTF8String],"path",[kBluetooth_iOS UTF8String]);
            }
        }
        PyObject *myobject_method = PyObject_GetAttrString(self.pyInstance, [kInterfacesign_eth_tx UTF8String]);
        result = PyObject_Call(myobject_method, args, kwargs);

    }else if([method isEqualToString:kInterface_get_wallet_balance]){
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "()",NULL);
    }
    OKPY_METHOD_CASE(kInterface_add_token) {
        NSString *symbol = [parameter safeStringForKey:@"symbol"];
        NSString *contract_addr = [parameter safeStringForKey:@"contract_addr"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s,s)", [symbol UTF8String], [contract_addr UTF8String]);
    }
    OKPY_METHOD_CASE(kInterface_delete_token) {
        const char *contract_addr = [parameter safeStringForKey:@"contract_addr"].UTF8String;
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)", contract_addr);
    }
    OKPY_METHOD_CASE(kInterface_get_all_token_info) { OKPY_SIMPLE_CALL_WITH_NOARGS }
    OKPY_METHOD_CASE(kInterface_get_cur_wallet_token_address) { OKPY_SIMPLE_CALL_WITH_NOARGS }
    OKPY_METHOD_CASE(kInterface_get_all_customer_token_info) { OKPY_SIMPLE_CALL_WITH_NOARGS }
    OKPY_METHOD_CASE(kInterface_get_customer_token_info) {
        const char *contract_addr = [parameter safeStringForKey:@"contract_address"].UTF8String;
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)", contract_addr);
    }else if ([method isEqualToString:kInterface_export_keystore]){
        NSString *password = [parameter safeStringForKey:@"password"];
        result = PyObject_CallMethod(self.pyInstance, [method UTF8String], "(s)", [password UTF8String]);
    }

    if (result == NULL) {
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
            if (![self.noTipsInterface containsObject:method]) {
                [kTools tipMessage:[NSString stringWithCString:msg encoding:NSUTF8StringEncoding]];
            } else {
                [kTools debugTipMessage:[NSString stringWithCString:msg encoding:NSUTF8StringEncoding]];
            }
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

- (void)cancel {
    [self callInterface:kInterface_set_cancel_flag parameter:@{}];
}

- (void)cancelPIN {
    if (kUserSettingManager.pinInputMethod == OKDevicePINInputMethodOnDevice) {
        [self callInterface:kInterface_set_user_cancel parameter:@{}];
    } else {
        [self cancel];
    }
}

- (void)asyncCall:(NSString *)method parameter:(NSDictionary *)parameter callback:(void(^)(id result))callback {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        id result = [self callInterface:method parameter:parameter];
        if (!callback) {
            return;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            callback(result);
        });
    });
}

- (void)asyncCall:(NSString *)method parameter:(NSDictionary *)parameter asyncCallback:(void(^)(id result))callback {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        id result = [self callInterface:method parameter:parameter];
        if (callback) {
            callback(result);
        }
    });
}

- (NSArray *)noTipsInterface
{
    if (!_noTipsInterface) {
        _noTipsInterface = @[
            kInterfaceSet_currency,
            kInterfaceSet_base_uint,
            kInterfaceget_tx_info_from_raw,
            kInterfaceget_default_fee_info,
            kInterface_add_token
        ];
    }
    return _noTipsInterface;;
}

@end
