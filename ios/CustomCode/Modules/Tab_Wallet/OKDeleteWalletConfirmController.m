//
//  OKDeleteWalletConfirmController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
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
@property (nonatomic,assign)OKDeleteTipsType type;
@end

@implementation OKDeleteWalletConfirmController

+ (instancetype)deleteWalletConfirmController:(ConfirmBtnClick)btnClick type:(OKDeleteTipsType)type
{
    OKDeleteWalletConfirmController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeleteWalletConfirmController"];
    vc.block = btnClick;
    vc.type = type;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.contentView setLayerRadius:20];
    [self.confirmDeleteBtn setLayerRadius:20];
    [self.cancleBtn setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    
    switch (self.type) {
        case OKDeleteTipsTypeWallet:
        {
            self.titleLabel.text = MyLocalizedString(@"To delete the wallet", nil);
            self.descLabel.text = MyLocalizedString(@"This action will delete all data for the wallet, please make sure the wallet is backed up before deleting", nil);
            [self.confirmDeleteBtn setTitle:MyLocalizedString(@"Confirm to delete the wallet", nil) forState:UIControlStateNormal];
            [self.cancleBtn setTitle:MyLocalizedString(@"cancel", nil) forState:UIControlStateNormal];
        }
            break;
        case OKDeleteTipsTypeAPP:
        {
            self.titleLabel.text = MyLocalizedString(@"Are you sure you want to do this", nil);
            self.descLabel.text = MyLocalizedString(@"This will immediately delete all data that you have created in OneKey, and unbacked wallets will never be retrieved. Are you sure you want to proceed with the operation", nil);
            [self.confirmDeleteBtn setTitle:MyLocalizedString(@"I have confirmed, reset immediately", nil) forState:UIControlStateNormal];
            [self.cancleBtn setTitle:MyLocalizedString(@"cancel", nil) forState:UIControlStateNormal];
        }
            break;
        default:
            break;
    }
    
   
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
