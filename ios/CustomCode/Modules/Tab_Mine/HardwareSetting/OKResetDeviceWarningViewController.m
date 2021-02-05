//
//  OKResetDeviceWarningViewController.m
//  OneKey
//
//  Created by liuzj on 09/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKResetDeviceWarningViewController.h"
#import "OKPINCodeViewController.h"
#import "OKDeviceConfirmController.h"

@interface OKResetDeviceWarningViewController () <OKHwNotiManagerDelegate, UINavigationControllerDelegate>
@property (weak, nonatomic) IBOutlet UILabel *warningText;
@property (weak, nonatomic) IBOutlet UIButton *warning;
@property (weak, nonatomic) IBOutlet UIButton *readButton;
@property (weak, nonatomic) IBOutlet UIButton *confirmButton;
@end

@implementation OKResetDeviceWarningViewController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKResetDeviceWarningViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [OKHwNotiManager sharedInstance].delegate = self;
    self.title = MyLocalizedString(@"hardwareWallet.recover.title", nil);
    self.warningText.text = MyLocalizedString(@"hardwareWallet.recover.tip", nil);
    self.warning.titleLabel.text = MyLocalizedString(@"⚠️ risk warning", nil);
    self.readButton.titleLabel.text = MyLocalizedString(@"I am aware of the above risk", nil);
    [self.confirmButton setTitle:MyLocalizedString(@"hardwareWallet.recover.title", nil) forState:UIControlStateNormal];
    [self.confirmButton setLayerRadius:20];
    self.confirmButton.alpha = 0.5;
    self.confirmButton.enabled = NO;


}

- (IBAction)agree:(UIButton *)sender {
    self.readButton.selected = !sender.isSelected;
    if (self.readButton.selected) {
        self.confirmButton.enabled = YES;
        self.confirmButton.alpha = 1.0;
    } else {
        self.confirmButton.enabled = NO;
        self.confirmButton.alpha = 0.5;
    }
}

- (IBAction)confirm:(id)sender {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result = [kPyCommandsManager callInterface:kInterface_wipe_device parameter:@{}];
        if ([result boolValue]) {
            [kTools tipMessage:[MyLocalizedString(@"hardwareWallet.recover.title", nil) stringByAppendingString:MyLocalizedString(@"success", nil)]] ;
        } else {
            [kTools tipMessage:[MyLocalizedString(@"hardwareWallet.recover.title", nil) stringByAppendingString:MyLocalizedString(@"fail", nil)]] ;
        }
        [kOKBlueManager disconnectAllPeripherals];
        [weakself dismissSelf];
    });
}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type {
    OKWeakSelf(self)
    NSLog(@"112233 hwNotiManagerDekegate %ld",(long)type);
    dispatch_async(dispatch_get_main_queue(), ^{
        if (type == OKHWNotiTypePin_Current) {
            OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                NSLog(@"pinCode = %@",pin);
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                });
            }];
            pinCode.backToPreviousCallback = ^{
                [kPyCommandsManager cancelPIN];
            };
            pinCode.forbidInteractivePopGestureRecognizer = YES;
            [weakself.navigationController pushViewController: pinCode animated:YES];
        } else if (type == OKHWNotiTypeFactoryReset) {
            OKDeviceConfirmController *confirmVC = [OKDeviceConfirmController controllerWithStoryboard];
            confirmVC.title = MyLocalizedString(@"hardwareWallet.recover.title", nil);
            confirmVC.titleText = MyLocalizedString(@"hardwareWallet.pin.comfirm", nil);
            confirmVC.btnText = MyLocalizedString(@"return", nil);
            confirmVC.forbidInteractivePopGestureRecognizer = YES;
            confirmVC.btnCallback = ^{
                [kPyCommandsManager cancel];
            };
            confirmVC.backToPreviousCallback = ^{
                [kPyCommandsManager cancel];
            };
            [weakself.navigationController pushViewController: confirmVC animated:YES];
        } else {
            NSLog(@"!!ERR");
        }
    });
}

- (void)dismissSelf {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUInteger index = [[self.navigationController viewControllers] indexOfObject:self];
        if (index <= 1) {
            return;
        }
        UIViewController *parentVC = [[self.navigationController viewControllers] objectAtIndex:index - 1];
        [self.navigationController popToViewController:parentVC animated:YES];
    });
}


@end
