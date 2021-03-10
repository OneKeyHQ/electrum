//
//  OKSendTxPreInfoViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/8.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSendTxPreInfoViewController.h"
#import "OKSendTxPreModel.h"

@interface OKSendTxPreInfoViewController ()
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewBottomConstraint;
@property (weak, nonatomic) IBOutlet UILabel *viewTitleLabel;
@property (weak, nonatomic) IBOutlet UIButton *createButton;
- (IBAction)createButtonClick:(UIButton *)sender;
@property (nonatomic,copy)BtnClickBlock clickBlock;
@property (nonatomic,copy)CancleClickBlock cancleBlock;
@property (weak, nonatomic) IBOutlet UILabel *sendFromLabel;
@property (weak, nonatomic) IBOutlet UILabel *sendFromRightLabel;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UIView *walletNameBgView;
@property (weak, nonatomic) IBOutlet UILabel *rLabel;
@property (weak, nonatomic) IBOutlet UILabel *rRightLabel;
@property (weak, nonatomic) IBOutlet UILabel *typeLabel;
@property (weak, nonatomic) IBOutlet UILabel *typeRightLabel;
@property (weak, nonatomic) IBOutlet UILabel *feeLabel;
@property (weak, nonatomic) IBOutlet UILabel *feeRightLabel;

@property (weak, nonatomic) IBOutlet UIView *txTypeBgView;

@end

@implementation OKSendTxPreInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.createButton setLayerDefaultRadius];
    [self.contentView setLayerDefaultRadius];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [UIView animateWithDuration:0.3 animations:^{
        self.contentViewBottomConstraint.constant = 30;
        [self.view layoutIfNeeded];
    }];
}

- (void)showOnWindowWithParentViewController:(UIViewController *)viewController block:(BtnClickBlock)block cancle:(nonnull CancleClickBlock)cancleBlock{
    [[[UIApplication sharedApplication] keyWindow] addSubview:self.view];
    [viewController addChildViewController:self];
    self.clickBlock = block;
    self.cancleBlock = cancleBlock;
    self.contentViewBottomConstraint.constant = -400;
    [self stupUI];
}

- (void)stupUI
{
    self.viewTitleLabel.text = MyLocalizedString(@"Transaction details", nil);
    self.sendFromLabel.text = MyLocalizedString(@"The sender", nil);
    self.rLabel.text = MyLocalizedString(@"The receiving party", nil);
    self.typeLabel.text = MyLocalizedString(@"type", nil);
    self.feeLabel.text = MyLocalizedString(@"Transaction fee", nil);
    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        [self.createButton setTitle:MyLocalizedString(@"Confirm at the facility", nil) forState:UIControlStateNormal];
        self.createButton.userInteractionEnabled = NO;
        self.createButton.alpha = 0.5;
        [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeConfirmBtn) name:kNotiHwBroadcastiComplete object:nil];
    }else{
        [self.createButton setTitle:MyLocalizedString(@"Confirm the payment", nil) forState:UIControlStateNormal];
        self.createButton.userInteractionEnabled = YES;
        self.createButton.alpha = 1.0;
    }
    [self.walletNameBgView setLayerRadius:12.5];
    if (self.info.txType.length == 0) {
        self.txTypeBgView.hidden = YES;
    }else{
        self.txTypeBgView.hidden = NO;
    }
    self.sendFromRightLabel.text = self.info.sendAddress;
    self.rRightLabel.text = self.info.rAddress;
    self.walletNameLabel.text = self.info.walletName;
    self.typeRightLabel.text = self.info.txType;
    self.feeRightLabel.text = self.info.fee;
    self.amountLabel.text = [NSString stringWithFormat:@"%@ %@",self.info.amount,self.info.coinType];
    [self.view layoutIfNeeded];
}
- (void)changeConfirmBtn
{
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
        weakself.createButton.userInteractionEnabled = YES;
        weakself.createButton.alpha = 1.0;
    });
}

- (IBAction)closeView:(id)sender {
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
    if (self.cancleBlock) {
        self.cancleBlock();
    }
}

- (IBAction)createButtonClick:(UIButton *)sender {
    if (self.clickBlock) {
        [self closeView:nil];
        self.clickBlock(@"---");
    }
}
- (void)closeView
{
    [self closeView:nil];
}
@end
