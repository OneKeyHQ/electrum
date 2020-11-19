//
//  OKPrivateKeyExportViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKPrivateKeyExportViewController.h"

@interface OKPrivateKeyExportViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIView *privateKeyBgView;
@property (weak, nonatomic) IBOutlet UILabel *privateKeyLabel;
@property (weak, nonatomic) IBOutlet UILabel *bottomTipsLabel;
@property (weak, nonatomic) IBOutlet UIButton *btnCopy;
- (IBAction)btnCopyClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *greenView;

@end

@implementation OKPrivateKeyExportViewController

+ (instancetype)privateKeyExportViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKPrivateKeyExportViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    self.bottomTipsLabel.text = MyLocalizedString(@"1. Carefully copy with pen and paper and keep it in a safe place after confirmation. 2. Mobile photo albums are easily accessible by other apps. 2. OneKey does not store any private key, which cannot be recovered once lost", nil);
    [self.btnCopy setTitle:MyLocalizedString(@"copy", nil) forState:UIControlStateNormal];
    [self.privateKeyBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    
    self.privateKeyLabel.text = self.privateKey;
    self.iconImageView.image = [QRCodeGenerator qrImageForString:self.privateKey imageSize:200];
    [self.btnCopy setLayerRadius:20];
    self.title = MyLocalizedString(@"The private key export", nil);
    [self.greenView setLayerRadius:2];
}

- (IBAction)btnCopyClick:(UIButton *)sender {
    
    [kTools pasteboardCopyString:self.privateKeyLabel.text msg:MyLocalizedString(@"Copied", nil)];

}

@end
