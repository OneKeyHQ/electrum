//
//  OKWalletInputFeeView.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletInputFeeView.h"
#import "OKDeleteTextField.h"
#import "OKDefaultFeeInfoModel.h"

@interface OKWalletInputFeeView () <UITextFieldDelegate>

@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UIView *alertView;
@property (weak, nonatomic) IBOutlet UILabel *viewTitleLabel;
@property (weak, nonatomic) IBOutlet OKDeleteTextField *feeTF;
@property (weak, nonatomic) IBOutlet OKDeleteTextField *sizeTF;
@property (weak, nonatomic) IBOutlet UIButton *sureBtn;
@property (weak, nonatomic) IBOutlet UILabel *timeStrLabel;
@property (weak, nonatomic) IBOutlet UILabel *equaltoLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *consBottomY;
@property (weak, nonatomic) IBOutlet UIButton *closeBtn;
@property (weak, nonatomic) IBOutlet UIView *feeBgView;
@property (weak, nonatomic) IBOutlet UIView *sizeBgView;

@property (nonatomic,copy)NSString *dsize;
@property (nonatomic,strong)NSDictionary *customFeeDict;
@property (nonatomic,copy)NSString *fiatCustom;
@property (nonatomic,copy)NSString *lowfeerate;
@property (nonatomic,copy)NSString *defaultfeerate;
@property (nonatomic,copy)NSString *coinType;
@end

@implementation OKWalletInputFeeView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (void)showWalletCustomFeeDsize:(NSString *)dsize feerate:(NSString *)feerate lowfeerate:(NSString *)lowfeerate coinType:(NSString *)coinType sure:(SureBlock)sureBlock  Cancel:(CancelBlock)cancelBlock{
    OKWalletInputFeeView *inputView = [[[NSBundle mainBundle] loadNibNamed:@"OKWalletInputFeeView" owner:self options:nil] firstObject];
    inputView.cancelBlock = cancelBlock;
    inputView.sureBlock = sureBlock;
    inputView.lowfeerate = lowfeerate;
    inputView.dsize = dsize;
    inputView.defaultfeerate = feerate;
    inputView.coinType = coinType;
    if (dsize.length != 0 && dsize != nil) {
        inputView.sizeTF.text = dsize;
    }
    [[NSNotificationCenter defaultCenter] addObserver:inputView selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];

    if (feerate.length != 0 && feerate != nil) {
        inputView.feeTF.text = feerate;
        [inputView textChange:inputView.feeTF.text];
    }

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
    if (self.feeTF.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"Please enter the rate", nil)];
        return;
    }
    if ([self.feeTF.text doubleValue] < [self.lowfeerate doubleValue]) {
        [kTools tipMessage:MyLocalizedString(@"The rate is lower than the minimum for the current network. Please reenter", nil)];
        return;
    }

    if (self.sureBlock) {
        self.sureBlock(self.customFeeDict,self.fiatCustom,self.feeTF.text);
        [self closeBtnClick:nil];
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
    if (string.length == 0) {
        return YES;
    }
    if (![string isNumbers]) {
        return NO;
    }
    if ([self.feeTF.text doubleValue] > [self.lowfeerate doubleValue] * 20.0) {
        return NO;
    }
    return YES;
}
- (void)textChange:(NSString *)str
{
    if (self.feeTF.text.length == 0) {
        return;
    }
    NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_default_fee_info parameter:@{@"feerate":self.feeTF.text,@"coin":[self.coinType lowercaseString]}];
    self.customFeeDict = dict[@"customer"];
    [self refreshFeeUI];
}
- (void)refreshFeeUI
{
    self.sizeTF.text = [self.customFeeDict safeStringForKey:@"size"];
    self.equaltoLabel.text = [NSString stringWithFormat:@"%@ %@ ≈ %@%@",[self.customFeeDict safeStringForKey:@"fee"],[kWalletManager getUnitForCoinType],kWalletManager.currentFiatSymbol,[self.customFeeDict safeStringForKey:@"fiat"]];
    self.timeStrLabel.text = [NSString stringWithFormat:@"%@%@%@%@",MyLocalizedString(@"Expected time:", nil),MyLocalizedString(@"sendcoin.about", nil),[self.customFeeDict safeStringForKey:@"time"],MyLocalizedString(@"sendcoin.minutes", nil)];
}

@end
