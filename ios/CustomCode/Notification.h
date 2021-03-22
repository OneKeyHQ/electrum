//
//  Notification.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#ifndef Notification_h
#define Notification_h

//创建钱包完成
#define kNotiWalletCreateComplete               @"kNotiWalletCreateComplete"
//选择一个钱包
#define kNotiSelectWalletComplete               @"kNotiSelectWalletComplete"
//刷新钱包列表
#define kNotiRefreshWalletList                  @"kNotiRefreshWalletList"
//更新密码完成
#define kNotiUpdatePassWordComplete             @"kNotiUpdatePassWordComplete"

//发送交易完成
#define kNotiSendTxComplete                     @"kNotiSendTxComplete"

//删除钱包
#define kNotiDeleteWalletComplete               @"kNotiDeleteWalletComplete"
//备份钱包完成
#define kNotiBackUPWalletComplete               @"kNotiBackUPWalletComplete"
//需要调用首页切换接口刷新数据
#define kNotiSwitchWalletNeed                   @"kNotiSwitchWalletNeed"

#define kNotiUpdate_status                      @"kNotiUpdate_status"

#define kNotiSelectFiatComplete                 @"kNotiSelectFiatComplete"

//硬件信息更新
#define kNotiHwInfoUpdate                       @"kNotiHwInfoUpdate"
#define kNotiHwBroadcastiComplete               @"kNotiHwBroadcastiComplete"


#define kUserSetingSysServerComplete            @"kUserSetingSysServerComplete"
#define kUserSetingBtcBComplete                 @"kUserSetingBtcBComplete"
#define kUserSetingEthBComplete                 @"kUserSetingEthBComplete"
#define kUserSetingMarketSource                 @"kUserSetingMarketSource"
#define kUserSetingElectrumServer               @"kUserSetingElectrumServer"

//设备激活成功
#define kActiveSuccess @"VerifyOnTheDeviceActiveSuccess"



#endif /* Notification_h */
