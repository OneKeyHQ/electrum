//
//  OKPrivateImportViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKPrivateImportViewController.h"
#import "OKSetWalletNameViewController.h"

@interface OKPrivateImportViewController ()<UITextViewDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIView *textBgView;
@property (weak, nonatomic) IBOutlet OKLabel *textPlacehoderLabel;
@property (weak, nonatomic) IBOutlet UITextView *textView;
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UILabel *tips2Label;
@property (weak, nonatomic) IBOutlet OKButton *importBtn;
- (IBAction)importBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *leftBgView;

@end

@implementation OKPrivateImportViewController

+ (instancetype)privateImportViewController
{
    return [[UIStoryboard storyboardWithName:@"Import" bundle:nil]instantiateViewControllerWithIdentifier:@"OKPrivateImportViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"The private key import", nil);
    self.iconImageView.image = [UIImage imageNamed:@"token_btc"];
    [self.textBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.textPlacehoderLabel.text = MyLocalizedString(@"Enter the private key or scan the QR code (case sensitive)", nil);
    self.tips1Label.text = MyLocalizedString(@"Once imported, the private key is encrypted and stored on your local device for safekeeping. OneKey does not store any private data, nor can it retrieve it for you", nil);
    self.tips2Label.text = MyLocalizedString(@"privateimporttips2", nil);
    [self.leftBgView setLayerRadius:2];
    [self.importBtn setLayerDefaultRadius];
    [self textChange];
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemScanBtnWithTarget:self selector:@selector(scanBtnClick)];
}


- (IBAction)importBtnClick:(UIButton *)sender {
    if (self.textView.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The private key cannot be empty", nil)];
        return;
    }
    
    id reult =  [kPyCommandsManager callInterface:kInterfaceverify_legality parameter:@{@"data":self.textView.text,@"flag":@"private"}];
    if (reult != nil) {
        OKSetWalletNameViewController *setNameVc = [OKSetWalletNameViewController setWalletNameViewController];
        setNameVc.addType = self.importType;
        setNameVc.privkeys = self.textView.text;
        setNameVc.where = OKWhereToSelectTypeWalletList;
        [self.navigationController pushViewController:setNameVc animated:YES];
    }
}

#pragma mark - 扫描二维码
- (void)scanBtnClick
{
    OKWeakSelf(self)
    OKWalletScanVC *vc = [OKWalletScanVC initViewControllerWithStoryboardName:@"Scan"];
    vc.scanningType = ScanningTypeAddress;
    vc.scanningCompleteBlock = ^(NSString* result) {
        if (result && result.length > 0) {
            weakself.textView.text = result;
            weakself.textPlacehoderLabel.hidden = YES;
            [weakself textChange];
        }
    };
    [vc authorizePushOn:self];
}


#pragma mark - TextView
- (void)textViewDidChange:(UITextView *)textView
{
    [self textChange];
}

- (void)textChange{
    if (self.textView.text.length > 10) {
        [self.importBtn status:OKButtonStatusEnabled];
    }else{
        [self.importBtn status:OKButtonStatusDisabled];
    }
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(nonnull NSString *)text {
    NSString *result = [textView.text stringByAppendingString:text];
    if (result.length > 100) {
        return NO;
    }
    if (textView == self.textView) {
        if (text.length == 0) { // 退格
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
