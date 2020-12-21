//
//  OKLookWalletTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/21.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKLookWalletTipsViewController.h"

@interface OKLookWalletTipsViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *qrImageView;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (nonatomic,copy)NSString *qrCodeStr;
@end

@implementation OKLookWalletTipsViewController
+ (instancetype)lookWalletTipsViewController:(NSString *)qr
{
    OKLookWalletTipsViewController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKLookWalletTipsViewController"];
    vc.qrCodeStr = qr;
    return vc;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.qrImageView.image = [QRCodeGenerator qrImageForString:self.qrCodeStr imageSize:240];
    [self.contentView setLayerRadius:20];
    self.titleLabel.text = MyLocalizedString(@"Use cold wallet for code scanning signatures", nil);
}

- (IBAction)closeBtnClick:(UIButton *)sender
{
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
