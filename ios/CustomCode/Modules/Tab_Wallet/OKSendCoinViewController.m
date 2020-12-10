//
//  OKSendCoinViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

typedef enum {
    OKFeeTypeSlow,
    OKFeeTypeRecommend,
    OKFeeTypeFast
}OKFeeType;


#import "OKSendCoinViewController.h"
#import "OKWalletInputFeeView.h"
#import "OKSendTxPreInfoViewController.h"
#import "OKSendTxPreModel.h"


@interface OKSendCoinViewController ()<UITextFieldDelegate>
//Top
@property (weak, nonatomic) IBOutlet UIView *shoukuanLabelBg;
@property (weak, nonatomic) IBOutlet UILabel *shoukuanLabel;
@property (weak, nonatomic) IBOutlet UITextField *addressTextField;
@property (weak, nonatomic) IBOutlet UIButton *addressbookBtn;
- (IBAction)addressbookBtnClick:(UIButton *)sender;

//Mid
@property (weak, nonatomic) IBOutlet UIView *amountBg;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UITextField *amountTextField;
@property (weak, nonatomic) IBOutlet UIButton *moreBtn;
- (IBAction)moreBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *balanceTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *coinTypeLabel;
@property (weak, nonatomic) IBOutlet UIButton *coinTypeBtn;
- (IBAction)coinTypeBtnClick:(UIButton *)sender;
//Bottom
@property (weak, nonatomic) IBOutlet UIView *feeLabelBg;
@property (weak, nonatomic) IBOutlet UILabel *feeLabel;
@property (weak, nonatomic) IBOutlet UILabel *feeTipsLabel;
@property (weak, nonatomic) IBOutlet UIButton *customBtn;
- (IBAction)customBtnClick:(UIButton *)sender;


@property (weak, nonatomic) IBOutlet UIView *feeTypeBgView;

@property (weak, nonatomic) IBOutlet UIView *slowBg;
@property (weak, nonatomic) IBOutlet UIView *recommendedBg;
@property (weak, nonatomic) IBOutlet UIView *fastBg;
@property (weak, nonatomic) IBOutlet UIView *custom_BGView;



@property (weak, nonatomic) IBOutlet OKButton *sendBtn;
- (IBAction)sendBtnClick:(OKButton *)sender;

@property (weak, nonatomic) IBOutlet UIView *slowBottomLabelBg;
@property (weak, nonatomic) IBOutlet UIView *recommendBottomLabelBg;
@property (weak, nonatomic) IBOutlet UIView *fastBottomLabelBg;
@property (weak, nonatomic) IBOutlet UIView *customBottomLabelBg;




//手势
- (IBAction)tapSlowBgClick:(UITapGestureRecognizer *)sender;
- (IBAction)tapRecommendBgClick:(UITapGestureRecognizer *)sender;
- (IBAction)tapFastBgClick:(UITapGestureRecognizer *)sender;

//feeType内部控件
@property (weak, nonatomic) IBOutlet UILabel *slowTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *slowTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *slowSelectBtn;

@property (weak, nonatomic) IBOutlet UILabel *recommendTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *recommendTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *recommendSelectBtn;

@property (weak, nonatomic) IBOutlet UILabel *fastTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *fastTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *fastSelectBtn;


@property (weak, nonatomic) IBOutlet UILabel *customTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *customCoinAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *customMoneyAmountLabel;
@property (weak, nonatomic) IBOutlet UILabel *customTimeLabel;
@property (weak, nonatomic) IBOutlet UIButton *customSelectBtn;

//Restore default options
@property (weak, nonatomic) IBOutlet UIButton *RestoreDefaultOptionsBtn;
@property (weak, nonatomic) IBOutlet UIView *restoreDefaultBgView;
- (IBAction)restoreDefaultOptionsBtnClick:(UIButton *)sender;



@property (nonatomic,copy)NSString* currentFee_status;

@property (nonatomic,assign)OKFeeType currentFeeType;

@property (nonatomic,strong)NSDictionary *lowFeeDict;
@property (nonatomic,copy)NSString *fiatLow;

@property (nonatomic,strong)NSDictionary *recommendFeeDict;
@property (nonatomic,copy)NSString *fiatRecommend;

@property (nonatomic,strong)NSDictionary *fastFeeDict;
@property (nonatomic,copy)NSString *fiatFast;

@property (nonatomic,strong)NSDictionary *customFeeDict;
@property (nonatomic,copy)NSString *fiatCustom;

@property (nonatomic,copy)NSString *currentMemo;

@property (nonatomic,assign)BOOL custom;

@end

@implementation OKSendCoinViewController

+ (instancetype)sendCoinViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSendCoinViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self setNavigationBarBackgroundColorWithClearColor];
    self.title = MyLocalizedString(@"transfer", nil);
    _custom = NO;
    [self stupUI];
    [self changeFeeType:OKFeeTypeRecommend];
    NSString *default_fee_status =  [kPyCommandsManager callInterface:kInterfaceGet_default_fee_status parameter:@{}];
    NSArray *default_fee_statusArray = [default_fee_status componentsSeparatedByString:@" "];
    NSString* default_fee_statusNum = [default_fee_statusArray firstObject];
    self.currentFee_status = default_fee_statusNum;
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshBalance:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];
    self.addressTextField.text = self.address;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
- (void)stupUI
{
    [self.shoukuanLabelBg setLayerRadius:12];
    [self.amountBg setLayerRadius:12];
    [self.feeLabelBg setLayerRadius:12];
    [self.moreBtn setLayerRadius:8];
    [self.moreBtn setBackgroundColor:RGBA(196, 196, 196, 0.2)];
    [self.feeTypeBgView setLayerBoarderColor:HexColor(0xE5E5E5) width:1 radius:20];
    [self.slowBottomLabelBg setLayerRadius:20];
    [self.recommendBottomLabelBg setLayerRadius:20];
    [self.fastBottomLabelBg setLayerRadius:20];
    [self.customBottomLabelBg setLayerRadius:20];
    [self.custom_BGView shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
    [self.sendBtn setLayerDefaultRadius];
    

    self.slowTitleLabel.text = MyLocalizedString(@"slow", nil);
    self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.slowTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    
    self.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
    self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.recommendTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    
    self.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
    self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.fastTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [self.coinTypeBtn setTitle:kWalletManager.currentBitcoinUnit forState:UIControlStateNormal];
    
    self.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
    self.customCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",kWalletManager.currentBitcoinUnit];
    self.customTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [self.coinTypeBtn setTitle:kWalletManager.currentBitcoinUnit forState:UIControlStateNormal];
    
    [self changUIForCustom];

    //self.coinTypeBtn.hidden  = YES;
}

- (void)refreshBalance:(NSNotification *)noti
{
    NSDictionary *dict = noti.object;
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        self.balanceLabel.text =  [dict safeStringForKey:@"balance"];
        self.coinTypeLabel.text = kWalletManager.currentBitcoinUnit;
    });
}

- (void)refreshFeeSelect
{
    NSString *fiatS = kWalletManager.currentFiatSymbol;
    if (_custom) {
        self.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
        NSString *fee = [self.customFeeDict safeStringForKey:@"fee"];
        if (fee == nil) {
            fee = @"0";
        }
        self.customCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[kWalletManager getFeeBaseWithSat:fee],kWalletManager.currentBitcoinUnit];
        self.customTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.customFeeDict safeStringForKey:@"time"]==nil?@"--":[self.customFeeDict safeStringForKey:@"time"]];
        self.customMoneyAmountLabel.text = [NSString stringWithFormat:@"%@%@",fiatS,self.fiatCustom];
    }else{
        self.slowTitleLabel.text = MyLocalizedString(@"slow", nil);
        NSString *feeslow = [self.lowFeeDict safeStringForKey:@"fee"];
        if (feeslow == nil) {
            feeslow = @"-";
        }
        self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[kWalletManager getFeeBaseWithSat:feeslow],kWalletManager.currentBitcoinUnit];
        self.slowTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.lowFeeDict safeStringForKey:@"time"]==nil?@"--":[self.lowFeeDict safeStringForKey:@"time"]];
        self.slowMoneyAmountLabel.text = [NSString stringWithFormat:@"%@%@",fiatS,self.fiatLow];
        
        
        self.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
        NSString *feerecommend = [self.recommendFeeDict safeStringForKey:@"fee"];
        if (feerecommend == nil) {
            feerecommend = @"-";
        }
        self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[kWalletManager getFeeBaseWithSat:feerecommend],kWalletManager.currentBitcoinUnit];
        self.recommendTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.recommendFeeDict safeStringForKey:@"time"]==nil?@"--":[self.recommendFeeDict safeStringForKey:@"time"]];
        self.recommendMoneyAmountLabel.text = [NSString stringWithFormat:@"%@%@",fiatS,self.fiatRecommend];
        
        self.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
        NSString *feefast = [self.fastFeeDict safeStringForKey:@"fee"];
        if (feefast == nil) {
            feefast = @"-";
        }
        self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",[kWalletManager getFeeBaseWithSat:feefast],kWalletManager.currentBitcoinUnit];
        self.fastTimeLabel.text = [NSString stringWithFormat:@"约%@分钟",[self.fastFeeDict safeStringForKey:@"time"] == nil ? @"--":[self.fastFeeDict safeStringForKey:@"time"]];
        self.fastMoneyAmountLabel.text = [NSString stringWithFormat:@"%@%@",fiatS,self.fiatFast];
    }
    [self changUIForCustom];
}

- (void)changUIForCustom
{
    self.custom_BGView.hidden = !_custom;
    self.slowBg.hidden = _custom;
    self.fastBg.hidden = _custom;
    self.recommendedBg.hidden = _custom;
    self.restoreDefaultBgView.hidden = !_custom;
}

- (IBAction)addressbookBtnClick:(UIButton *)sender {
    
    
}

- (IBAction)moreBtnClick:(UIButton *)sender {
    self.amountTextField.text = self.balanceLabel.text;
}
- (IBAction)coinTypeBtnClick:(UIButton *)sender {
    
}
- (IBAction)customBtnClick:(UIButton *)sender {
    
    if (![self checkTextField]) {
        return;
    }
    OKWeakSelf(self)
    [OKWalletInputFeeView showWalletCustomFeeAddress:self.addressTextField.text amount:self.amountTextField.text sure:^(NSDictionary *customFeeDict, NSString *fiat) {
        weakself.customFeeDict = customFeeDict;
        weakself.fiatCustom = fiat;
        weakself.custom = YES;
        [weakself refreshFeeSelect];
    } Cancel:nil];
}

- (BOOL)checkTextField
{
    if (self.addressTextField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"Please enter the transfer address", nil)];
        return NO;
    }

    if (self.amountTextField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"Please enter the transfer amount", nil)];
        return NO;
    }
    
    if ([self.balanceLabel.text doubleValue] < [self.amountTextField.text doubleValue]) {
        [kTools tipMessage:MyLocalizedString(@"Lack of balance", nil)];
        return NO;
    }
    
    if ([self.amountTextField.text doubleValue] <= 0) {
        [kTools tipMessage:MyLocalizedString(@"The transfer amount cannot be zero", nil)];
        return NO;
    }
    return YES;
}
- (IBAction)sendBtnClick:(OKButton *)sender {
    if (![self checkTextField]) {
        return;
    }
    OKWeakSelf(self)
    __block  NSDictionary *dict = [NSDictionary dictionary];
    if (weakself.custom) {
        dict = self.customFeeDict;
    }else{
        switch (weakself.currentFeeType) {
            case OKFeeTypeSlow:
            {
                dict = weakself.lowFeeDict;
            }
                break;
            case OKFeeTypeRecommend:
            {
                dict = weakself.recommendFeeDict;
            }
                break;
            case OKFeeTypeFast:
            {
                dict = weakself.fastFeeDict;
            }
                break;
            default:
                break;
        }
    }
    OKSendTxPreInfoViewController *sendVc = [OKSendTxPreInfoViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    OKSendTxPreModel *model = [OKSendTxPreModel new];
    model.amount = self.amountTextField.text;
    model.coinType = self.coinTypeLabel.text;
    model.walletName = kWalletManager.currentWalletInfo.label;
    model.sendAddress = kWalletManager.currentWalletInfo.addr;
    model.rAddress = self.addressTextField.text;
    model.txType = @"";
    model.fee = [NSString stringWithFormat:@"%@ %@",[kWalletManager getFeeBaseWithSat:[dict safeStringForKey:@"fee"]],[kWalletManager currentBitcoinUnit]];
    sendVc.info = model;
    [sendVc showOnWindowWithParentViewController:self block:^(NSString * _Nonnull str) {
        if (kWalletManager.isOpenAuthBiological) {
            [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                if (state == YZAuthIDStateNotSupport
                    || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                    [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                        [weakself sendTxPwd:pwd dict:dict];
                    }];
                } else if (state == YZAuthIDStateSuccess) {
                    NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                    [weakself sendTxPwd:pwd dict:dict];
                }
            }];
        }else{
            [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                [weakself sendTxPwd:pwd dict:dict];
            }];
            
        }
    }];
}
- (void)sendTxPwd:(NSString *)pwd dict:(NSDictionary *)dict
{
    NSString *feerateTx = [dict safeStringForKey:@"tx"];
    NSDictionary *dict1 =  [kPyCommandsManager callInterface:kInterfaceMktx parameter:@{@"tx":feerateTx}];
    NSString *unSignStr = [dict1 safeStringForKey:@"tx"];
    NSString *tx = unSignStr;
    NSString *password = pwd;
    NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx,@"password":password}];
    NSString *signTx = [signTxDict safeStringForKey:@"tx"];
    [kPyCommandsManager callInterface:kInterfaceBroadcast_tx parameter:@{@"tx":signTx}];
    [kTools tipMessage:MyLocalizedString(@"Send a success", nil)];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
    [self.navigationController popViewControllerAnimated:YES];
}



- (void)loadFee
{
    [self loadZeroFee];
    [self loadReRecommendFee];
    [self loadFastFee];
}
- (void)loadFastFee
{
    NSString *status = [NSString stringWithFormat:@"%zd",[self.currentFee_status integerValue] * 2];
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    self.fastFeeDict = dict;
    
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.fiatFast =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":[kWalletManager getFeeBaseWithSat:feesat]}];
}

- (void)loadReRecommendFee
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":self.currentFee_status}];
    self.recommendFeeDict = dict;
    
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.fiatRecommend =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":[kWalletManager getFeeBaseWithSat:feesat]}];
}

- (void)loadZeroFee
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{self.addressTextField.text:self.amountTextField.text};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":@"1"}];
    self.lowFeeDict = dict;
    
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.fiatLow =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":[kWalletManager getFeeBaseWithSat:feesat]}];
}


- (IBAction)tapSlowBgClick:(UITapGestureRecognizer *)sender {
    if (self.currentFeeType != OKFeeTypeSlow) {
        [self changeFeeType:OKFeeTypeSlow];
    }
}

- (IBAction)tapRecommendBgClick:(UITapGestureRecognizer *)sender
{
    if (self.currentFeeType != OKFeeTypeRecommend) {
        [self changeFeeType:OKFeeTypeRecommend];
    }
}
- (IBAction)tapFastBgClick:(UITapGestureRecognizer *)sender
{
    if (self.currentFeeType != OKFeeTypeFast) {
        [self changeFeeType:OKFeeTypeFast];
    }
}

#pragma mark - OKFeeType
- (void)changeFeeType:(OKFeeType)feeType
{
    _currentFeeType = feeType;
    switch (_currentFeeType) {
        case OKFeeTypeSlow:
        {
            self.slowSelectBtn.hidden = NO;
            self.recommendSelectBtn.hidden = YES;
            self.fastSelectBtn.hidden = YES;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeRecommend:
        {
            self.slowSelectBtn.hidden = YES;
            self.recommendSelectBtn.hidden = NO;
            self.fastSelectBtn.hidden = YES;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeFast:
        {
            self.slowSelectBtn.hidden = YES;
            self.recommendSelectBtn.hidden = YES;
            self.fastSelectBtn.hidden = NO;
            [self.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [self.fastBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        default:
            break;
    }
}


#pragma mark - UITextFieldDelegate
- (void)textChange:(NSString *)str
{
    if (self.addressTextField.text.length > 0 && self.amountTextField.text.length > 0 && [self.amountTextField.text doubleValue] > 0 && [self.amountTextField.text doubleValue] <= [self.balanceLabel.text doubleValue]) {
        [self loadFee];
        [self refreshFeeSelect];
    }
}
- (IBAction)restoreDefaultOptionsBtnClick:(UIButton *)sender {
    _custom = NO;
    [self changUIForCustom];
}
@end
