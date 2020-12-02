//
//  OKSetWalletNameViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSetWalletNameViewController.h"
#import "OKPwdViewController.h"


@interface OKSetWalletNameViewController ()
@property (weak, nonatomic) IBOutlet UILabel *seWalletNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UITextField *walletNameTextfield;
@property (weak, nonatomic) IBOutlet UIButton *createBtn;
- (IBAction)createBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *nameBgView;

@end

@implementation OKSetWalletNameViewController

+ (instancetype)setWalletNameViewController
{
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSetWalletNameViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Create a new wallet", nil);
    self.seWalletNameLabel.text = MyLocalizedString(@"Set the wallet name", nil);
    self.descLabel.text = MyLocalizedString(@"Easy for you to identify", nil);
    [self.nameBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.createBtn setLayerDefaultRadius];
    [self.walletNameTextfield becomeFirstResponder];
}

- (IBAction)createBtnClick:(UIButton *)sender {
    if (self.walletNameTextfield.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The wallet name cannot be empty", nil)];
        return;
    }
    
    if (![kWalletManager checkWalletName:self.walletNameTextfield.text]) {
        [kTools tipMessage:MyLocalizedString(@"Wallet names cannot exceed 15 characters", nil)];
        return;
    }
    
    OKWeakSelf(self)
    if (self.addType == OKAddTypeImportAddresses) {
        NSString *result =  [kPyCommandsManager callInterface:kInterfaceImport_Address parameter:@{@"name":self.walletNameTextfield.text,@"address":self.address}];
        if (result != nil) {
            [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
        }
        [[NSNotificationCenter defaultCenter]postNotification:[NSNotification notificationWithName:kNotiRefreshWalletList object:nil]];
        [self.navigationController popToRootViewControllerAnimated:YES];
        return;
    }
    if ([kWalletManager checkIsHavePwd]) {
        [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
            [weakself importWallet:pwd];
        }];
    }else{
        OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
            [weakself importWallet:pwd];
        }];
        BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
        [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
    }
}
- (void)importWallet:(NSString *)pwd
{
    OKWeakSelf(self)
    id result = nil;
    switch (weakself.addType) {
        case OKAddTypeCreateHDDerived:
        {
            result = [kPyCommandsManager callInterface:kInterfaceCreate_derived_wallet parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd,@"coin":[self.coinType lowercaseString]}];
        }
            break;
        case OKAddTypeCreateSolo:
        {
            result = [kPyCommandsManager callInterface:kInterfaceCreate_create parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd}];
        }
            break;
        case OKAddTypeImportPrivkeys:
        {
            result = [kPyCommandsManager callInterface:kInterfaceImport_Privkeys parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd,@"privkeys":self.privkeys}];
        }
            break;
        case OKAddTypeImportSeed:
        {
            result =  [kPyCommandsManager callInterface:kInterfaceImport_Seed parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd,@"seed":self.seed}];
        }
            break;
            
        default:
            break;
    }
    if (result != nil) {
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:nil];
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiRefreshWalletList object:nil];
        switch (weakself.addType) {
            case OKAddTypeCreateHDDerived:
            case OKAddTypeCreateSolo:
                [kTools tipMessage:MyLocalizedString(@"Creating successful", nil)];
                break;
            case OKAddTypeImportPrivkeys:
            case OKAddTypeImportSeed:
                [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
                break;
            default:
                break;
        }
        [weakself.OK_TopViewController dismissToViewControllerWithClassName:@"OKSetWalletNameViewController" animated:NO complete:^{
            
        }];
        [weakself.navigationController popToRootViewControllerAnimated:YES];
    }
}
@end
