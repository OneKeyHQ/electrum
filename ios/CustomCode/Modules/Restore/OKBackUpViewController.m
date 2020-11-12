//
//  OKBackUpViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKBackUpViewController.h"
#import "OKWordImportView.h"
#import "OKWordCheckViewController.h"

@interface OKBackUpViewController ()

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;

@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;

@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *bottomDescLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;




@end

@implementation OKBackUpViewController

+ (instancetype)backUpViewController
{
    OKBackUpViewController *backupVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBackUpViewController"];
    return backupVc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    switch (_showType) {
        case WordsShowTypeRestore:
        {
            self.titleLabel.text = MyLocalizedString(@"HD Wallet root mnemonic", nil);
            self.descLabel.text = MyLocalizedString(@"Mnemonics are used to recover assets in other apps or wallets, transcribe them in the correct order, and place them in a safe place known only to you", nil);
            self.bottomDescLabel.text = MyLocalizedString(@"- Do not uninstall OneKey App easily - do not disclose mnemonics or private keys to anyone - do not take screenshots, send sensitive information via chat tools, etc", nil);
        }
            break;
        case WordsShowTypeExport:
        {
            self.titleLabel.text = MyLocalizedString(@"HD Wallet root mnemonic", nil);
            self.descLabel.text = MyLocalizedString(@"Mnemonics are used to recover assets in other apps or wallets, transcribe them in the correct order, and place them in a safe place known only to you", nil);
            self.bottomDescLabel.text = MyLocalizedString(@"- Do not uninstall OneKey App easily - do not disclose mnemonics or private keys to anyone - do not take screenshots, send sensitive information via chat tools, etc", nil);
        }
            break;
        default:
            break;
    }
    self.title = MyLocalizedString(@"Backup the purse", nil);
    self.wordInputView.userInteractionEnabled = NO;
    [self.wordInputView configureData:self.words];
}

- (IBAction)next:(id)sender {
    switch (_showType) {
        case WordsShowTypeRestore:
        {
            OKWordCheckViewController *wordVc = [OKWordCheckViewController wordCheckViewController];
            wordVc.words = self.words;
            [self.navigationController pushViewController:wordVc animated:YES];
        }
            break;
        case WordsShowTypeExport:
        {
            for (UIViewController *vc in self.navigationController.viewControllers) {
                if ([vc isKindOfClass:NSClassFromString(@"OKHDWalletViewController")]) {
                    [self.navigationController popToViewController:vc animated:YES];
                }
            }
        }
            break;
        default:
            break;
    }
}
@end
