//
//  OKDeviceChangePINController.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/24.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceChangePINController.h"
#import "OKPINCodeViewController.h"
#import "OKDeviceConfirmController.h"

@interface OKDeviceChangePINController () <OKHwNotiManagerDelegate>
@property (nonatomic, assign) BOOL changed;
@end

@implementation OKDeviceChangePINController


- (void)viewDidLoad {
    [super viewDidLoad];
    [OKHwNotiManager sharedInstance].delegate = self;
    [self setupUI];
    [self changePIN];
}

- (void)setupUI {
    self.title = MyLocalizedString(@"hardwareWallet.pin.title", nil);
    self.view.backgroundColor = [UIColor whiteColor];
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(back)];
}

- (void)changePIN {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result = [kPyCommandsManager callInterface:kInterfacereset_pin parameter:@{}];
        if ([result boolValue]) {
            [kTools tipMessage:MyLocalizedString(@"hardwareWallet.pin.success", nil)];
        } else {
            [kTools tipMessage:MyLocalizedString(@"hardwareWallet.pin.fail", nil)];
        }
        NSLog(@"112233 changePIN: %d", [result boolValue]);
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself back];
        });
    });

}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type {
    OKWeakSelf(self)
    NSLog(@"112233 hwNotiManagerDekegate %ld",(long)type);
    dispatch_async(dispatch_get_main_queue(), ^{
        if (type == OKHWNotiTypePin_Current) {
            OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                NSLog(@"pinCode = %@",pin);
                OKPINCodeViewController *pinCode2 = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin2) {
                    dispatch_async(dispatch_get_global_queue(0, 0), ^{
                        [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":[pin stringByAppendingString:pin2]}];
                        weakself.changed = YES;
                    });
                }];
                pinCode2.titleLabelText = MyLocalizedString(@"hardwareWallet.pin.newPinTip", nil);
                [weakself.navigationController pushViewController:pinCode2 animated:YES];
            }];
            pinCode.titleLabelText = MyLocalizedString(@"hardwareWallet.pin.inputPinTip", nil);
            [weakself.navigationController pushViewController:pinCode animated:YES];
        } else if (type == OKHWNotiTypeKeyConfirm) {
            OKDeviceConfirmController *confirmVC = [OKDeviceConfirmController controllerWithStoryboard];
            if (weakself.changed) {
                confirmVC.titleText = MyLocalizedString(@"hardwareWallet.pin.comfirm", nil);
                confirmVC.descText = MyLocalizedString(@"hardwareWallet.pin.comfirmTip", nil);
            } else {
                confirmVC.titleText = MyLocalizedString(@"Verify on the equipment", nil);
            }
            confirmVC.title =  MyLocalizedString(@"hardwareWallet.pin.title", nil);
            confirmVC.titleText = MyLocalizedString(@"Verify on the equipment", nil);
            confirmVC.btnText = MyLocalizedString(@"return", nil);
            confirmVC.btnCallback = ^{
                [weakself.navigationController dismissViewControllerAnimated:YES completion:nil];
            };
            [weakself.navigationController pushViewController:confirmVC animated:YES];
        } else {
            NSLog(@"");
        }
    });
}

- (void)back {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
