//
//  OKKeystoreImportViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKKeystoreImportViewController.h"
#import "OKSetWalletNameViewController.h"

@interface OKKeystoreImportViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UIView *textBgView;
@property (weak, nonatomic) IBOutlet OKLabel *textPlacehoderLabel;

@property (weak, nonatomic) IBOutlet UITextView *textView;

@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UILabel *tips2Label;
@property (weak, nonatomic) IBOutlet OKButton *importBtn;
- (IBAction)importBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *leftBgView;

@property (weak, nonatomic) IBOutlet UIView *pwdBgView;
@property (weak, nonatomic) IBOutlet UITextField *pwdTextField;
@property (weak, nonatomic) IBOutlet UIButton *eyeBtn;
- (IBAction)eyeBtnClick:(UIButton *)sender;

@end

@implementation OKKeystoreImportViewController
+ (instancetype)keystoreImportViewController
{
    return [[UIStoryboard storyboardWithName:@"Import" bundle:nil]instantiateViewControllerWithIdentifier:@"OKKeystoreImportViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];

    self.title = MyLocalizedString(@"Keystore import", nil);
    self.iconImageView.image = [UIImage imageNamed:@"token_btc"];
    [self.textBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.textPlacehoderLabel.text = MyLocalizedString(@"Copy and paste the contents of the Keystore file, or scan it Keystore QR code import", nil);
    self.tips1Label.text = MyLocalizedString(@"Once imported, the private key is encrypted and stored on your local device for safekeeping. OneKey does not store any private data, nor can it retrieve it for you", nil);
    self.tips2Label.text = MyLocalizedString(@"privateimporttips2", nil);
    [self.leftBgView setLayerRadius:2];
    [self.importBtn setLayerDefaultRadius];
    [self.pwdBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];

    self.pwdTextField.placeholder = MyLocalizedString(@"Enter the Keystore file password", nil);
    [self textChange];
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemScanBtnWithTarget:self selector:@selector(scanBtnClick)];
}

#pragma mark - 导入
- (IBAction)importBtnClick:(UIButton *)sender
{
    OKSetWalletNameViewController *setNameVc = [OKSetWalletNameViewController setWalletNameViewController];
    setNameVc.addType = self.importType;
    [self.navigationController pushViewController:setNameVc animated:YES];
}
#pragma mark - 扫描
- (void)scanBtnClick
{
    NSLog(@"scanBtnClick");
}
#pragma mark - TextView
- (void)textViewDidChange:(UITextView *)textView
{
    [self textChange];
}

- (void)textChange{
    if (self.textView.text.length > 10) {
        [self.importBtn status:OKButtonStatusEnabled];
    }
    else{
        [self.importBtn status:OKButtonStatusDisabled];
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(nonnull NSString *)text {
    NSString *result = [textView.text stringByAppendingString:text];
    if (result.length > 100) {
        return NO;
    }
    if (textView == self.textView) {
        if (text.length == 0) { 
            if (textView.text.length == 1) {
                self.textPlacehoderLabel.hidden = NO;
            }
        } else {
            if (self.textPlacehoderLabel.hidden == NO) {
                self.textPlacehoderLabel.hidden = YES;
            }
        }
    }
    
    return YES;
}

- (IBAction)eyeBtnClick:(UIButton *)sender {
    NSLog(@"点击了眼睛");
}
@end
