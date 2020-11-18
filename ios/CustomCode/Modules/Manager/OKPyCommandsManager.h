//
//  OKPyCommandsManager.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/27.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>
#import <Python.h>

#define kInterfaceCreate_hd_wallet       @"create_hd_wallet" //创建HD钱包 或者恢复HD钱包
#define kInterfaceCreate_derived_wallet  @"create_derived_wallet" //创建派生钱包
#define kInterfaceCreate_create          @"create"              //创建独立钱包

#define kInterfaceDelete_wallet          @"delete_wallet"   //删除钱包



#define kInterfaceImport_Privkeys          @"Import_create_privkeys"     // 私钥导入钱包
#define kInterfaceImport_Seed              @"Import_create_seed"         // seed导入钱包
#define kInterfaceImport_Address           @"Import_create_addresses"    // addresses导入钱包
#define kInterfaceImport_xpub              @"Import_create_xpub"         // xpub导入钱包

#define kInterfaceLoad_all_wallet        @"load_all_wallet" //加载并且选择钱包
#define kInterfaceList_wallets           @"list_wallets" //加载钱包列表


#define kInterfaceSelect_wallet          @"select_wallet" //选择钱包
#define kInterfaceGet_tx_info            @"get_tx_info" //获取交易详情
#define kInterfaceGet_all_tx_list        @"get_all_tx_list" //获取交易记录
#define kInterfaceGet_default_fee_status @"get_default_fee_status" //获取默认费率
#define kInterfaceGet_fee_by_feerate     @"get_fee_by_feerate"  //输入地址和转账额度获取fee
#define kInterfaceMktx                   @"mktx" //创建交易
#define kInterfaceSign_tx                @"sign_tx" //签名交易
#define kInterfaceBroadcast_tx           @"broadcast_tx" //广播交易

#define kInterfaceget_wallet_address_show_UI  @"get_wallet_address_show_UI" //获取收款

#define kInterfaceGet_currencies         @"get_currencies"  //获取法币列表
#define kInterfaceSet_currency           @"set_currency" //设置法币
#define kInterfaceSet_base_uint          @"set_base_uint" //设置比特币单位

#define kInterfaceRemove_local_tx        @"remove_local_tx" //删除交易
#define kInterfaceUpdate_password        @"update_wallet_password" //修改密码


#define kInterfaceget_all_mnemonic       @"get_all_mnemonic" //获取2048个助记词

#define kInterfaceget_backup_info        @"get_backup_info"  //获取是否备份的信息
#define kInterfacedelete_backup_info     @"delete_backup_info"  //删除备份记录
#define kInterfaceexport_seed            @"export_seed"   //导出助记词


#define kInterfaceget_all_wallet_balance @"get_all_wallet_balance" //获取全部资产
#define kInterfacerename_wallet          @"rename_wallet"  //修改名称
#define kInterfaceexport_privkey         @"export_privkey" //导出私钥

#define kPyCommandsManager (OKPyCommandsManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN
@interface OKPyCommandsManager : NSObject
+ (OKPyCommandsManager *)sharedInstance;
+ (void)setNetwork;
- (id)callInterface:(NSString *)method parameter:(NSDictionary *)parameter;
@property (nonatomic,assign)PyObject *pyInstance;
@end

NS_ASSUME_NONNULL_END
