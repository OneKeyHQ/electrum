//
//  OKEditWalletNameViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKEditWalletNameViewController.h"

@interface OKEditWalletNameViewController ()<UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *confirmBtn;
- (IBAction)confirmBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *textFieldBgView;
@property (weak, nonatomic) IBOutlet UITextField *textField;

@property (nonatomic,copy)ConfirmBtnClick block;

@end

@implementation OKEditWalletNameViewController

+ (instancetype)editWalletNameViewController:(ConfirmBtnClick)btnClick
{
    OKEditWalletNameViewController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKEditWalletNameViewController"];
    vc.block = btnClick;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
  
    [self.contentView setLayerRadius:20];
    [self.confirmBtn setLayerRadius:20];
    [self.textFieldBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.titleLabel.text = MyLocalizedString(@"Edit the name", nil);
    self.descLabel.text = MyLocalizedString(@"The name of the", nil);
    [self.confirmBtn setTitle:MyLocalizedString(@"determine", nil) forState:UIControlStateNormal];
    self.textField.text = kWalletManager.currentWalletInfo.label;
    self.textField.delegate = self;
}

- (IBAction)confirmBtnClick:(UIButton *)sender {
    if (self.textField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The wallet name cannot be empty", nil)];
        return;
    }
    
    if ([self.textField.text isEqualToString:kWalletManager.currentWalletInfo.label]) {
        [self closeBtnClick:nil];
        return;
    }
    
    if (![kWalletManager checkWalletName:self.textField.text]) {
        [kTools tipMessage:MyLocalizedString(@"Wallet names cannot exceed 15 characters", nil)];
        return;
    }
    
    NSString *msg =  [kPyCommandsManager callInterface:kInterfacerename_wallet parameter:@{@"old_name": kWalletManager.currentWalletInfo.name,@"new_name" : self.textField.text}];
    if (msg != nil) {
        [kTools tipMessage:MyLocalizedString(@"Name modification successful", nil)];
        OKWalletInfoModel *model = kWalletManager.currentWalletInfo;
        model.label = self.textField.text;
        [kWalletManager setCurrentWalletInfo:model];
        [self closeBtnClick:nil];
        if (self.block) {
            self.block();
        }
    }
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}


#pragma mark -  UITextViewDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSString *str =   [self.textField.text stringByAppendingString:string];
    if (str.length > 15 && string.length > 0) {
        return NO;
    }else{
        return YES;
    }
}

@end
