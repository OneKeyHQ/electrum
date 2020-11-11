//
//  OKWalletDetailModel.h
//  OneKey
//
//  Created by bixin on 2020/10/29.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//


//"Export mnemonic" = "导出助记词";
//"Export the private key" = "导出私钥";
//"Export the Keystore" = "导出 Keystore";
//"Independent wallet" = "独立钱包";

typedef enum {
    OKClickTypeExportDefault,             //默认不响应
    OKClickTypeExportMnemonic,            //导出助记词
    OKClickTypeExportPrivatekey,         //导出私钥
    OKClickTypeExportKeystore,           //导出KeyStore
    OKClickTypeDeleteWallet             //删除钱包
}OKClickType;


#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletDetailModel : NSObject
@property (nonatomic,copy) NSString* titleStr;
@property (nonatomic,copy) NSString* rightLabelStr;
@property (nonatomic,assign) BOOL isShowCopy;
@property (nonatomic,assign) BOOL isShowSerialNumber;
@property (nonatomic,assign) BOOL isShowDesc;
@property (nonatomic,strong)UIColor *rightLabelColor;
@property (nonatomic,strong)UIColor *leftLabelColor;
@property (nonatomic,assign) BOOL isShowArrow;
@property (nonatomic,assign) OKClickType clickType;

@end

NS_ASSUME_NONNULL_END
