//
//  OKPrivateKeyExportViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKPrivateKeyExportViewController.h"
#import "OKWalletDetailViewController.h"

@interface OKPrivateKeyExportViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIView *privateKeyBgView;
@property (weak, nonatomic) IBOutlet UITextView *keyTextView;
@property (weak, nonatomic) IBOutlet UILabel *bottomTipsLabel;
@property (weak, nonatomic) IBOutlet UIButton *btnCopy;
- (IBAction)btnCopyClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *greenView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *keyTextViewConsH;

@property (weak, nonatomic) IBOutlet UIView *showQRImageBgView;
@property (weak, nonatomic) IBOutlet UIView *QRBgView;
@property (weak, nonatomic) IBOutlet UILabel *topLabel;
@property (weak, nonatomic) IBOutlet UILabel *showQrLabel;

@property (nonatomic,assign)BOOL showQr;

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
    _showQr = NO;
    self.iconImageView.hidden = !_showQr;
    self.bottomTipsLabel.text = MyLocalizedString(@"1. Carefully copy with pen and paper and keep it in a safe place after confirmation. 2. Mobile photo albums are easily accessible by other apps. 2. OneKey does not store any private key, which cannot be recovered once lost", nil);
    [self.btnCopy setTitle:MyLocalizedString(@"I copied", nil) forState:UIControlStateNormal];
    [self.privateKeyBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    switch (_exportType) {
        case OKExportTypePrivate:
            self.keyTextView.text = self.keyStr;
            self.keyTextViewConsH.constant = 50;
            self.keyTextView.editable = NO;
            self.keyTextView.selectable = NO;
            self.keyTextView.userInteractionEnabled = NO;
            self.title = MyLocalizedString(@"The private key export", nil);
            break;
        case OKExportTypeKeyStore:
            self.keyTextView.text = self.keyStr;
            self.keyTextViewConsH.constant = 128;
            self.keyTextView.userInteractionEnabled = YES;
            self.keyTextView.editable = NO;
            self.keyTextView.selectable = NO;
            self.title = MyLocalizedString(@"Keystore export", nil);
            break;
        default:
            break;
    }
    self.iconImageView.image = [QRCodeGenerator qrImageForString:self.keyStr imageSize:200];
    [self.btnCopy setLayerRadius:20];
    [self.greenView setLayerRadius:2];
    [self.QRBgView setLayerRadius:20];
    [self.showQRImageBgView setLayerRadius:14];

    UITapGestureRecognizer *tapp = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(privateKeyBgViewClick)];
    [self.privateKeyBgView addGestureRecognizer:tapp];

    UITapGestureRecognizer *tapshowQr = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapshowQrClick)];
    [self.showQRImageBgView addGestureRecognizer:tapshowQr];
    self.showQrLabel.text = MyLocalizedString(@"Display qr code", nil);
    [self.view layoutIfNeeded];
}

- (IBAction)btnCopyClick:(UIButton *)sender {
    OKWeakSelf(self)
    if (kWalletManager.isOpenAuthBiological) {
        for (int i = 0; i < weakself.OK_TopViewController.navigationController.viewControllers.count; i++) {
            UIViewController *vc = weakself.OK_TopViewController.navigationController.viewControllers[i];
            if ([vc isKindOfClass:[OKWalletDetailViewController class]]) {
                [weakself.OK_TopViewController.navigationController popToViewController:vc animated:YES];
            }
        }
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)tapshowQrClick
{
    _showQr = !_showQr;
    self.iconImageView.hidden = !_showQr;
    if (self.iconImageView.hidden) {
        self.showQrLabel.text = MyLocalizedString(@"Display qr code", nil);
    }else{
        self.showQrLabel.text = MyLocalizedString(@"Hide QR code", nil);
    }
}

- (void)privateKeyBgViewClick
{
    [kTools pasteboardCopyString:self.keyTextView.text msg:MyLocalizedString(@"Copied", nil)];
}

@end
