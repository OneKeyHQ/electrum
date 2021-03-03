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
#import "OKBTCAddressTypeSelectController.h"

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

    id result =  [kPyCommandsManager callInterface:kInterfaceverify_legality parameter:@{@"data":[self.wordInputView.wordsArr componentsJoinedByString:@" "],@"flag":@"seed"}];
    if (result != nil) {
        [self getAddressType:^(OKBTCAddressType btcAddressType) {
            OKSetWalletNameViewController *setNameVc = [OKSetWalletNameViewController setWalletNameViewController];
            setNameVc.addType = OKAddTypeImportSeed;
            setNameVc.coinType = self.coinType;
            setNameVc.where = OKWhereToSelectTypeWalletList;
            setNameVc.seed = [self.wordInputView.wordsArr componentsJoinedByString:@" "];
            setNameVc.btcAddressType = btcAddressType;
            [self.navigationController pushViewController:setNameVc animated:YES];
        }];
    }
}

- (void)getAddressType:(void(^)(OKBTCAddressType btcAddressType))callback {
    if (!callback) { return; }

    NSString *coinType = self.coinType.lowercaseString;
    if (![coinType isEqualToString:@"btc"]) {
        callback(OKBTCAddressTypeNotBTC);
        return;
    }

    OKWeakSelf(self)
    OKBTCAddressTypeSelectController *vc = [OKBTCAddressTypeSelectController viewControllerWithStoryboard];
    vc.callback = ^(OKBTCAddressType type) {
        [weakself.navigationController dismissViewControllerAnimated:YES completion:nil];
        callback(type);
    };
    [self.navigationController presentViewController:vc animated:YES completion:nil];
}
@end
