//
//  OKWalletInputFeeView.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletInputFeeView.h"
#import "OKDeleteTextField.h"
#import "OKSendFeeModel.h"
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


@property (nonatomic,strong)NSDictionary *customFeeDict;
@property (nonatomic,copy)NSString *fiatCustom;
@property (nonatomic,copy)NSString *coinType;

@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *rightTitleLabel;

@property (nonatomic,strong)OKDefaultFeeInfoModel *model;
@property (nonatomic,strong)OKSendFeeModel *selectModel;
@property (nonatomic,assign)OKFeeType feetype;
@property (nonatomic,copy)NSString *lowNum;
@end

@implementation OKWalletInputFeeView

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

+ (void)showWalletCustomFeeModel:(OKDefaultFeeInfoModel *)model feetype:(OKFeeType)feetype coinType:(NSString *)coinType sure:(SureBlock)sureBlock  Cancel:(CancelBlock)cancelBlock{
    OKWalletInputFeeView *inputView = [[[NSBundle mainBundle] loadNibNamed:@"OKWalletInputFeeView" owner:self options:nil] firstObject];
    inputView.cancelBlock = cancelBlock;
    inputView.sureBlock = sureBlock;
    inputView.feetype = feetype;
    inputView.coinType = coinType;
    inputView.model = model;
    [inputView stupUI];

    [[NSNotificationCenter defaultCenter] addObserver:inputView selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];


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

- (void)stupUI
{
    OKWeakSelf(self)
    OKSendFeeModel *selectModel;
    switch (self.feetype) {
        case OKFeeTypeFast:
            selectModel = weakself.model.fast;
            break;
        case OKFeeTypeRecommend:
            selectModel = weakself.model.normal;
            break;
        case OKFeeTypeSlow:
            selectModel = weakself.model.slow;
            break;

        default:
            break;
    }
    self.selectModel = selectModel;
    if ([self.coinType isEqualToString:COIN_BTC]) {
        self.lowNum = self.model.slow.feerate;
        self.sizeTF.userInteractionEnabled = NO;
        self.sizeTF.textColor = RGB(141, 149, 158);
        self.sizeBgView.backgroundColor = HexColor(0xF6F6F6);
        if (selectModel.size.length != 0 && selectModel.size != nil) {
            self.sizeTF.text = selectModel.size;
        }
        if (selectModel.feerate.length != 0 && selectModel.feerate != nil) {
            self.feeTF.text = selectModel.feerate;
            [self textChange:self.feeTF.text];
        }
    }else if ([kWalletManager isETHClassification:self.coinType]){
        self.lowNum = self.model.slow.gas_price;
        self.sizeTF.userInteractionEnabled = YES;
        self.sizeTF.textColor = HexColor(0x000000);
        self.sizeBgView.backgroundColor = HexColor(0xFFFFFF);
        self.feeTF.text = selectModel.gas_price;
        self.sizeTF.text = selectModel.gas_limit;
        [self textChange:self.feeTF.text];
    }
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
        NSString *msg = [self.coinType isEqualToString:COIN_BTC]?MyLocalizedString(@"Please enter the rate", nil):MyLocalizedString(@"Please enter the Gas Price", nil);
        [kTools tipMessage:msg];
        return;
    }
    if ([self.feeTF.text doubleValue] < [self.lowNum doubleValue]) {
        NSString *msg = [self.coinType isEqualToString:COIN_BTC]?MyLocalizedString(@"The rate is lower than the minimum for the current network. Please reenter", nil):MyLocalizedString(@"GasPrice is lower than the minimum value. Please re-enter", nil);
        [kTools tipMessage:msg];
        return;
    }

    if (self.sureBlock) {
        OKWalletInputFeeViewCallBackModel *model = [OKWalletInputFeeViewCallBackModel new];
        model.customFeeDict = [self getCustomFeeDict];
        model.fiat = self.fiatCustom;
        model.feeTfStr = self.feeTF.text;
        model.sizeTfStr = self.sizeTF.text;
        self.sureBlock(model);
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
    if ([textField isEqual:self.feeTF] && [[self.feeTF.text stringByAppendingString:string] doubleValue] > [self.lowNum doubleValue] * 20.0) {
        return NO;
    }

    if ([textField isEqual:self.sizeTF] && [[self.sizeTF.text stringByAppendingString:string] doubleValue] > 400000.0) {
        return NO;
    }

    return YES;
}
- (void)textChange:(NSString *)str
{
    if (self.feeTF.text.length == 0) {
        return;
    }
    if ([self.coinType isEqualToString:COIN_BTC]) {
        NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_default_fee_info parameter:@{@"feerate":self.feeTF.text,@"coin":[self.coinType lowercaseString]}];
        self.customFeeDict = dict[@"customer"];
        [self refreshFeeUI];
    }else if ([kWalletManager isETHClassification:self.coinType]){
        [self refreshFeeUI];
    }
}
- (void)refreshFeeUI
{
    if ([self.coinType isEqualToString:COIN_BTC]) {
        self.leftTitleLabel.text = @"Fee per byte(sat/b)";
        self.rightTitleLabel.text = @"Size(byte)";
        self.sizeTF.text = [self.customFeeDict safeStringForKey:@"size"];
        self.equaltoLabel.text = [NSString stringWithFormat:@"%@ %@ ≈ %@%@",[self.customFeeDict safeStringForKey:@"fee"],[kWalletManager getUnitForCoinType],kWalletManager.currentFiatSymbol,[self.customFeeDict safeStringForKey:@"fiat"]];
        self.timeStrLabel.text = [NSString stringWithFormat:@"%@%@%@%@",MyLocalizedString(@"Expected time:", nil),MyLocalizedString(@"sendcoin.about", nil),[self.customFeeDict safeStringForKey:@"time"],MyLocalizedString(@"sendcoin.minutes", nil)];

    }else if ([kWalletManager isETHClassification:self.coinType]){
        self.leftTitleLabel.text = @"Gas Price(gwei)";
        self.rightTitleLabel.text = @"Gas Limit(gas)";
        NSString *ethFee = [self getETHFee];
        NSString *ethTime = [self getEthTime:ethFee];
        self.equaltoLabel.text = [NSString stringWithFormat:@"%@ %@ ≈ %@%@",ethFee,[kWalletManager getUnitForCoinType],kWalletManager.currentFiatSymbol,[self getEthFiat:ethFee]];
        self.timeStrLabel.text = [NSString stringWithFormat:@"%@%@%@%@",MyLocalizedString(@"Expected time:", nil),MyLocalizedString(@"sendcoin.about", nil),ethTime,MyLocalizedString(@"sendcoin.minutes", nil)];
    }
}


- (NSString *)coinType
{
    return [_coinType uppercaseString];
}

- (NSString *)getETHFee
{
    if (self.feeTF.text.length == 0 || self.sizeTF.text.length == 0) {
        return @"";
    }
    NSDecimalNumber *gasPrice = [NSDecimalNumber decimalNumberWithString:self.feeTF.text];
    NSDecimalNumber *gasLimit = [NSDecimalNumber decimalNumberWithString:self.sizeTF.text];
    NSDecimalNumber *feeDecimal = [[gasLimit decimalNumberByMultiplyingBy:gasPrice]decimalNumberByMultiplyingByPowerOf10:-9];
    return [feeDecimal stringValue];
}

- (NSString *)getEthTime:(NSString *)fee
{
    if (fee.length == 0) {
        return @"--";
    }
    if ([fee doubleValue] >= [self.model.slow.fee doubleValue] && [fee doubleValue] < [self.model.normal.fee doubleValue]) {
        return self.model.slow.time;
    }else if([fee doubleValue] >= [self.model.normal.fee doubleValue] && [fee doubleValue] < [self.model.fast.fee doubleValue]) {
        return self.model.normal.time;
    }else if ([fee doubleValue] >= [self.model.fast.fee doubleValue]){
        return self.model.fast.time;
    }else{
        return @"--";
    }
}

- (NSString *)getEthFiat:(NSString *)fee
{
    if (self.model.normal.fiat.length == 0) {
        return @"--";
    }
    NSString *nFiatStr = [[self.model.normal.fiat componentsSeparatedByString:@" "] firstObject];
    NSString *nFeeStr = self.model.normal.fee;
    NSDecimalNumber *nFiatNum = [NSDecimalNumber decimalNumberWithString:nFiatStr];
    NSDecimalNumber *nFeeNum = [NSDecimalNumber decimalNumberWithString:nFeeStr];
    NSDecimalNumber *feeNum = [NSDecimalNumber decimalNumberWithString:fee];

    NSDecimalNumberHandler *roundUp = [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundBankers scale:2 raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:YES];
    NSDecimalNumber *ethFiat = [feeNum decimalNumberByMultiplyingBy:[nFiatNum decimalNumberByDividingBy:nFeeNum]withBehavior:roundUp];

    return [ethFiat stringValue];
}

- (NSDictionary *)getCustomFeeDict
{
    if ([self.coinType isEqualToString:COIN_BTC]) {
        return self.customFeeDict;
    }else if ([kWalletManager isETHClassification:self.coinType]){
        NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
        NSString *ethfee  = [self getETHFee];
        NSString *ethFiat = [self getEthFiat:ethfee];
        NSString *ethTime = [self getEthTime:ethfee];
        [dictM setValue:ethfee forKey:@"fee"];
        [dictM setValue:ethFiat forKey:@"fiat"];
        [dictM setValue:ethTime forKey:@"time"];
        [dictM setValue:self.feeTF.text forKey:@"gas_price"];
        [dictM setValue:self.sizeTF.text forKey:@"gas_limit"];
        return [dictM copy];
    }else{
        return nil;
    }
}
@end
