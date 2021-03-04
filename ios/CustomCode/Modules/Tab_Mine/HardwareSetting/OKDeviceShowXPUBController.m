//
//  OKDeviceShowXPUBController.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/24.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceShowXPUBController.h"
#import "OKPINCodeViewController.h"

@interface OKDeviceShowXPUBController () <OKHwNotiManagerDelegate>
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIView *view0;
@property (weak, nonatomic) IBOutlet UILabel *xpubTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *xpubLabel;
@property (weak, nonatomic) IBOutlet UIView *xpubView;
@property (strong, nonatomic) NSString *xpub;

@end

@implementation OKDeviceShowXPUBController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceShowXPUBController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [OKHwNotiManager sharedInstance].delegate = self;
    self.title = @"hardwareWallet.xpub.title".localized;
    [self.view0 setLayerRadius:20];
    [self.xpubView setLayerRadius:30];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(copyXPUB)];
    [self.xpubView addGestureRecognizer:tap];
    self.descLabel.text = @"hardwareWallet.xpub.tip".localized;
    self.xpubTitleLabel.text = @"hardwareWallet.xpub.xpub".localized;
    [self getXPUB];
}

- (void)copyXPUB {
    if (self.xpub) {
        [UIPasteboard generalPasteboard].string = self.xpub;
        [kTools tipMessage:@"hardwareWallet.xpub.copy".localized];
    }
}

- (void)getXPUB {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSString *result = [kPyCommandsManager callInterface:kInterface_get_xpub_from_hw parameter:@{}];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[self presentedViewController] dismissViewControllerAnimated:YES completion:nil];
            if (result) {
                weakself.xpub = result;
            } else {
                [weakself.navigationController popViewControllerAnimated:YES];
            }
        });
    });
}

- (void)setXpub:(NSString *)xpub {
    _xpub = xpub;
    NSMutableAttributedString *xpubText = [NSString lineSpacing:10 content:xpub];
    NSTextAttachment *attchment = [[NSTextAttachment alloc] init];
    attchment.bounds = CGRectMake(10, 0, 16, 16);
    attchment.image = [UIImage imageNamed:@"copy_medium"];
    NSAttributedString *attchmentStr = [NSAttributedString attributedStringWithAttachment:attchment];
    [xpubText appendAttributedString:attchmentStr];
    self.xpubLabel.attributedText = xpubText;
}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
        if (type == OKHWNotiTypePin_Current) {
            OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                NSLog(@"pinCode = %@",pin);
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                });
            }];
            BaseNavigationController *nav = [[BaseNavigationController alloc] initWithRootViewController:pinCode];
            [pinCode setNavigationBarBackgroundColorWithClearColor];
            pinCode.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:weakself selector:@selector(cancel)];
            [weakself presentViewController:nav animated:YES completion:nil];
        }
    });
}

- (void)cancel {
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [kPyCommandsManager cancelPIN];
    });
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
