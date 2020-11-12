//
//  OKObserveImportViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKObserveImportViewController.h"
#import "OKSetWalletNameViewController.h"

@interface OKObserveImportViewController ()<UITextViewDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIView *textBgView;
@property (weak, nonatomic) IBOutlet OKLabel *textPlacehoderLabel;
@property (weak, nonatomic) IBOutlet UITextView *textView;
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet OKButton *importBtn;
- (IBAction)importBtnClick:(UIButton *)sender;
@end

@implementation OKObserveImportViewController
+ (instancetype)observeImportViewController
{
    return [[UIStoryboard storyboardWithName:@"Import" bundle:nil]instantiateViewControllerWithIdentifier:@"OKObserveImportViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Observe the purse import", nil);
    self.iconImageView.image = [UIImage imageNamed:@"token_btc"];
    [self.textBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.textPlacehoderLabel.text = MyLocalizedString(@"Please enter an address or public key, support xPub, or scan Two-dimensional code import", nil);
    self.tips1Label.text = MyLocalizedString(@"Observing a wallet does not require importing a private key or mnemonic, just an address or public key, which you can use to track daily transactions or to receive notifications of incoming or outgoing money", nil);
    [self.importBtn setLayerDefaultRadius];
    [self textChange];
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemScanBtnWithTarget:self selector:@selector(scanBtnClick)];
    
}
#pragma mark - 导入
- (IBAction)importBtnClick:(UIButton *)sender
{
    if (self.textView.text.length == 0) {
        [kTools tipMessage:@"地址不能为空"];
        return;
    }
    
    OKSetWalletNameViewController *setNameVc = [OKSetWalletNameViewController setWalletNameViewController];
    setNameVc.addType = self.importType;
    setNameVc.address = self.textView.text;
    [self.navigationController pushViewController:setNameVc animated:YES];
}
#pragma mark - 扫描
- (void)scanBtnClick
{
    [kTools tipMessage:@"点击扫描二维码"];
}
#pragma mark - TextView
- (void)textViewDidChange:(UITextView *)textView
{
    [self textChange];
}

- (void)textChange{
    if (self.textView.text.length > 0) {
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

@end
