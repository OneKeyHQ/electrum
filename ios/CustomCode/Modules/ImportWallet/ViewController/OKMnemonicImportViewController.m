//
//  OKMnemonicImportViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKMnemonicImportViewController.h"
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
    NSLog(@"点击了导入");
}

@end
