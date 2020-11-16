//
//  OKBackUpTipsViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKBackUpTipsViewController.h"

@interface OKBackUpTipsViewController ()
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewBottomConstraint;
@property (nonatomic,copy)BackUpBtnClickBlock clickBlock;
- (IBAction)closeView:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *backUpNowBtn;
- (IBAction)backUpNowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *topTipsLabel;
@property (weak, nonatomic) IBOutlet UILabel *bottomTipsLabel;
@end

@implementation OKBackUpTipsViewController
+ (instancetype)backUpTipsViewController:(BackUpBtnClickBlock)blockClick
{
    OKBackUpTipsViewController *backupVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBackUpTipsViewController"];
    backupVc.clickBlock = blockClick;
    return backupVc;
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.nextBtn setLayerBoarderColor:HexColor(0xBDBDBD) width:1 radius:20];
    [self.backUpNowBtn setLayerDefaultRadius];
    [self.contentView setLayerDefaultRadius];
    [self.topTipsLabel setText:MyLocalizedString(@"If you don't back up your wallet, once you lose your phone, you won't be able to recover your assets. In some extreme cases, the phone manufacturer may have an accident during the system upgrade, causing all your data or App to be lost, with unpredictable consequences if you happen not to have a backup", nil) lineSpacing:15];
    self.bottomTipsLabel.text = MyLocalizedString(@"The only way to protect your assets is to back them up correctly", nil);
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
- (IBAction)nextBtnClick:(UIButton *)sender {
    if (self.clickBlock) {
        [self closeView:nil];
        self.clickBlock(BackUpBtnClickTypeClose);
    }
}
- (IBAction)backUpNowBtnClick:(UIButton *)sender {
    if (self.clickBlock) {
        self.clickBlock(BackUpBtnClickTypeBackUp);
    }
}
@end
