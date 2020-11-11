//
//  OKSetWalletNameViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKSetWalletNameViewController.h"


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
    OKWeakSelf(self)
    [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
        switch (weakself.addType) {
            case OKAddTypeCreateHDDerived:  //创建基于HD的派生钱包
            {
                //创建派生钱包
                [kPyCommandsManager callInterface:kInterfaceCreate_derived_wallet parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd,@"coin":[self.coinType lowercaseString]}];
            }
                break;
            case OKAddTypeCreateSolo:  //创建独立钱包
            {
                [kPyCommandsManager callInterface:kInterfaceCreate_create parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd}];
            }
                break;
            case OKAddTypeImportPrivkeys:  //创建私钥
            {
                [kPyCommandsManager callInterface:kInterfaceImport_Privkeys parameter:@{@"name":self.walletNameTextfield.text,@"password":pwd,@"privkeys":self.privkeys}];
            }
                break;
            case OKAddTypeImportAddresses:  //创建观察钱包
            {
                [kPyCommandsManager callInterface:kInterfaceImport_Address parameter:@{@"name":self.walletNameTextfield.text,@"address":self.address}];
            }
                break;
            default:
                break;
        }
        
        [[NSNotificationCenter defaultCenter]postNotification:[NSNotification notificationWithName:kNotiRefreshWalletList object:nil]];
        [self.navigationController popToRootViewControllerAnimated:YES];
    }];
}
@end
