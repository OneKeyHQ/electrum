//
//  OKDeleteWalletConfirmController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKDeleteWalletConfirmController.h"

@interface OKDeleteWalletConfirmController ()
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *cancleBtn;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UIButton *confirmDeleteBtn;

- (IBAction)confirmDeleteBtnClick:(UIButton *)sender;
- (IBAction)cancleBtnClick:(UIButton *)sender;


@property (nonatomic,copy)ConfirmBtnClick block;

@end

@implementation OKDeleteWalletConfirmController

+ (instancetype)deleteWalletConfirmController:(ConfirmBtnClick)btnClick
{
    OKDeleteWalletConfirmController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeleteWalletConfirmController"];
    vc.block = btnClick;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.contentView setLayerRadius:20];
    [self.confirmDeleteBtn setLayerRadius:20];
    [self.cancleBtn setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.titleLabel.text = MyLocalizedString(@"To delete the wallet", nil);
    self.descLabel.text = MyLocalizedString(@"This action will delete all data for the wallet, please make sure the wallet is backed up before deleting", nil);
    [self.confirmDeleteBtn setTitle:MyLocalizedString(@"Confirm to delete the wallet", nil) forState:UIControlStateNormal];
    [self.cancleBtn setTitle:MyLocalizedString(@"cancel", nil) forState:UIControlStateNormal];
}
- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (IBAction)cancleBtnClick:(UIButton *)sender {
    [self closeBtnClick:nil];
}

- (IBAction)confirmDeleteBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:^{
        if (self.block) {
            self.block();
        }
    }];
}
@end
