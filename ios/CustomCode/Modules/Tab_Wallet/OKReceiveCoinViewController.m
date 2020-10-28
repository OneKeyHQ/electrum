//
//  OKReceiveCoinViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKReceiveCoinViewController.h"

@interface OKReceiveCoinViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *coinTypeImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *QRCodeImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;


@property (weak, nonatomic) IBOutlet UIButton *verifyBtn;
@property (weak, nonatomic) IBOutlet UIView *bottomTipsBgView;


@property (weak, nonatomic) IBOutlet UIButton *cyBtn;
@property (weak, nonatomic) IBOutlet UIButton *shareBtn;

//务必在您的硬件钱包上核对地址  提示Label
@property (weak, nonatomic) IBOutlet UILabel *bottomTipsLabel;

@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIStackView *stackBgView;

- (IBAction)verifyBtnClick:(UIButton *)sender;
- (IBAction)cyBtnClick:(UIButton *)sender;
- (IBAction)shareBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *titleNavLabel;

@end

@implementation OKReceiveCoinViewController

+ (instancetype)receiveCoinViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKReceiveCoinViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self stupUI];
}

- (void)stupUI
{
//    self.title = MyLocalizedString(@"ok collection", nil);
    self.titleNavLabel.text = MyLocalizedString(@"ok collection", nil);
    self.titleLabel.text = [NSString stringWithFormat:@"%@%@",MyLocalizedString(@"Scan goes to", nil),self.coinType];
    self.walletAddressTitleLabel.text = MyLocalizedString(@"The wallet address", nil);
    self.QRCodeImageView.image = [QRCodeGenerator qrImageForString:@"3MEtJZRVPawaRjX6R8BoXB7mMyAzmXNvn6" imageSize:207];
    [self.bgView setLayerDefaultRadius];
    [self setNavigationBarBackgroundColorWithClearColor];
    [self backButtonWhiteColor];
    if (self.walletType == OKWalletTypePhone) {
        self.verifyBtn.hidden = YES;
        self.bottomTipsLabel.hidden = YES;
    }else if(self.walletType == OKWalletTypeHardware){
        self.verifyBtn.hidden = NO;
        self.bottomTipsBgView.hidden = NO;
    }
}

- (IBAction)shareBtnClick:(UIButton *)sender {
    NSLog(@"点击了分享");
}

- (IBAction)cyBtnClick:(UIButton *)sender {
    [kTools pasteboardCopyString:self.walletAddressLabel.text msg:MyLocalizedString(@"Copied", nil)];
}

- (IBAction)verifyBtnClick:(UIButton *)sender {
    NSLog(@"点击了硬件");
}
@end
