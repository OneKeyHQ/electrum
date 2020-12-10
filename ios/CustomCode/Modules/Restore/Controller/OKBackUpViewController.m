//
//  OKBackUpViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKBackUpViewController.h"
#import "OKWordImportView.h"
#import "OKWordCheckViewController.h"
#import "OKScreenshotsTipsController.h"
#import "OKHDWalletViewController.h"
#import "OKWalletDetailViewController.h"

@interface OKBackUpViewController ()

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *bottomDescLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *greenLeftBgView;

@end

@implementation OKBackUpViewController

+ (instancetype)backUpViewController
{
    OKBackUpViewController *backupVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBackUpViewController"];
    return backupVc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.greenLeftBgView setLayerRadius:2];
    switch (_showType) {
        case WordsShowTypeRestore:
        {
            self.title = MyLocalizedString(@"Backup the purse", nil);
            if ([kWalletManager getWalletDetailType] == OKWalletTypeHD) {
                self.titleLabel.text = MyLocalizedString(@"HD Wallet root mnemonic", nil);
            }else if([kWalletManager getWalletDetailType] == OKWalletTypeIndependent){
                self.titleLabel.text = MyLocalizedString(@"Independent wallet mnemonic", nil);
            }else{
                self.titleLabel.text = @"";
            }
            
            self.descLabel.text = MyLocalizedString(@"Mnemonics are used to recover assets in other apps or wallets, transcribe them in the correct order, and place them in a safe place known only to you", nil);
            self.bottomDescLabel.text = MyLocalizedString(@"- Do not uninstall OneKey App easily - do not disclose mnemonics or private keys to anyone - do not take screenshots, send sensitive information via chat tools, etc", nil);
        }
            break;
        case WordsShowTypeExport:
        {
            self.title = MyLocalizedString(@"Mnemonic derivation", nil);
            self.titleLabel.hidden = YES;
            self.descLabel.text = MyLocalizedString(@"Mnemonics are used to recover assets in other apps or wallets, transcribe them in the correct order, and place them in a safe place known only to you", nil);
            self.bottomDescLabel.text = MyLocalizedString(@"- Do not uninstall OneKey App easily - do not disclose mnemonics or private keys to anyone - do not take screenshots, send sensitive information via chat tools, etc", nil);
        }
            break;
        case WordsShowTypeHDExport:
        {
            self.title = MyLocalizedString(@"Mnemonic derivation", nil);
            self.titleLabel.text = MyLocalizedString(@"HD Wallet root mnemonic", nil);
            self.descLabel.text = MyLocalizedString(@"Mnemonics are used to recover assets in other apps or wallets, transcribe them in the correct order, and place them in a safe place known only to you", nil);
            self.bottomDescLabel.text = MyLocalizedString(@"- Do not uninstall OneKey App easily - do not disclose mnemonics or private keys to anyone - do not take screenshots, send sensitive information via chat tools, etc", nil);
        }
            break;
        default:
            break;
    }
    self.wordInputView.userInteractionEnabled = NO;
    [self.wordInputView configureData:self.words];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(userDidTakeScreenshot:)
                                                     name:UIApplicationUserDidTakeScreenshotNotification object:nil];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}


- (IBAction)next:(id)sender {
    
    OKWeakSelf(self)
    switch (_showType) {
        case WordsShowTypeRestore:
        {
            OKWordCheckViewController *wordVc = [OKWordCheckViewController wordCheckViewController];
            wordVc.words = self.words;
            wordVc.walletName = self.walletName;
            [self.navigationController pushViewController:wordVc animated:YES];
        }
            break;
        case WordsShowTypeExport:
        {
            if (kWalletManager.isOpenAuthBiological) {
                for (int i = 0; i < weakself.OK_TopViewController.navigationController.viewControllers.count; i++) {
                    UIViewController *vc = weakself.OK_TopViewController.navigationController.viewControllers[i];
                    if ([vc isKindOfClass:[OKWalletDetailViewController class]]) {
                        [weakself.OK_TopViewController.navigationController popToViewController:vc animated:YES];
                    }
                }
            }else{
                [self.OK_TopViewController dismissViewControllerAnimated:YES completion:nil];
            }
        }
            break;
        case WordsShowTypeHDExport:
        {
            if (kWalletManager.isOpenAuthBiological) {
                for (int i = 0; i < weakself.OK_TopViewController.navigationController.viewControllers.count; i++) {
                    UIViewController *vc = weakself.OK_TopViewController.navigationController.viewControllers[i];
                    if ([vc isKindOfClass:[OKHDWalletViewController class]]) {
                        [weakself.OK_TopViewController.navigationController popToViewController:vc animated:YES];
                    }
                }
            }else{
                [self.OK_TopViewController dismissViewControllerAnimated:YES completion:nil];
            }
        }
            break;
        default:
            break;
    }
}

- (void)userDidTakeScreenshot:(NSNotification *)noti
{
    OKScreenshotsTipsController *screenshotsTips = [OKScreenshotsTipsController screenshotsTipsController];
    screenshotsTips.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self.OK_TopViewController presentViewController:screenshotsTips animated:NO completion:nil];
    
}

@end
