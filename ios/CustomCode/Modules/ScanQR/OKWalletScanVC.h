//
//  OneKeyImport.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"
#import "OKQRCodeScanManager.h"

#define MnenomicPrefix @"OneKeyMnemonic:"

@class OKWalletScanVC;
typedef void(^ScanningCompleteBlock)(OKWalletScanVC *vc, id result);

typedef NS_ENUM(NSInteger, ScanningType) {
    ScanningTypeAddress = 0,   //导入地址
    ScanningTypeImportMninomic, //导入助记词
    ScanningTypeImportPrivateKey, //导入私钥
    ScanningTypeImportKeyStore,  //导入keyStore
    ScanningTypeImportObserver,   //导入观察者钱包
    ScanningTypeAll,
    ScanningTypeAddressInplace,
};

typedef NS_ENUM(NSInteger, OKQRParseType) {
    OKQRParseTypeAddress = 1,
    OKQRParseTypeTx = 2,
    OKQRParseTypeOthers = 3,
};

@interface OKWalletScanVC : BaseViewController
@property (nonatomic, strong) NSArray *wordsArray;
@property (nonatomic, strong) UIViewController *popToVC;
@property (nonatomic, copy) NSString *password;
@property (assign, nonatomic) BOOL isReturnHome;
@property (nonatomic, strong) OKQRCodeScanManager *scanManager;

@property (nonatomic) ScanningType scanningType;
@property (nonatomic, copy) ScanningCompleteBlock scanningCompleteBlock;

- (void)authorizePushOn:(UIViewController *)viewController;

@end
