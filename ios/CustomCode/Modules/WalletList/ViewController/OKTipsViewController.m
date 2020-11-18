//
//  OKTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/4.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTipsViewController.h"

@interface OKTipsViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *iKnowBtn;
- (IBAction)iKnowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@end

@implementation OKTipsViewController
+ (instancetype)tipsViewController
{
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil]instantiateViewControllerWithIdentifier:@"OKTipsViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.titleLabel.text = MyLocalizedString(@"What is HD Wallet", nil);
    [self.iKnowBtn setTitle:MyLocalizedString(@"I know the", nil) forState:UIControlStateNormal];
    self.detailLabel.attributedText = [NSString lineSpacing:30 content:MyLocalizedString(@"HD wallets are known as Hierarchical Deterministic wallets in Chinese. It is by far the best and most convenient deterministic wallet", nil)];
    [self.iKnowBtn setLayerRadius:20];
    [self.contentView setLayerRadius:20];
}

- (IBAction)iKnowBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
