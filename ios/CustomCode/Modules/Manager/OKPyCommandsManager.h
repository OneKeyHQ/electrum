//
//  OKPyCommandsManager.h
//  OneKey
//
//  Created by bixin on 2020/10/27.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

#define kInterfaceLoad_wallet            @"load_wallet" //加载并且选择钱包
#define kInterfaceSelect_wallet          @"select_wallet" //选择钱包
#define kInterfaceGet_tx_info            @"get_tx_info" //获取交易详情
#define kInterfaceGet_all_tx_list        @"get_all_tx_list" //获取交易记录
#define kInterfaceGet_default_fee_status @"get_default_fee_status" //获取默认费率
#define kInterfaceGet_fee_by_feerate     @"get_fee_by_feerate"  //输入地址和转账额度获取fee
#define kInterfaceMktx                   @"mktx" //创建交易
#define kInterfaceSign_tx                @"sign_tx" //签名交易
#define kInterfaceBroadcast_tx           @"broadcast_tx" //广播交易


#define kPyCommandsManager (OKPyCommandsManager.sharedInstance)
NS_ASSUME_NONNULL_BEGIN
@interface OKPyCommandsManager : NSObject
+ (OKPyCommandsManager *)sharedInstance;
- (NSDictionary *)callInterface:(NSString *)method parameter:(NSDictionary *)parameter;
@property (nonatomic,assign)PyObject *pyInstance;
@end

NS_ASSUME_NONNULL_END
