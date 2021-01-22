//
//  OKSetPINPreViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSetPINPreViewController.h"
#import "OKDeviceSuccessViewController.h"
#import "OKPINCodeViewController.h"

@interface OKSetPINPreViewController ()<OKHwNotiManagerDelegate>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;
@end

@implementation OKSetPINPreViewController
+ (instancetype)setPINPreViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSetPINPreViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Activate hardware wallet", nil);
    self.titleLabel.text = MyLocalizedString(@"Set the PIN", nil);
    self.descLabel.text = MyLocalizedString(@"Each use requires a PIN code to gain access to the hardware wallet", nil);
    self.iconImageView.image = [UIImage imageNamed:@"device_confirm"];
    [self.nextBtn setLayerDefaultRadius];
}


- (IBAction)nextBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    kHwNotiManager.delegate = self;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result =  [kPyCommandsManager callInterface:kInterfacereset_pin parameter:@{}];
        if (result != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                OKDeviceSuccessViewController *devicevc = [OKDeviceSuccessViewController deviceSuccessViewController:OKDeviceSuccessActivate deviceName:self.deviceName];
                [weakself.navigationController pushViewController:devicevc animated:YES];
            });
        }
    });
}

#pragma mark - OKHwNotiManagerDekegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (OKHWNotiTypePin_New_First == type) {
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
}
@end
