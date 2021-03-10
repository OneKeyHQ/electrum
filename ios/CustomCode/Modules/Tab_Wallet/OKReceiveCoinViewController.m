//
//  OKReceiveCoinViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKReceiveCoinViewController.h"
#import "OKShareView.h"
#import "OKShareActivity.h"
#import "OKMatchingInCirclesViewController.h"

@interface OKReceiveCoinViewController ()<OKHwNotiManagerDelegate>
@property (weak, nonatomic) IBOutlet UIImageView *coinTypeImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *QRCodeImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *walletAddressLabel;


@property (weak, nonatomic) IBOutlet UIButton *verifyBtn;

@property (weak, nonatomic) IBOutlet UIButton *cyBtn;
@property (weak, nonatomic) IBOutlet UIButton *shareBtn;

@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIStackView *stackBgView;

- (IBAction)verifyBtnClick:(UIButton *)sender;
- (IBAction)cyBtnClick:(UIButton *)sender;
- (IBAction)shareBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *titleNavLabel;

@property (nonatomic,strong)NSDictionary *qrDataDict;

//HW
@property (weak, nonatomic) IBOutlet UIView *hwBgView;
@property (weak, nonatomic) IBOutlet UILabel *hwDescLabel;
@property (weak, nonatomic) IBOutlet UILabel *hwStartCheckLabel;
@property (weak, nonatomic) IBOutlet UIView *startChekBgView;

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
    if ([kWalletManager getWalletDetailType] == OKWalletTypeObserve) {
        OKWeakSelf(self)
        [kTools alertTips:MyLocalizedString(@"prompt", nil) desc:MyLocalizedString(@"Are you sure you want to use the address of the wallet to initiate a collection in order to view the wallet?", nil) confirm:^{} cancel:^{
                       [weakself.navigationController popViewControllerAnimated:YES];
        } vc:self conLabel:MyLocalizedString(@"confirm", nil) isOneBtn:NO];
    }
}

- (void)stupUI
{
    self.titleNavLabel.text = MyLocalizedString(@"ok collection", nil);
    self.titleLabel.text = [NSString stringWithFormat:@"%@%@",MyLocalizedString(@"Scan goes to", nil),[self.coinType uppercaseString]];
    self.coinTypeImageView.image = [UIImage imageNamed:[NSString stringWithFormat:@"token_%@",[self.coinType lowercaseString]]];
    self.walletAddressTitleLabel.text = MyLocalizedString(@"The wallet address", nil);
    [self.bgView setLayerDefaultRadius];
    [self setNavigationBarBackgroundColorWithClearColor];
    [self backButtonWhiteColor];
    if(self.walletType == OKWalletTypeHardware){
        [self.startChekBgView setLayerRadius:14];
        [self.verifyBtn setTitle:MyLocalizedString(@"Have to check", nil) forState:UIControlStateNormal];
        self.verifyBtn.hidden = NO;
        self.hwBgView.hidden = NO;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(startCheckAddress)];
        [self.hwBgView addGestureRecognizer:tap];
    }else{
        self.verifyBtn.hidden = YES;
        self.hwBgView.hidden = YES;
    }
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemWithTitle:MyLocalizedString(@"Save", nil) titleColor:[UIColor whiteColor] target:self selector:@selector(save)];
}

- (void)refreshUI{
    if (self.walletType == OKWalletTypeHardware) {
        self.QRCodeImageView.image  = [UIImage imageNamed:@"qrcode_shade"];
        NSString *address = [self.qrDataDict safeStringForKey:@"addr"];
        self.walletAddressLabel.text = [NSString stringWithFormat:@"%@...%@",[address substringToIndex:6],[address substringFromIndex:address.length - 6]];
    }else{
        self.QRCodeImageView.image = [QRCodeGenerator qrImageForString:[self.qrDataDict safeStringForKey:@"qr_data"] imageSize:207];
        self.walletAddressLabel.text = [self.qrDataDict safeStringForKey:@"addr"];
    }
}

- (IBAction)shareBtnClick:(UIButton *)sender {
    OKShareView *shareView = [OKShareView initViewWithImage:self.QRCodeImageView.image coinType:self.coinType address:self.walletAddressLabel.text];
    UIImage* shareImage = [shareView convertImage2WithOptions];
    NSString* shareTitleString = MyLocalizedString(@"Share QRCode", nil);
    NSString* shareTitle = [NSString stringWithFormat:shareTitleString,[self.coinType uppercaseString]];
    OKShareActivity* shareActivity = [[OKShareActivity alloc]initWithImage:shareImage
                                                             andShareTitle:shareTitle];
    NSArray* shareItems = [[NSArray alloc]initWithObjects:shareActivity, nil];
    [OKSystemShareView showSystemShareViewWithActivityItems:shareItems parentVc:self cancelBlock:^{

    } shareCompletionBlock:^{

    }];
}

- (IBAction)cyBtnClick:(UIButton *)sender {
    [kTools pasteboardCopyString:self.walletAddressLabel.text msg:MyLocalizedString(@"Copied", nil)];
}

- (void)startCheckAddress
{
    self.QRCodeImageView.image = [QRCodeGenerator qrImageForString:[self.qrDataDict safeStringForKey:@"qr_data"] imageSize:207];
    self.walletAddressLabel.text = [self.qrDataDict safeStringForKey:@"addr"];
    [OKHwNotiManager sharedInstance].delegate = self;
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        id result = [kPyCommandsManager callInterface:kInterfaceshow_address parameter:@{@"address":[self.qrDataDict safeStringForKey:@"addr"],@"coin":[weakself.coinType lowercaseString]}];
        if (result != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                weakself.hwBgView.hidden = YES;
                weakself.verifyBtn.hidden = NO;
            });
        };
    });
}

- (void)save {
    OKShareView *shareView = [OKShareView initViewWithImage:self.QRCodeImageView.image coinType:self.coinType address:self.walletAddressLabel.text];
    UIImageWriteToSavedPhotosAlbum([shareView convertImage2WithOptions], self, @selector(image:didFinishSavingWithError:contextInfo:), nil);
}

-(void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    NSString *msg = MyLocalizedString(@"Save Success", nil);
    if (error) {
        msg = MyLocalizedString(@"Save Failed", nil);
    }
    [kTools tipMessage:msg];
}

#pragma mark - OKHwNotiManagerDelegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (type == OKHWNotiTypeVerify_Address_Confirm) {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.startChekBgView.hidden = YES;
            self.hwDescLabel.text = MyLocalizedString(@"Hardware wallet checking", nil);
        });
    }else if(type == OKHWNotiTypePin_Current){
        dispatch_async(dispatch_get_main_queue(), ^{
            OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    id result = [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    if (result != nil) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [weakself.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{

                            }];
                        });
                        return;
                    }
                });
            }];
            [weakself.OK_TopViewController presentViewController:pinCodeVc animated:YES completion:nil];
        });
    }
}


- (IBAction)verifyBtnClick:(UIButton *)sender {

}

- (void)backToPrevious
{
    [kPyCommandsManager callInterface:kInterface_set_cancel_flag parameter:@{}];
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    if ([self.navigationController.viewControllers indexOfObject:self]==NSNotFound)
    {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}
@end
