//
//  OKDeleteWalletTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKDeleteWalletTipsViewController.h"
#import "OKDeleteBackUpTipsController.h"
#import "OKHDWalletViewController.h"
#import "OKDeleteWalletConfirmController.h"

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
    
    switch (self.deleteType) {
        case OKWhereToDeleteTypeMine:
        {
            self.title = MyLocalizedString(@"Delete HD Wallet", nil);
            self.descLabel.text = MyLocalizedString(@"Once deleted: 1. All HD wallets will be erased. 2. Please make sure that the root mnemonic of HD Wallet has been copied and kept before deletion. You can use it to recover all HD Wallets and retrieve assets.", nil);
            [self.titleLabel setTitle:MyLocalizedString(@"⚠️ risk warning", nil) forState:UIControlStateNormal];
            [self.deleteBtn setTitle:MyLocalizedString(@"Delete HD Wallet", nil) forState:UIControlStateNormal];
        }
            break;
        case OKWhereToDeleteTypeDetail:
        {
            self.title = MyLocalizedString(@"Delete a separate wallet", nil);
            self.descLabel.text = MyLocalizedString(@"Once deleted: 1. The wallet will be erased from the App. 2. Please make sure that the mnemonic of the independent wallet has been copied and kept before deletion. You can use it to recover the wallet, so as to recover the assets.", nil);
            [self.titleLabel setTitle:MyLocalizedString(@"⚠️ risk warning", nil) forState:UIControlStateNormal];
            [self.deleteBtn setTitle:MyLocalizedString(@"To delete the wallet", nil) forState:UIControlStateNormal];
        }
            break;
        case OKWhereToDeleteTypeAPP:
        {
            self.title = MyLocalizedString(@"Reset the App", nil);
            self.descLabel.text = MyLocalizedString(@"This will delete all the data in the App, including the currently created wallet and custom Settings. This operation is irrevocable. Please make a backup of your wallet before deleting it so that you can recover your assets", nil);
            [self.titleLabel setTitle:MyLocalizedString(@"⚠️ risk warning", nil) forState:UIControlStateNormal];
            [self.deleteBtn setTitle:MyLocalizedString(@"Reset the App", nil) forState:UIControlStateNormal];
            
        }
            break;
        default:
            break;
    }
    
    [self.iAgree setTitle:[NSString stringWithFormat:@" %@",MyLocalizedString(@"I am aware of the above risks", nil)] forState:UIControlStateNormal];
    [self.iAgree setImage:[UIImage imageNamed:@"notselected"] forState:UIControlStateNormal];
    [self.iAgree setImage:[UIImage imageNamed:@"isselected"] forState:UIControlStateSelected];
    [self.deleteBtn setLayerRadius:20];
    [self checkUI];
}

- (IBAction)deleteBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    switch (_deleteType) {
        case OKWhereToDeleteTypeAPP:
        {
            OKDeleteWalletConfirmController *deleteVc = [OKDeleteWalletConfirmController deleteWalletConfirmController:^{
                [weakself resetAPP];
            } type:OKDeleteTipsTypeAPP];
            deleteVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [weakself.OK_TopViewController presentViewController:deleteVc animated:NO completion:nil];
        }
            break;
        case OKWhereToDeleteTypeDetail:
        case OKWhereToDeleteTypeMine:
        {
            BOOL isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":self.walletName}] boolValue];
            if (isBackUp) {
                if (kWalletManager.isOpenAuthBiological) {
                    [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                        if (state == YZAuthIDStateNotSupport
                            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                            [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                [weakself deleteWallet:pwd];
                            }];
                        } else if (state == YZAuthIDStateSuccess) {
                            NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                            [weakself deleteWallet:pwd];
                        }
                    }];
                }else{
                    [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                        [weakself deleteWallet:pwd];
                    }];
                }
            }else{
                OKWeakSelf(self)
                OKDeleteBackUpTipsController *tipsVc = [OKDeleteBackUpTipsController deleteBackUpTipsController:^{
                    [weakself.OK_TopViewController.navigationController popViewControllerAnimated:YES];
                }];
                tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
                [self presentViewController:tipsVc animated:NO completion:nil];
            }
        }
            break;
        default:
            break;
    }
}
- (void)deleteWallet:(NSString *)pwd
{
    OKWeakSelf(self)
    NSString *name = self.walletName;
    if (name == nil || name.length == 0) {
        name = kWalletManager.currentWalletInfo.name;
    }
    [kPyCommandsManager callInterface:kInterfaceDelete_wallet parameter:@{@"name":name,@"password":pwd}];
    [kWalletManager clearCurrentWalletInfo];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiDeleteWalletComplete object:nil];
    [kTools tipMessage:MyLocalizedString(@"Wallet deleted successfully", nil)];
    if (weakself.deleteType == OKWhereToDeleteTypeMine) {
        for (int i = 0; i < weakself.navigationController.viewControllers.count; i++) {
            UIViewController *vc = weakself.navigationController.viewControllers[i];
            if ([vc isKindOfClass:[OKHDWalletViewController class]]) {
                [weakself.navigationController popToViewController:vc animated:YES];
            }
        }
    }else{
        [weakself.navigationController popToRootViewControllerAnimated:YES];
    }
}

- (void)resetAPP
{
    OKWeakSelf(self)
    NSString *path =  [OKStorageManager getDocumentDirectoryPath];
    NSUserDefaults *defatluts = [NSUserDefaults standardUserDefaults];
    NSDictionary *dictionary = [defatluts dictionaryRepresentation];
    for(NSString *key in [dictionary allKeys]){
             [defatluts removeObjectForKey:key];
             [defatluts synchronize];
    }
    BOOL isSuccess = [kTools clearDataWithFilePath:path];
    if (isSuccess) {
        [kTools alertTips:MyLocalizedString(@"prompt", nil) desc:MyLocalizedString(@"When the APP is reset, just open it again", nil) confirm:^{
            [weakself exitApplication];
        } cancel:^{
            
        } vc:self conLabel:MyLocalizedString(@"determine", nil) isOneBtn:NO];
    }
}

- (void)exitApplication {
    OKWeakSelf(self)
    [UIView animateWithDuration:0.4f animations:^{
         weakself.view.window.alpha = 0;
         CGFloat y = weakself.view.window.bounds.size.height;
         CGFloat x = weakself.view.window.bounds.size.width / 2;
         weakself.view.window.frame = CGRectMake(x, y, 0, 0);
    } completion:^(BOOL finished) {
         exit(0);
    }];
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
