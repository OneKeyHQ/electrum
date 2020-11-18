//
//  OKWalletInputFeeView.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletInputFeeView.h"
#import "OKDeleteTextField.h"

@interface OKWalletInputFeeView () <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIView *alertView;
@property (weak, nonatomic) IBOutlet UILabel *viewTitleLabel;
@property (weak, nonatomic) IBOutlet OKDeleteTextField *feeTF;
@property (weak, nonatomic) IBOutlet OKDeleteTextField *sizeTF;
@property (weak, nonatomic) IBOutlet UIButton *sureBtn;
@property (weak, nonatomic) IBOutlet UILabel *tipsStrLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *consBottomY;

@property (weak, nonatomic) IBOutlet UIButton *closeBtn;

@property (weak, nonatomic) IBOutlet UIView *feeBgView;
@property (weak, nonatomic) IBOutlet UIView *sizeBgView;



@end

@implementation OKWalletInputFeeView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (void)showWalletCustomFeeInputSize:(NSString *)size sure:(SureBlock)sureBlock Cancel:(CancelBlock)cancelBlock{
    OKWalletInputFeeView *inputView = [[[NSBundle mainBundle] loadNibNamed:@"OKWalletInputFeeView" owner:self options:nil] firstObject];
    inputView.cancelBlock = cancelBlock;
    inputView.sureBlock = sureBlock;
    inputView.sizeTF.text = size;
    [[NSNotificationCenter defaultCenter] addObserver:inputView
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:inputView
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    [OKKeyWindow addSubview:inputView];
}

- (void)willMoveToWindow:(UIWindow *)newWindow {
    [super willMoveToWindow:newWindow];
    if (newWindow) {
        self.frame = CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        [self.sureBtn setTitle:MyLocalizedString(@"determine", nil) forState:UIControlStateNormal];
        self.viewTitleLabel.text = MyLocalizedString(@"Custom rate", nil);
        [self.feeBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
        [self.sizeBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
        [self.sureBtn setLayerRadius:20];
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
}

- (IBAction)sureBtnAction:(id)sender {
    if (self.sizeTF.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"Please enter the rate", nil)];
        return;
    }
    if (self.sureBlock) {
        self.sureBlock(self.feeTF.text);
    }
}
- (IBAction)closeBtnClick:(id)sender {
    [self removeFromSuperview];
}

- (void)keyboardWillShow:(NSNotification *)notification {
    NSDictionary *userInfo = [notification userInfo];
    NSValue *aValue = [userInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardRect = [aValue CGRectValue];
    self.consBottomY.constant = keyboardRect.size.height + 31;
}

- (void)keyboardWillHide:(NSNotification *)notification {
    self.consBottomY.constant = 31;
}

#pragma mark - UITextFieldDelegate

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if ([string containsChinese]) {
        return NO;
    }
    return YES;
}

@end
