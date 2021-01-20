//
//  OKHWCompleteCopyViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/18.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKHWCompleteCopyViewController.h"
#import "OKSetPINPreViewController.h"

@interface OKHWCompleteCopyViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIView *leftBgView;
@property (weak, nonatomic) IBOutlet UILabel *bottomDescLabel;
@property (weak, nonatomic) IBOutlet UIButton *comfirmBtn;
- (IBAction)comfirmBtnClick:(UIButton *)sender;

@end

@implementation OKHWCompleteCopyViewController
+ (instancetype)hwCompleteCopyViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKHWCompleteCopyViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
    [self activateInterface];
}

- (void)activateInterface
{
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result =  [kPyCommandsManager callInterface:kInterfaceinit parameter:@{@"label":self.deviceName}];
        if (result != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                weakself.comfirmBtn.alpha = 1.0;
                weakself.comfirmBtn.enabled = YES;
            });
        }
    });
}

- (void)stupUI
{
    switch (_type) {
        case OKMnemonicLengthType12:
        {
            self.descLabel.text = MyLocalizedString(@"Please copy your 12 - digit mnemonic words", nil);
        }
            break;
        case OKMnemonicLengthType24:
        {
            self.descLabel.text = MyLocalizedString(@"Please copy your 24 - digit mnemonic words", nil);
        }
        default:
            break;
    }
    self.title = MyLocalizedString(@"Activate the hardware wallet", nil);
    self.bottomDescLabel.text = MyLocalizedString(@"1. Copy carefully with paper and pen, and keep it properly after reconfirming. 2. OneKey does not store private data for you, and mnemonic words once lost cannot be retrieved", nil);
    [self.comfirmBtn setTitle:MyLocalizedString(@"I copied", nil) forState:UIControlStateNormal];
    self.comfirmBtn.alpha = 0.5;
    self.comfirmBtn.enabled = NO;
    [self.comfirmBtn setLayerRadius:20];
}

- (IBAction)comfirmBtnClick:(UIButton *)sender {
    OKSetPINPreViewController *setPinVc = [OKSetPINPreViewController setPINPreViewController];
    setPinVc.deviceName = self.deviceName;
    [self.navigationController pushViewController:setPinVc animated:YES];
}
@end
