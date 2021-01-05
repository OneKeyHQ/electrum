//
//  OKTakeCareMnemonicViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/16.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKTakeCareMnemonicViewController.h"

@interface OKTakeCareMnemonicViewController ()

@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewBottomConstraint;
- (IBAction)closeView:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *backUpNowBtn;
- (IBAction)backUpNowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *topTipsLabel;
@property (weak, nonatomic) IBOutlet UILabel *bottomTipsLabel;

@end

@implementation OKTakeCareMnemonicViewController

+ (instancetype)takeCareMnemonicViewController
{
    OKTakeCareMnemonicViewController *takeCareVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKTakeCareMnemonicViewController"];
    return takeCareVc;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.backUpNowBtn setLayerDefaultRadius];
    [self.contentView setLayerDefaultRadius];
    self.titleLabel.text = MyLocalizedString(@"Take care of your mnemonic", nil);
    [self.topTipsLabel setText:MyLocalizedString(@"You have successfully restored the HD Wallet on this device. We believe that you are already familiar with backing up and keeping mnemonics, so we do not require you to repeat the backup of your existing mnemonic", nil) lineSpacing:15];
    self.bottomTipsLabel.text = MyLocalizedString(@"But again, don't lose your mnemonic in case you need to use it again", nil);
    [self.backUpNowBtn setTitle:MyLocalizedString(@"I know the", nil) forState:UIControlStateNormal];
    [self setNavigationBarBackgroundColorWithClearColor];
}
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [UIView animateWithDuration:0.3 animations:^{
        self.contentViewBottomConstraint.constant = 30;
        [self.view layoutIfNeeded];
    }];
}
- (IBAction)closeView:(id)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}
- (IBAction)backUpNowBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}
@end
