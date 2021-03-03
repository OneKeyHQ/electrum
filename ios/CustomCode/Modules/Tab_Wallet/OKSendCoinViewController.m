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
#import "OKDefaultFeeInfoModel.h"
#import "OKDefaultFeeInfoSubModel.h"
#import "OKLookWalletTipsViewController.h"
#import "OKHwNotiManager.h"
#import "OKTransferCompleteController.h"
#import "OKTxDetailViewController.h"


@interface OKSendCoinViewController ()<UITextFieldDelegate,OKHwNotiManagerDelegate>
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

//当前选择的 慢、推荐、快类型
@property (nonatomic,assign)OKFeeType currentFeeType;

@property (nonatomic,copy)NSString *currentMemo;

//存储硬件签名数据
@property (nonatomic,strong)NSString *feeBit;
@property (nonatomic,strong)NSDictionary *hwPredata;
@property (nonatomic,strong)NSDictionary *hwSignData;
@property (nonatomic,copy)NSString *hwFiat;

@property (nonatomic,assign)BOOL isClickBiggest;
//存储最大选项的交易编码数据
@property (nonatomic,strong)NSDictionary *biggestFeeDict;
//存储慢、推荐、快、自定义的交易数据
@property (nonatomic,strong)NSDictionary *lowFeeDict;
@property (nonatomic,strong)NSDictionary *recommendFeeDict;
@property (nonatomic,strong)NSDictionary *fastFeeDict;
@property (nonatomic,strong)NSDictionary *customFeeDict;

//是否选择了自定义
@property (nonatomic,assign)BOOL custom;
//存储UI展示的慢、推荐、快数据
@property (nonatomic,strong)OKDefaultFeeInfoModel *defaultFeeInfoModel;
//存储UI展示的自定义数据
@property (nonatomic,strong)OKSendFeeModel *customFeeModel;

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
    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor blackColor]};
    _custom = NO;
    _isClickBiggest = NO;
    [self stupUI];
    [self changeFeeType:OKFeeTypeRecommend];
    [self getNoDataRates];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshBalance:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];
    self.addressTextField.text = self.address;
}

//获取没有输入内容时候的默认费率
- (void)getNoDataRates
{
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_default_fee_info parameter:@{@"coin":[self.coinType lowercaseString]}];
        if (dict == nil || dict.count == 0) {
            [kTools tipMessage:MyLocalizedString(@"Failed to get the rate", nil)];
            return;
        }
        self.defaultFeeInfoModel = [OKDefaultFeeInfoModel mj_objectWithKeyValues:dict];
        dispatch_async(dispatch_get_main_queue(), ^{
            //刷新默认fee值
            [self refreshFeeSelect];
        });
    });
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
    self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    self.slowTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);

    self.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
    self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    self.recommendTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);

    self.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
    self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    self.fastTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [self.coinTypeBtn setTitle:[kWalletManager getUnitForCoinType] forState:UIControlStateNormal];

    self.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
    self.customCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    self.customTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [self.coinTypeBtn setTitle:[kWalletManager getUnitForCoinType] forState:UIControlStateNormal];

    BOOL isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":kWalletManager.currentWalletInfo.name}] boolValue];
    if (!isBackUp) {
        OKWeakSelf(self)
        [kTools alertTips:MyLocalizedString(@"prompt", nil) desc:MyLocalizedString(@"The wallet has not been backed up. For the safety of your funds, please complete the backup before initiating the transfer using this address", nil) confirm:^{} cancel:^{
            [weakself.navigationController popViewControllerAnimated:YES];
        } vc:weakself conLabel:MyLocalizedString(@"I have known_alert", nil) isOneBtn:NO];
    }

    if ([kWalletManager getWalletDetailType] == OKWalletTypeObserve) {
        OKWeakSelf(self)
        [kTools alertTips:MyLocalizedString(@"prompt", nil) desc:MyLocalizedString(@"For the current purpose of observing the wallet, the initiated transfer shall be signed by scanning the code with the cold wallet holding the private key", nil) confirm:^{} cancel:^{
                       [weakself.navigationController popViewControllerAnimated:YES];
        } vc:self conLabel:MyLocalizedString(@"confirm", nil) isOneBtn:NO];
    }
    //刷新自定义界面
    [self changUIForCustom];
    //刷新确定按钮状态
    [self changeBtn];
}

/// 刷新余额
/// @param noti 通知
- (void)refreshBalance:(NSNotification *)noti
{
    NSDictionary *dict = noti.object;
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        self.balanceLabel.text =  [dict safeStringForKey:@"balance"];
        self.coinTypeLabel.text = [kWalletManager getUnitForCoinType];
    });
}

/// 刷新矿工费UI数据
- (void)refreshFeeSelect
{
    NSLog(@"refreshFeeSelect , %@",[self.defaultFeeInfoModel mj_JSONObject]);
    NSString *fiatS = kWalletManager.currentFiatSymbol;
    if (_custom) {
        if (self.customFeeModel == nil) {
            return;
        }
        self.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
        NSString *fee = self.customFeeModel.fee;
        if (fee == nil) {
            fee = @"--";
        }
        self.customCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fee,[kWalletManager getUnitForCoinType]];
        self.customTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),self.customFeeModel.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *fiat = [[self.customFeeModel.fiat componentsSeparatedByString:@" "] firstObject];
        self.customMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,fiat];
    }else{
        if (self.defaultFeeInfoModel.slow == nil || self.defaultFeeInfoModel.normal == nil || self.defaultFeeInfoModel.fast == nil) {
            return;
        }
        self.slowTitleLabel.text = MyLocalizedString(@"slow", nil);
        NSString *feeslow = self.defaultFeeInfoModel.slow.fee;
        if (feeslow == nil) {
            feeslow = @"-";
        }
        self.slowCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feeslow,[kWalletManager getUnitForCoinType]];
        self.slowTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),self.defaultFeeInfoModel.slow.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *slowfiat = [[self.defaultFeeInfoModel.slow.fiat componentsSeparatedByString:@" "] firstObject];
        self.slowMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,slowfiat];


        self.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
        NSString *feerecommend = self.defaultFeeInfoModel.normal.fee;
        if (feerecommend == nil) {
            feerecommend = @"-";
        }
        self.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feerecommend,[kWalletManager getUnitForCoinType]];
        self.recommendTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),self.defaultFeeInfoModel.normal.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *recommendfiat = [[self.defaultFeeInfoModel.normal.fiat componentsSeparatedByString:@" "] firstObject];
        self.recommendMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,recommendfiat];

        self.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
        NSString *feefast = self.defaultFeeInfoModel.fast.fee;
        if (feefast == nil) {
            feefast = @"-";
        }
        self.fastCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feefast,[kWalletManager getUnitForCoinType]];
        self.fastTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),self.defaultFeeInfoModel.fast.time?: @"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *fastfiat = [[self.defaultFeeInfoModel.fast.fiat componentsSeparatedByString:@" "] firstObject];
        self.fastMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,fastfiat];
    }
    [self changUIForCustom];
}
///刷新矿工费自定义UI数据
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
    if (self.balanceLabel.text.length == 0) {
        return;
    }
    NSString *fee = @"";
    if (self.custom) {
        fee = [self.customFeeDict safeStringForKey:@"fee"]?:self.customFeeModel.fee;
    }else{
        switch (self.currentFeeType) {
            case OKFeeTypeSlow:
                fee = [self.lowFeeDict safeStringForKey:@"fee"]?:self.defaultFeeInfoModel.slow.fee;
                break;
            case OKFeeTypeRecommend:
                fee = [self.recommendFeeDict safeStringForKey:@"fee"]?:self.defaultFeeInfoModel.normal.fee;
                break;
            case OKFeeTypeFast:
                fee = [self.fastFeeDict safeStringForKey:@"fee"]?:self.defaultFeeInfoModel.fast.fee;
                break;
            default:
                break;
        }
    }
    NSDecimalNumber *balanceNum = [NSDecimalNumber decimalNumberWithString:self.balanceLabel.text];
    NSDecimalNumber *feeNum = [NSDecimalNumber decimalNumberWithString:fee];
    NSDecimalNumber *resultNum = [balanceNum decimalNumberBySubtracting:feeNum];
    if ([resultNum compare:[NSDecimalNumber decimalNumberWithString:@"0"]] == NSOrderedAscending) {
        [kTools tipMessage:MyLocalizedString(@"Lack of balance", nil)];
        return;
    }
    NSString *biggestAmount = [NSString stringWithFormat:@"%@",resultNum.stringValue];
    self.amountTextField.text = biggestAmount;
    _isClickBiggest = YES;
    [self changeBtn];
}
- (IBAction)coinTypeBtnClick:(UIButton *)sender {

}
- (IBAction)customBtnClick:(UIButton *)sender {
    NSString *dsize = @"";
    NSString *feerate = @"";
    switch (self.currentFeeType) {
        case OKFeeTypeFast:
            feerate = self.defaultFeeInfoModel.fast.feerate;
            dsize = self.defaultFeeInfoModel.fast.size;
            break;
        case OKFeeTypeRecommend:
            feerate = self.defaultFeeInfoModel.normal.feerate;
            dsize = self.defaultFeeInfoModel.normal.size;
            break;
        case OKFeeTypeSlow:
            feerate = self.defaultFeeInfoModel.slow.feerate;
            dsize = self.defaultFeeInfoModel.slow.size;
            break;

        default:
            break;
    }
    NSString *lowest = self.defaultFeeInfoModel.slow.feerate;
    OKWeakSelf(self)
    [OKWalletInputFeeView showWalletCustomFeeDsize:dsize feerate:feerate lowfeerate:lowest coinType:self.coinType sure:^(NSDictionary *customFeeDict, NSString *fiat, NSString *feeBit) {
        weakself.customFeeDict = customFeeDict;
        weakself.customFeeModel = [OKSendFeeModel mj_objectWithKeyValues:customFeeDict];
        weakself.custom = YES;
        weakself.feeBit = feeBit;
        [weakself refreshFeeSelect];
        if (weakself.isClickBiggest) {
            [weakself moreBtnClick:nil];
        }
    } Cancel:nil];
}

- (void)changeBtn
{
    if (self.addressTextField.text.length > 0 && self.amountTextField.text.length > 0) {
        self.sendBtn.alpha = 1.0;
        self.sendBtn.userInteractionEnabled = YES;
    }else{
        self.sendBtn.alpha = 0.5;
        self.sendBtn.userInteractionEnabled = NO;
    }
}


- (BOOL)checkTextField:(BOOL)isShowTips
{
    if (self.addressTextField.text.length == 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Please enter the transfer address", nil)];
        }
        return NO;
    }

    if (isShowTips) {
        id result =  [kPyCommandsManager callInterface:kInterfaceverify_legality parameter:@{@"data":self.addressTextField.text,@"flag":@"address"}];
        if (result == nil) {
            return NO;
        }
    }

    if (self.amountTextField.text.length == 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Please enter the transfer amount", nil)];
        }
        return NO;
    }

    if ([self.amountTextField.text doubleValue] <= 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"The transfer amount cannot be zero", nil)];
        }
        return NO;
    }

    if ([self.balanceLabel.text doubleValue] < [self.amountTextField.text doubleValue]) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Lack of balance", nil)];
        }
        return NO;
    }

    if ([self.amountTextField.text doubleValue] < [[kWalletManager getFeeBaseWithSat:@"546"] doubleValue]) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"The minimum amount must not be less than 546 sat", nil)];
        }
        return NO;
    }
    return YES;
}
- (IBAction)sendBtnClick:(OKButton *)sender {
    if (![self checkTextField:YES]) {
        return;
    }
    if ([[self.coinType uppercaseString] isEqualToString:COIN_BTC]) {
        [self loadFee:^(void) {
            OKWeakSelf(self)
            __block  NSDictionary *dict = [NSDictionary dictionary];
            if (weakself.isClickBiggest) {
                dict = self.biggestFeeDict;
            }else{
                if (weakself.custom) {
                    dict = self.customFeeDict;
                }else{
                    switch (weakself.currentFeeType) {
                        case OKFeeTypeSlow:
                            dict = weakself.lowFeeDict;
                            break;
                        case OKFeeTypeRecommend:
                            dict = weakself.recommendFeeDict;
                            break;
                        case OKFeeTypeFast:
                            dict = weakself.fastFeeDict;
                            break;
                        default:
                            break;
                    }
                }
            }
            if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
                [MBProgressHUD showHUDAddedTo:self.view animated:YES];
                [OKHwNotiManager sharedInstance].delegate = self;
                self.hwPredata = dict;
                self.hwFiat = [self getCurrentFiat];
                NSString *feerateTx = [dict safeStringForKey:@"tx"];
                NSDictionary *dict1 =  [kPyCommandsManager callInterface:kInterfaceMktx parameter:@{@"tx":feerateTx}];
                NSString *unSignStr = [dict1 safeStringForKey:@"tx"];
                NSString *tx = unSignStr;
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx}];
                    if (signTxDict != nil) {
                        weakself.hwSignData = signTxDict;
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiHwBroadcastiComplete object:nil];
                    }
                });
                return;
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                [self showPreInfoView:dict fiat:[self getCurrentFiat]];
            });
        }];
    }else if ([[self.coinType uppercaseString] isEqualToString:COIN_ETH]){

    }
}
- (void)showPreInfoView:(NSDictionary *)dict fiat:(NSString *)fiat
{
    OKSendTxPreInfoViewController *sendVc = [OKSendTxPreInfoViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    OKSendTxPreModel *model = [OKSendTxPreModel new];
    NSString *amount = self.amountTextField.text;
    if (_isClickBiggest) {
        NSDecimalNumber *blance = [NSDecimalNumber decimalNumberWithString:self.balanceLabel.text];
        NSDecimalNumber *fee = [NSDecimalNumber decimalNumberWithString:[dict safeStringForKey:@"fee"]];
        amount = [[blance decimalNumberBySubtracting:fee] stringValue];
    }
    model.amount = amount;
    model.coinType = self.coinTypeLabel.text;
    model.walletName = kWalletManager.currentWalletInfo.label;
    model.sendAddress = kWalletManager.currentWalletInfo.addr;
    model.rAddress = self.addressTextField.text;
    model.txType = @"";
    model.fee = [NSString stringWithFormat:@"%@ %@ ≈ %@%@",[dict safeStringForKey:@"fee"],[kWalletManager currentBitcoinUnit],kWalletManager.currentFiatSymbol,fiat];
    sendVc.info = model;
    OKWeakSelf(self)
    [sendVc showOnWindowWithParentViewController:self block:^(NSString * _Nonnull str) {

        if ([kWalletManager getWalletDetailType] == OKWalletTypeObserve) {
            OKLookWalletTipsViewController *lookVc = [OKLookWalletTipsViewController lookWalletTipsViewController:[dict safeStringForKey:@"tx"]];
            lookVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [weakself.OK_TopViewController presentViewController:lookVc animated:NO completion:nil];
            return;
        }

        if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
            [weakself broadcast_tx:weakself.hwSignData dict:dict];
            return;
        }

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
    NSLog(@"pwd = %@ dict ==== %@",pwd,dict);
    NSString *feerateTx = [dict safeStringForKey:@"tx"];
    NSDictionary *dict1 =  [kPyCommandsManager callInterface:kInterfaceMktx parameter:@{@"tx":feerateTx}];
    NSString *unSignStr = [dict1 safeStringForKey:@"tx"];
    NSString *tx = unSignStr;
    NSString *password = pwd;
    NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx,@"password":password}];
    [self broadcast_tx:signTxDict dict:dict];
}
- (void)broadcast_tx:(NSDictionary *)signTxDict dict:(NSDictionary *)dict
{
    NSString *signTx = [signTxDict safeStringForKey:@"tx"];
    id result =  [kPyCommandsManager callInterface:kInterfaceBroadcast_tx parameter:@{@"tx":signTx}];
    if (result != nil) {
        OKWeakSelf(self)
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
            OKTransferCompleteController *transferCompleteVc = [OKTransferCompleteController transferCompleteController:dict block:^{
                OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
                txDetailVc.tx_hash = [signTxDict safeStringForKey:@"txid"];
                [weakself.navigationController pushViewController:txDetailVc animated:YES];
            }];
            [self.navigationController pushViewController:transferCompleteVc animated:YES];
        });
    }
}

#pragma mark - OKHwNotiManagerDelegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (type == OKHWNotiTypeSendCoinConfirm) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [MBProgressHUD hideHUDForView:self.view animated:YES];
            [self showPreInfoView:self.hwPredata fiat:self.hwFiat];
        });
    }else if(type == OKHWNotiTypePin_Current){
        dispatch_async(dispatch_get_main_queue(), ^{
            OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    id result = [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    if (result != nil) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [weakself.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{

                            }];
                        });
                        return;
                    }
                });
            }];
            [weakself.OK_TopViewController presentViewController:pinCodeVc animated:YES completion:nil];
        });
    }
}

- (void)loadFee:(void(^)(void))block
{
    NSString *address = [self.addressTextField.text copy];
    NSString *amount = [self.amountTextField.text copy];
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_t group = dispatch_group_create();
    dispatch_group_async(group, queue, ^{
        [self loadZeroFee:address amount:amount];
    });
    dispatch_group_async(group, queue, ^{
        [self loadReRecommendFee:address amount:amount];
    });
    dispatch_group_async(group, queue, ^{
        [self loadFastFee:address amount:amount];
    });
    if (_isClickBiggest) {
        dispatch_group_async(group, queue, ^{
            [self loadBiggestFeeDict:address];
        });
    }
    if (self.custom) {
        dispatch_group_async(group, queue, ^{
            [self loadCustomFee:address amount:amount];
        });
    }
    dispatch_group_notify(group,dispatch_get_main_queue(), ^{
        if (self.custom) {
            if (block && self.customFeeDict) {
                block();
            }
        }else{
            if (block && self.lowFeeDict && self.recommendFeeDict && self.fastFeeDict) {
                block();
            }
        }
    });
}
- (void)loadFastFee:(NSString *)address amount:(NSString *)amount
{
    NSString *status = [NSString stringWithFormat:@"%zd",[self.defaultFeeInfoModel.fast.feerate integerValue] * 2];
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    self.fastFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.defaultFeeInfoModel.fast.fee = feesat;
    self.defaultFeeInfoModel.fast.time = [dict safeStringForKey:@"time"];
    self.defaultFeeInfoModel.fast.size = [dict safeStringForKey:@"size"];
    self.defaultFeeInfoModel.fast.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];;
}

- (void)loadReRecommendFee:(NSString *)address amount:(NSString *)amount
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":self.defaultFeeInfoModel.normal.feerate}];
    self.recommendFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.defaultFeeInfoModel.normal.fee = feesat;
    self.defaultFeeInfoModel.normal.time = [dict safeStringForKey:@"time"];
    self.defaultFeeInfoModel.normal.size = [dict safeStringForKey:@"size"];
    self.defaultFeeInfoModel.normal.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];
}

- (void)loadZeroFee:(NSString *)address amount:(NSString *)amount
{
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":self.defaultFeeInfoModel.slow.feerate}];
    self.lowFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.defaultFeeInfoModel.slow.fee = feesat;
    self.defaultFeeInfoModel.slow.time = [dict safeStringForKey:@"time"];
    self.defaultFeeInfoModel.slow.size = [dict safeStringForKey:@"size"];
    self.defaultFeeInfoModel.slow.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];
}

- (void)loadCustomFee:(NSString *)address amount:(NSString *)amount
{
    NSString *status = self.feeBit;
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    self.customFeeDict = dict;
    if (dict == nil) {
        return;
    }
    self.customFeeModel = [OKSendFeeModel mj_objectWithKeyValues:dict];
    NSString *feesat = [dict safeStringForKey:@"fee"];
    self.customFeeModel.fee = feesat;
    self.customFeeModel.time = [dict safeStringForKey:@"time"];
    self.customFeeModel.size = [dict safeStringForKey:@"size"];
    self.customFeeModel.fiat = [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];;
}


- (void)loadBiggestFeeDict:(NSString *)address
{
    NSDictionary *outputsDict = @{address:@"!"};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSString *feerate = self.feeBit;
    if (!self.custom) {
        feerate = [self getCurrentFiat];
    }
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":feerate}];
    self.biggestFeeDict = dict;
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
    [self changeBtn];
    if (![self checkTextField:NO])
    {
        [self getNoDataRates];
        return;
    }
    [self loadFee:^(void) {
        [self refreshFeeSelect];
    }];
}
- (IBAction)restoreDefaultOptionsBtnClick:(UIButton *)sender {
    _custom = NO;
    [self moreBtnClick:nil];
    [self changUIForCustom];
}

- (void)backToPrevious
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    if ([self.navigationController.viewControllers indexOfObject:self]==NSNotFound)
    {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}



- (NSString *)getCurrentFiat
{
    NSString *fiat = @"";
    if (_custom) {
        fiat = self.customFeeModel.fiat;
    }else{
        switch (_currentFeeType) {
            case OKFeeTypeSlow:
            {
                fiat = self.defaultFeeInfoModel.slow.fiat;
            }
                break;
            case OKFeeTypeRecommend:
            {
                fiat = self.defaultFeeInfoModel.normal.fiat;
            }
                break;
            case OKFeeTypeFast:
            {
                fiat = self.defaultFeeInfoModel.fast.fiat;
            }
                break;
            default:
                fiat = self.defaultFeeInfoModel.normal.fiat;
                break;
        }
    }
    return fiat;
}
@end
