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
    self.title = @"hardwareWallet.pin.title".localized;
    self.view.backgroundColor = [UIColor whiteColor];
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(back)];
}

- (void)changePIN {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result = [kPyCommandsManager callInterface:kInterfacereset_pin parameter:@{}];
        if ([result boolValue]) {
            [kTools tipMessage:@"hardwareWallet.pin.success".localized];
        } else {
            [kTools tipMessage:@"hardwareWallet.pin.fail".localized];
        }
        NSLog(@"112233 changePIN: %d", [result boolValue]);
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself back];
        });
    });

}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
        if (type == OKHWNotiTypePin_Current) {
            OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                NSLog(@"pinCode = %@",pin);
                if (kUserSettingManager.pinInputMethod == OKDevicePINInputMethodOnDevice) {
                    [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    return;
                }
                OKPINCodeViewController *pinCode2 = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin2) {
                    dispatch_async(dispatch_get_global_queue(0, 0), ^{
                        NSString *oldPin = [[pin stringByAppendingString:@"000000000"] substringToIndex:9];
                        NSString *newPin = [[pin2 stringByAppendingString:@"000000000"] substringToIndex:9];
                        NSString *pincom = [oldPin stringByAppendingString:newPin];
                        [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin": pincom}];
                        weakself.changed = YES;
                    });
                }];
                pinCode2.titleLabelText = @"hardwareWallet.pin.newPinTip".localized;
                [weakself.navigationController pushViewController:pinCode2 animated:YES];
            }];
            pinCode.titleLabelText = @"hardwareWallet.pin.inputPinTip".localized;
            pinCode.backToPreviousCallback = ^{
                [kPyCommandsManager cancelPIN];
            };
            [weakself.navigationController pushViewController:pinCode animated:YES];
        } else if (type == OKHWNotiTypeKeyConfirm) {
            OKDeviceConfirmController *confirmVC = [OKDeviceConfirmController controllerWithStoryboard];
            if (weakself.changed) {
                confirmVC.titleText = @"hardwareWallet.pin.comfirm".localized;
                confirmVC.descText = @"hardwareWallet.pin.comfirmTip".localized;
            } else {
                confirmVC.titleText = @"Verify on the equipment".localized;
            }
            confirmVC.title =  @"hardwareWallet.pin.title".localized;
            confirmVC.titleText = @"Verify on the equipment".localized;
            confirmVC.btnText = @"return".localized;
            confirmVC.btnCallback = ^{
                [kPyCommandsManager cancel];
            };
            confirmVC.backToPreviousCallback = ^{
                [kPyCommandsManager cancel];
            };
            [weakself.navigationController pushViewController:confirmVC animated:YES];
        } else if (type == OKHWNotiTypePin_New_First){
            dispatch_async(dispatch_get_main_queue(), ^{
                OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                    NSLog(@"pinCode = %@",pin);
                    dispatch_async(dispatch_get_global_queue(0, 0), ^{
                        [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    });
                }];
               [weakself.navigationController pushViewController:pinCode animated:YES];
            });
        }
    });
}

- (void)back {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
