//
//  OKYouareDoneViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKYouareDoneViewController.h"

@interface OKYouareDoneViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *backToWallet;
- (IBAction)backToWalletClick:(UIButton *)sender;
@end

@implementation OKYouareDoneViewController
+ (instancetype)youareDoneViewController
{
    OKYouareDoneViewController *youareDoneVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKYouareDoneViewController"];
    return youareDoneVc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Backup the purse", nil);
    self.titleLabel.text = MyLocalizedString(@"You're done", nil);
    self.iconView.image = [UIImage imageNamed:@"success"];
    self.descLabel.text = MyLocalizedString(@"Everything seems to be in order! We have nothing to remind you of. In a word, remember to take care of the mnemonic, no one can help you get it back. I wish you play in the chain of blocks in the world happy", nil);
    [self.backToWallet setTitle:MyLocalizedString(@"Return the wallet", nil) forState:UIControlStateNormal];
    [self.backToWallet setLayerRadius:20];
}

- (IBAction)backToWalletClick:(UIButton *)sender {
    if (kWalletManager.isOpenAuthBiological) {
        [self.OK_TopViewController.navigationController popToRootViewControllerAnimated:YES];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}
@end
