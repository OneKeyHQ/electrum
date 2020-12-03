//
//  OKDeleteWalletTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKDeleteWalletTipsViewController.h"
#import "OKDeleteBackUpTipsController.h"

@interface OKDeleteWalletTipsViewController ()

@property (weak, nonatomic) IBOutlet UIButton *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *deleteBtn;
@property (weak, nonatomic) IBOutlet UIButton *iAgree;
- (IBAction)iAgreeClick:(UIButton *)sender;
- (IBAction)deleteBtnClick:(UIButton *)sender;
@end

@implementation OKDeleteWalletTipsViewController

+ (instancetype)deleteWalletTipsViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeleteWalletTipsViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Delete HD Wallet", nil);
    self.descLabel.text = MyLocalizedString(@"Once deleted: 1. All HD wallets will be erased. 2. Please make sure that the root mnemonic of HD Wallet has been copied and kept before deletion. You can use it to recover all HD Wallets and retrieve assets.", nil);
    [self.titleLabel setTitle:MyLocalizedString(@"⚠️ risk warning", nil) forState:UIControlStateNormal];
    [self.deleteBtn setTitle:MyLocalizedString(@"Delete HD Wallet", nil) forState:UIControlStateNormal];
    [self.iAgree setTitle:[NSString stringWithFormat:@" %@",MyLocalizedString(@"I am aware of the above risks", nil)] forState:UIControlStateNormal];
    [self.iAgree setImage:[UIImage imageNamed:@"notselected"] forState:UIControlStateNormal];
    [self.iAgree setImage:[UIImage imageNamed:@"isselected"] forState:UIControlStateSelected];
    [self.deleteBtn setLayerRadius:20];
    [self checkUI];
}

- (IBAction)deleteBtnClick:(UIButton *)sender {
    BOOL isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":self.walletName}] boolValue];
    if (isBackUp) {
        [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
            [kPyCommandsManager callInterface:kInterfaceDelete_wallet parameter:@{@"name":self.walletName,@"password":pwd}];
            [kWalletManager clearCurrentWalletInfo];
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiDeleteWalletComplete object:nil];
            [kTools tipMessage:MyLocalizedString(@"Wallet deleted successfully", nil)];
            [self.navigationController popToRootViewControllerAnimated:YES];
        }];
    }else{
        OKWeakSelf(self)
        OKDeleteBackUpTipsController *tipsVc = [OKDeleteBackUpTipsController deleteBackUpTipsController:^{
            [weakself.navigationController popViewControllerAnimated:YES];
        }];
        tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
        [self presentViewController:tipsVc animated:NO completion:nil];
    }
}

- (IBAction)iAgreeClick:(UIButton *)sender {
    self.iAgree.selected = !sender.isSelected;
    [self checkUI];
}
- (void)checkUI
{
    if (self.iAgree.selected) {
        self.deleteBtn.enabled = YES;
        self.deleteBtn.alpha = 1.0;
    }else{
        self.deleteBtn.enabled = NO;
        self.deleteBtn.alpha = 0.5;
    }
}
@end
