//
//  OneKeyImport.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

#define MnenomicPrefix @"OneKeyMnemonic:"

typedef void(^ScanningCompleteBlock)(id result);

typedef NS_ENUM(NSInteger, ScanningType) {
    ScanningTypeAddress = 0,   //导入地址
    ScanningTypeImportMninomic, //导入助记词
    ScanningTypeImportPrivateKey, //导入私钥
    ScanningTypeImportKeyStore,  //导入keyStore
    ScanningTypeImportObserver,   //导入观察者钱包
    ScanningTypeAll
};

@interface OKWalletScanVC : BaseViewController
@property (nonatomic, strong) NSArray *wordsArray;
@property (nonatomic, strong) UIViewController *popToVC;
@property (nonatomic, copy) NSString *password;
@property (assign, nonatomic) BOOL isReturnHome;

@property (nonatomic) ScanningType scanningType;
@property (nonatomic, copy) ScanningCompleteBlock scanningCompleteBlock;

- (void)authorizePushOn:(UIViewController *)viewController;

@end
