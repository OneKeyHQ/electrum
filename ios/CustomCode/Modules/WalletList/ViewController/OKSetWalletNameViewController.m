//
//  OKSetWalletNameViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSetWalletNameViewController.h"
#import "OKPwdViewController.h"
#import "OKHDWalletViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKBiologicalViewController.h"
#import "OKPINCodeViewController.h"
#import "OKMatchingInCirclesViewController.h"


@interface OKSetWalletNameViewController ()<UITextFieldDelegate,OKHwNotiManagerDelegate>
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
    self.walletNameTextfield.delegate = self;
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
    [self.view endEditing:YES];
    OKWeakSelf(self)
    if (self.addType == OKAddTypeImportAddresses) {
        NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceImport_Address parameter:@{@"name":self.walletNameTextfield.text,@"address":self.address,@"coin":[self.coinType lowercaseString]}];
        OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
        if (createResultModel != nil) {
            [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
            OKCreateResultWalletInfoModel *walletInfoModel = [createResultModel.wallet_info firstObject];
            OKWalletInfoModel *model = [kWalletManager getCurrentWalletAddress:walletInfoModel.name];
            [kWalletManager setCurrentWalletInfo:model];
            if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
                [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
            }
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiRefreshWalletList object:nil];
            [[NSNotificationCenter defaultCenter]postNotification:[NSNotification notificationWithName:kNotiRefreshWalletList object:nil]];
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:nil];

            [weakself.OK_TopViewController dismissToViewControllerWithClassName:@"OKSetWalletNameViewController" animated:NO complete:^{

            }];
            [weakself.navigationController popToRootViewControllerAnimated:YES];
        }
        return;
    }
    if (self.addType == OKAddTypeCreateHWDerived) {
        [self importWallet:nil isInit:NO];
        return;
    }
    if ([kWalletManager checkIsHavePwd]) {
        if (kWalletManager.isOpenAuthBiological) {
           [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
               if (state == YZAuthIDStateNotSupport
                   || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                   [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                       [weakself importWallet:pwd isInit:NO];
                   }];
               } else if (state == YZAuthIDStateSuccess) {
                   NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                   [weakself importWallet:pwd isInit:NO];
               }
           }];
       }else{
           [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                [weakself importWallet:pwd isInit:NO];
            }];
       }
    }else{
        OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
            [weakself importWallet:pwd isInit:YES];
        }];
        BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
        [weakself.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
    }
}
- (void)importWallet:(NSString *)pwd isInit:(BOOL)isInit
{
    OKWeakSelf(self)
    __block NSDictionary* create = nil;

    NSMutableDictionary *params = [@{
        @"name": self.walletNameTextfield.text ?: @"",
        @"password": pwd ?: @"",
        @"coin": [self.coinType lowercaseString] ?: @"btc",
        @"purpose": @(self.btcAddressType)
    } mutableCopy];
    switch (weakself.addType) {
        case OKAddTypeCreateHDDerived:
        {
            create = [kPyCommandsManager callInterface:kInterfaceCreate_derived_wallet parameter:params];
            [weakself createComplete:create isInit:isInit pwd:pwd isHw:NO];
        }
            break;
        case OKAddTypeCreateSolo:
        {
            create = [kPyCommandsManager callInterface:kInterfaceCreate_create parameter:params];
            [weakself createComplete:create isInit:isInit pwd:pwd isHw:NO];
        }
            break;
        case OKAddTypeImportPrivkeys:
        {
            [params addEntriesFromDictionary:@{
                @"privkeys":self.privkeys
            }];
            create = [kPyCommandsManager callInterface:kInterfaceImport_Privkeys parameter:params];
            [weakself createComplete:create isInit:isInit pwd:pwd isHw:NO];
        }
            break;
        case OKAddTypeImportSeed:
        {
            [params addEntriesFromDictionary:@{
                @"seed":self.seed
            }];
            create =  [kPyCommandsManager callInterface:kInterfaceImport_Seed parameter:params];
            [weakself createComplete:create isInit:isInit pwd:pwd isHw:NO];
        }
            break;
        case OKAddTypeCreateHWDerived:
        {
            __block NSString *name = self.walletNameTextfield.text;
            [OKHwNotiManager  sharedInstance].delegate = self;
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            dispatch_async(dispatch_get_global_queue(0, 0), ^{
               NSString *xpub = [kPyCommandsManager callInterface:kInterfacecreate_hw_derived_wallet parameter:@{@"purpose": @(self.btcAddressType)}];
                if (xpub == nil) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{
                            for (int i = 0; i < weakself.navigationController.viewControllers.count; i++) {
                                UIViewController *vc = weakself.navigationController.viewControllers[i];
                                if ([vc isKindOfClass:[OKMatchingInCirclesViewController class]]) {
                                    [weakself.navigationController popToViewController:vc animated:YES];
                                }
                            }
                        }];
                    });
                    return;
                }
                NSArray *array = @[@[xpub,kOKBlueManager.currentDeviceID]];
                NSString *xpubs = [array mj_JSONString];
                create = [kPyCommandsManager callInterface:kInterfaceimport_create_hw_wallet parameter:@{@"name":name,@"m":@"1",@"n":@"1",@"xpubs":xpubs,@"hd":@"0"}];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakself createComplete:create isInit:isInit pwd:pwd isHw:YES];
                });
            });
        }
            break;
        case OKAddTypeImportKeystore:
        {
            [params addEntriesFromDictionary:@{
                @"keystores":self.keystores,
                @"keystore_password":self.keystore_password
            }];
            create =  [kPyCommandsManager callInterface:kInterfaceImport_KeyStore parameter:params];
            [weakself createComplete:create isInit:isInit pwd:pwd isHw:NO];
        }
            break;
        default:
            break;
    }
}

- (void)createComplete:(NSDictionary *)create isInit:(BOOL)isInit pwd:(NSString *)pwd isHw:(BOOL)isHw
{
    OKWeakSelf(self)
    if (create != nil) {
        OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
        OKCreateResultWalletInfoModel *walletInfoModel = [createResultModel.wallet_info firstObject];
        OKWalletInfoModel *model = [kWalletManager getCurrentWalletAddress:walletInfoModel.name];
        [kWalletManager setCurrentWalletInfo:model];
        if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
            [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
        }
        if (weakself.addType == OKAddTypeCreateSolo) {
            [kTools tipMessage:MyLocalizedString(@"Creating successful", nil)];
            if (!kWalletManager.isOpenAuthBiological && isInit) {
                OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
                    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
                }];
                [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
            }else{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiRefreshWalletList object:nil];
                [weakself.OK_TopViewController dismissToViewControllerWithClassName:@"OKSetWalletNameViewController" animated:NO complete:^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];
                }];
                [weakself.navigationController popToRootViewControllerAnimated:YES];
            }
        }else{
            switch (weakself.addType) {
                case OKAddTypeCreateHDDerived:
                {
                    [kTools tipMessage:MyLocalizedString(@"Creating successful", nil)];
                }
                    break;
                case OKAddTypeCreateSolo:
                {
                    [kTools tipMessage:MyLocalizedString(@"Creating successful", nil)];
                }
                    break;
                case OKAddTypeImportPrivkeys:
                {
                    [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
                }
                    break;
                case OKAddTypeImportSeed:
                {
                    [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
                }
                    break;
                case OKAddTypeImportAddresses:
                {
                    [kTools tipMessage:MyLocalizedString(@"Import success", nil)];
                }
                    break;
                case OKAddTypeCreateHWDerived:
                {
                    [MBProgressHUD hideHUDForView:self.view animated:YES];
                    [kTools tipMessage:MyLocalizedString(@"Creating successful", nil)];
                }
                    break;
                default:
                    break;
            }

            if (!kWalletManager.isOpenAuthBiological && isInit && weakself.addType != OKAddTypeImportAddresses && weakself.addType != OKAddTypeCreateHWDerived) {
                OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
                    [weakself completeImport];
                }];
                [self.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
            }else{
                [weakself completeImport];
            }
        }
    }
}

- (void)completeImport
{
    OKWeakSelf(self)
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:nil];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiRefreshWalletList object:nil];

    if (self.where == OKWhereToSelectTypeHDMag) {
        [weakself.OK_TopViewController dismissToViewControllerWithClassName:@"OKSetWalletNameViewController" animated:NO complete:^{
            for (int i = 0; i < weakself.OK_TopViewController.navigationController.viewControllers.count; i++) {
                UIViewController *vc = weakself.OK_TopViewController.navigationController.viewControllers[i];
                if ([vc isKindOfClass:[OKHDWalletViewController class]]) {
                    [weakself.OK_TopViewController.navigationController popToViewController:vc animated:YES];
                }
            }
        }];
    }else{
        [weakself.OK_TopViewController dismissToViewControllerWithClassName:@"OKWalletViewController" animated:NO complete:^{
        }];
    }
}

#pragma mark - OKHwNotiManagerDekegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if(type == OKHWNotiTypePin_Current){
        dispatch_async(dispatch_get_main_queue(), ^{
            OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    id result = [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    if (result != nil) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [weakself.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{

                            }];
                        });
                        return;
                    }
                });
            }];
            [weakself.OK_TopViewController presentViewController:pinCodeVc animated:YES completion:nil];
        });
    }
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.walletNameTextfield) {
        NSString *str =   [self.walletNameTextfield.text stringByAppendingString:string];
        if (str.length > 15 && string.length > 0) {
            return NO;
        }else{
            if ([string isEqualToString:@" "]) {
                return NO;
            }
            return YES;
        }
    }else{
        return YES;
    }
}
@end
