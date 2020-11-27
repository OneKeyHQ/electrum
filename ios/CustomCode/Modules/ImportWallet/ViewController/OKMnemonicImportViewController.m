//
//  OKMnemonicImportViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKMnemonicImportViewController.h"
#import "OKSetWalletNameViewController.h"
#import "OKWordImportView.h"

@interface OKMnemonicImportViewController ()

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
@property (weak, nonatomic) IBOutlet UITextField *walletNameTextField;
@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;
@property (weak, nonatomic) IBOutlet UIView *textFieldBgView;

@end

@implementation OKMnemonicImportViewController

+ (instancetype)mnemonicImportViewController
{
    return [[UIStoryboard storyboardWithName:@"Import" bundle:nil]instantiateViewControllerWithIdentifier:@"OKMnemonicImportViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Mnemonic import", nil);
    self.walletNameTextField.placeholder = MyLocalizedString(@"Set the wallet name", nil);
    [self.textFieldBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
}

- (IBAction)next:(id)sender {
    if (self.wordInputView.wordsArr.count < 12) {
        [kTools tipMessage:MyLocalizedString(@"Please fill in the mnemonic", nil)];
        return;
    }
    if (self.walletNameTextField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"Please enter the name of the wallet", nil)];
        return;
    }
    OKWeakSelf(self)
    [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
        NSString *result =  [kPyCommandsManager callInterface:kInterfaceImport_Seed parameter:@{@"name":self.walletNameTextField.text,@"password":pwd,@"seed":[self.wordInputView.wordsArr componentsJoinedByString:@" "]}];
        if (result != nil) {
            [kTools tipMessage:MyLocalizedString(@"Mnemonic import successful", nil)];
        }
        [weakself.navigationController popToRootViewControllerAnimated:YES];
    }];
}

@end
