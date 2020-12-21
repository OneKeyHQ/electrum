//
//  OKReceiveCoinViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKReceiveCoinViewController.h"
#import "OKShareView.h"

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

@property (nonatomic,strong)NSDictionary *qrDataDict;

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
    NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_wallet_address_show_UI parameter:@{}];
    self.qrDataDict = dict;
    [self refreshUI];
    BOOL isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":kWalletManager.currentWalletInfo.name}] boolValue];
    if (!isBackUp) {
        OKWeakSelf(self)
        [kTools alertTips:MyLocalizedString(@"prompt", nil) desc:MyLocalizedString(@"The wallet has not been backed up. For the safety of your funds, please complete the backup before using this address to initiate the collection", nil) confirm:^{} cancel:^{
            [weakself.navigationController popViewControllerAnimated:YES];
        } vc:weakself conLabel:MyLocalizedString(@"I have known_alert", nil) isOneBtn:NO];
    }
}

- (void)stupUI
{
    self.titleNavLabel.text = MyLocalizedString(@"ok collection", nil);
    self.titleLabel.text = [NSString stringWithFormat:@"%@%@",MyLocalizedString(@"Scan goes to", nil),self.coinType];
    self.walletAddressTitleLabel.text = MyLocalizedString(@"The wallet address", nil);
    [self.bgView setLayerDefaultRadius];
    [self setNavigationBarBackgroundColorWithClearColor];
    [self backButtonWhiteColor];
    if (self.walletType == OKWalletTypeHD || self.walletType == OKWalletTypeIndependent) {
        self.verifyBtn.hidden = YES;
        self.bottomTipsLabel.hidden = YES;
    }else if(self.walletType == OKWalletTypeHardware){
        self.verifyBtn.hidden = NO;
        self.bottomTipsBgView.hidden = NO;
    }
}

- (void)refreshUI{
    self.QRCodeImageView.image = [QRCodeGenerator qrImageForString:[self.qrDataDict safeStringForKey:@"qr_data"] imageSize:207];
    self.walletAddressLabel.text = [self.qrDataDict safeStringForKey:@"addr"];
}

- (IBAction)shareBtnClick:(UIButton *)sender {
    OKShareView *shareView = [OKShareView initViewWithImage:self.QRCodeImageView.image coinType:self.coinType address:self.walletAddressLabel.text];
    [OKSystemShareView showSystemShareViewWithActivityItems:@[[shareView convertImage2WithOptions]] parentVc:self cancelBlock:^{
        
    } shareCompletionBlock:^{
        
    }];
}

- (IBAction)cyBtnClick:(UIButton *)sender {
    [kTools pasteboardCopyString:self.walletAddressLabel.text msg:MyLocalizedString(@"Copied", nil)];
}

- (IBAction)verifyBtnClick:(UIButton *)sender {
    NSLog(@"点击了硬件");
}
@end
