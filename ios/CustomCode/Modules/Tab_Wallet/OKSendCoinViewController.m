//
//  OKSendCoinViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//


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
#import "OKTokenSelectController.h"


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
@property (weak, nonatomic) IBOutlet OKButton *coinTypeBtn;
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
@property (nonatomic,strong)NSTimer* ethTimer;

@property (nonatomic,strong)OKSendTxPreInfoViewController *sendTxPreInfoVc;
@property (nonatomic,strong)NSArray *tokensArray;
@end

@implementation OKSendCoinViewController

+ (instancetype)sendCoinViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSendCoinViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    OKWeakSelf(self)
    [weakself setNavigationBarBackgroundColorWithClearColor];
    weakself.title = MyLocalizedString(@"transfer", nil);
    weakself.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor blackColor]};
    _custom = NO;
    _isClickBiggest = NO;
    [weakself stupUI];
    [weakself changeFeeType:OKFeeTypeRecommend];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshBalance:) name:kNotiUpdate_status object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChange:) name:UITextFieldTextDidChangeNotification object:nil];
    weakself.addressTextField.text = weakself.address;

    if ([[weakself.coinType uppercaseString] isEqualToString:COIN_ETH]) {
        weakself.ethTimer = [NSTimer scheduledTimerWithTimeInterval:30.0 TimerDo:^{
            [weakself getNoDataRates];
        }];
        [weakself.ethTimer fire];
    }else{
        [weakself getNoDataRates];
    }
}

#pragma mark - 获取 BTC默认费率 ETH实时费率
- (void)getNoDataRates
{
    OKWeakSelf(self)
    NSString *address = [weakself.addressTextField.text copy];
    NSString *amount = [weakself.amountTextField.text copy];
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *paramter = @{@"coin":[weakself.coinType lowercaseString]};
        if ([weakself.coinType isEqualToString:COIN_ETH] && address.length > 0 && amount.length > 0) {
            NSDictionary *eth_tx_info = @{@"to_address":address,@"value":amount};
            paramter = @{@"coin":[weakself.coinType lowercaseString],@"eth_tx_info":[eth_tx_info mj_JSONString]};
        }
        NSDictionary *dict = [kPyCommandsManager callInterface:kInterfaceget_default_fee_info parameter:paramter];
        if (dict == nil || dict.count == 0) {
            [kTools tipMessage:MyLocalizedString(@"Failed to get the rate", nil)];
            return;
        }
        weakself.defaultFeeInfoModel = [OKDefaultFeeInfoModel mj_objectWithKeyValues:dict];
        dispatch_async(dispatch_get_main_queue(), ^{
            //刷新默认fee值
            [weakself refreshFeeSelect];
        });
    });
}
#pragma mark - 初始化UI
- (void)stupUI
{
    OKWeakSelf(self)
    [weakself.shoukuanLabelBg setLayerRadius:12];
    [weakself.amountBg setLayerRadius:12];
    [weakself.feeLabelBg setLayerRadius:12];
    [weakself.moreBtn setLayerRadius:8];
    [weakself.moreBtn setBackgroundColor:RGBA(196, 196, 196, 0.2)];
    [weakself.feeTypeBgView setLayerBoarderColor:HexColor(0xE5E5E5) width:1 radius:20];
    [weakself.slowBottomLabelBg setLayerRadius:20];
    [weakself.recommendBottomLabelBg setLayerRadius:20];
    [weakself.fastBottomLabelBg setLayerRadius:20];
    [weakself.customBottomLabelBg setLayerRadius:20];
    [weakself.custom_BGView shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
    [weakself.sendBtn setLayerDefaultRadius];
    [weakself.coinTypeBtn status:OKButtonStatusDisabled];

    weakself.slowTitleLabel.text = MyLocalizedString(@"slow", nil);
    weakself.slowCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    weakself.slowTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);

    weakself.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
    weakself.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    weakself.recommendTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);

    weakself.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
    weakself.fastCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    weakself.fastTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [weakself.coinTypeBtn setTitle:[kWalletManager getUnitForCoinType] forState:UIControlStateNormal];

    weakself.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
    weakself.customCoinAmountLabel.text = [NSString stringWithFormat:@"0 %@",[kWalletManager getUnitForCoinType]];
    weakself.customTimeLabel.text = MyLocalizedString(@"About 0 minutes", nil);
    [weakself.coinTypeBtn setTitle:[kWalletManager getUnitForCoinType] forState:UIControlStateNormal];

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
    [weakself changUIForCustom];
    //刷新确定按钮状态
    [weakself changeBtn];
}

#pragma mark - 刷新余额
- (void)refreshBalance:(NSNotification *)noti
{
    OKWeakSelf(self)
    NSDictionary *dict = noti.object;
    weakself.tokensArray = [dict objectForKey:@"tokens"];
    dispatch_async(dispatch_get_main_queue(), ^{
        // UI更新代码
        [weakself.coinTypeBtn status:OKButtonStatusEnabled];
        weakself.balanceLabel.text =  [dict safeStringForKey:@"balance"];
        weakself.coinTypeLabel.text = [kWalletManager getUnitForCoinType];
    });
}

#pragma mark - 刷新矿工费UI数据
- (void)refreshFeeSelect
{
    OKWeakSelf(self)
    NSLog(@"refreshFeeSelect , %@",[weakself.defaultFeeInfoModel mj_JSONObject]);
    NSString *fiatS = kWalletManager.currentFiatSymbol;
    if (_custom) {
        if (weakself.customFeeModel == nil) {
            return;
        }
        weakself.customTitleLabel.text = MyLocalizedString(@"The custom", nil);
        NSString *fee = weakself.customFeeModel.fee;
        if (fee == nil) {
            fee = @"--";
        }
        weakself.customCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fee,[kWalletManager getUnitForCoinType]];
        weakself.customTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),weakself.customFeeModel.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *fiat = [[weakself.customFeeModel.fiat componentsSeparatedByString:@" "] firstObject];
        weakself.customMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,fiat];
    }else{
        if (weakself.defaultFeeInfoModel.slow == nil || weakself.defaultFeeInfoModel.normal == nil || weakself.defaultFeeInfoModel.fast == nil) {
            return;
        }
        weakself.slowTitleLabel.text = MyLocalizedString(@"slow", nil);
        NSString *feeslow = weakself.defaultFeeInfoModel.slow.fee;
        if (feeslow == nil) {
            feeslow = @"-";
        }
        weakself.slowCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feeslow,[kWalletManager getUnitForCoinType]];
        weakself.slowTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),weakself.defaultFeeInfoModel.slow.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *slowfiat = [[weakself.defaultFeeInfoModel.slow.fiat componentsSeparatedByString:@" "] firstObject];
        weakself.slowMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,slowfiat];


        weakself.recommendTitleLabel.text = MyLocalizedString(@"recommended", nil);
        NSString *feerecommend = weakself.defaultFeeInfoModel.normal.fee;
        if (feerecommend == nil) {
            feerecommend = @"-";
        }
        weakself.recommendCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feerecommend,[kWalletManager getUnitForCoinType]];
        weakself.recommendTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),weakself.defaultFeeInfoModel.normal.time?:@"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *recommendfiat = [[weakself.defaultFeeInfoModel.normal.fiat componentsSeparatedByString:@" "] firstObject];
        weakself.recommendMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,recommendfiat];

        weakself.fastTitleLabel.text = MyLocalizedString(@"fast", nil);
        NSString *feefast = weakself.defaultFeeInfoModel.fast.fee;
        if (feefast == nil) {
            feefast = @"-";
        }
        weakself.fastCoinAmountLabel.text = [NSString stringWithFormat:@"%@ %@",feefast,[kWalletManager getUnitForCoinType]];
        weakself.fastTimeLabel.text = [NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"sendcoin.about", nil),weakself.defaultFeeInfoModel.fast.time?: @"--",MyLocalizedString(@"sendcoin.minutes", nil)];
        NSString *fastfiat = [[weakself.defaultFeeInfoModel.fast.fiat componentsSeparatedByString:@" "] firstObject];
        weakself.fastMoneyAmountLabel.text = [NSString stringWithFormat:@"%@ %@",fiatS,fastfiat];
    }
    [weakself changUIForCustom];
}

#pragma mark - 刷新自定义矿工费UI数据
- (void)changUIForCustom
{
    OKWeakSelf(self)
    weakself.custom_BGView.hidden = !_custom;
    weakself.slowBg.hidden = _custom;
    weakself.fastBg.hidden = _custom;
    weakself.recommendedBg.hidden = _custom;
    weakself.restoreDefaultBgView.hidden = !_custom;
    if (_custom && [weakself.coinType isEqualToString:COIN_ETH]) {
        [weakself.ethTimer standBy];
    }else{
        [weakself.ethTimer resume];
    }
}
#pragma mark - 地址簿
//（暂时隐藏了）
- (IBAction)addressbookBtnClick:(UIButton *)sender {

}

#pragma mark - 最大 按钮被点击
- (IBAction)moreBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    if (![weakself checkAddress:YES]) {
        return;
    }
    NSString *address = [weakself.addressTextField.text copy];
    NSString *fee = @"";
    if ([weakself.coinType isEqualToString:COIN_BTC]) {
        [weakself loadBiggestFeeDict:address];
        fee = [weakself.biggestFeeDict safeStringForKey:@"fee"];

    }else if([weakself.coinType isEqualToString:COIN_ETH]){
        fee = [weakself getCurrentDefaultFee];
    }
    if (fee.length == 0 || fee == nil) {
        [kTools tipMessage:MyLocalizedString(@"Failed to get the rate", nil)];
        return;
    }
    NSDecimalNumber *balanceNum = [NSDecimalNumber decimalNumberWithString:weakself.balanceLabel.text];
    NSDecimalNumber *feeNum = [NSDecimalNumber decimalNumberWithString:fee];
    NSDecimalNumber *resultNum = [balanceNum decimalNumberBySubtracting:feeNum];
    if ([resultNum compare:[NSDecimalNumber decimalNumberWithString:@"0"]] == NSOrderedAscending) {
        [kTools tipMessage:MyLocalizedString(@"Lack of balance", nil)];
        return;
    }
    NSString *biggestAmount = [NSString stringWithFormat:@"%@",resultNum.stringValue];
    weakself.amountTextField.text = biggestAmount;
    _isClickBiggest = YES;
    [weakself changeBtn];
}
#pragma mark - 币种类型按钮被点击
- (IBAction)coinTypeBtnClick:(UIButton *)sender {
    OKTokenSelectController *tokenSelectVc = [OKTokenSelectController controllerWithStoryboard];
    tokenSelectVc.data = self.tokensArray;
    tokenSelectVc.selectCallback = ^(OKAllAssetsCellModel * _Nonnull selected) {

    };
    [self.navigationController pushViewController:tokenSelectVc animated:YES];
}
#pragma mark - 自定义 按钮被点击
- (IBAction)customBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    [OKWalletInputFeeView showWalletCustomFeeModel:weakself.defaultFeeInfoModel feetype:weakself.currentFeeType coinType:weakself.coinType sure:^(OKWalletInputFeeViewCallBackModel *callBackModel) {
        weakself.customFeeDict = callBackModel.customFeeDict;
        weakself.customFeeModel = [OKSendFeeModel mj_objectWithKeyValues:callBackModel.customFeeDict];
        weakself.custom = YES;
        weakself.feeBit = callBackModel.feeTfStr;
        [weakself refreshFeeSelect];
        if (weakself.isClickBiggest) {
            [weakself moreBtnClick:nil];
        }
    } Cancel:nil];
}
#pragma mark - 刷新 确认按钮
- (void)changeBtn
{
    OKWeakSelf(self)
    if (weakself.addressTextField.text.length > 0 && weakself.amountTextField.text.length > 0) {
        weakself.sendBtn.alpha = 1.0;
        weakself.sendBtn.userInteractionEnabled = YES;
    }else{
        weakself.sendBtn.alpha = 0.5;
        weakself.sendBtn.userInteractionEnabled = NO;
    }
}

#pragma mark - 检查文本框合法性
- (BOOL)checkTextField:(BOOL)isShowTips
{
    OKWeakSelf(self)
    if (![weakself checkAddress:isShowTips]) {
        return NO;
    }

    if (weakself.amountTextField.text.length == 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Please enter the transfer amount", nil)];
        }
        return NO;
    }

    if ([weakself.amountTextField.text doubleValue] <= 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"The transfer amount cannot be zero", nil)];
        }
        return NO;
    }

    if ([weakself.balanceLabel.text doubleValue] < [weakself.amountTextField.text doubleValue]) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Lack of balance", nil)];
        }
        return NO;
    }

    if ([weakself.amountTextField.text doubleValue] < [[kWalletManager getFeeBaseWithSat:@"546"] doubleValue]) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"The minimum amount must not be less than 546 sat", nil)];
        }
        return NO;
    }
    return YES;
}
#pragma mark - 检查地址合法性
- (BOOL)checkAddress:(BOOL)isShowTips
{
    OKWeakSelf(self)
    if (weakself.addressTextField.text.length == 0) {
        if (isShowTips) {
            [kTools tipMessage:MyLocalizedString(@"Please enter the transfer address", nil)];
        }
        return NO;
    }

    if (isShowTips) {
        id result =  [kPyCommandsManager callInterface:kInterfaceverify_legality parameter:@{@"data":weakself.addressTextField.text,@"flag":@"address",@"coin":[weakself.coinType lowercaseString]}];
        if (result == nil) {
            return NO;
        }
    }
    return YES;
}
#pragma mark - 发送按钮
- (IBAction)sendBtnClick:(OKButton *)sender {
    OKWeakSelf(self)
    if (![weakself checkTextField:YES]) {
        return;
    }
    __block  NSDictionary *dict = [NSDictionary dictionary];
    [weakself loadFee:^{
        dict = [weakself getTxDict];
        if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
            [MBProgressHUD showHUDAddedTo:weakself.view animated:YES];
            [OKHwNotiManager sharedInstance].delegate = self;
            weakself.hwPredata = dict;
            weakself.hwFiat = [weakself getCurrentFiat];
            if ([self.coinType isEqualToString:COIN_BTC]) {
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
            }else{
                NSDictionary *paramter = @{
                    @"to_addr":weakself.addressTextField.text,
                    @"value":weakself.amountTextField.text,
                    @"gas_price":[dict safeStringForKey:@"gas_price"],
                    @"gas_limit":[dict safeStringForKey:@"gas_limit"],
                    @"path":kBluetooth_iOS
                };
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    id result =  [kPyCommandsManager callInterface:kInterfacesign_eth_tx parameter:paramter];
                    if (result != nil) {
                        OKWeakSelf(self)
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
                        dispatch_async(dispatch_get_main_queue(), ^{
                            if (weakself.sendTxPreInfoVc != nil) {
                                [weakself.sendTxPreInfoVc closeView];
                            }
                            OKTransferCompleteController *transferCompleteVc = [OKTransferCompleteController transferCompleteController:[paramter safeStringForKey:@"value"] block:^{
                                OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
                                txDetailVc.tx_hash = result;
                                [weakself.navigationController pushViewController:txDetailVc animated:YES];
                            }];
                            [weakself.navigationController pushViewController:transferCompleteVc animated:YES];
                        });
                    }
                });
                return;
            }
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself showPreInfoView:dict fiat:[weakself getCurrentFiat]];
        });
    }];
}
#pragma mark - 预览交易信息
- (void)showPreInfoView:(NSDictionary *)dict fiat:(NSString *)fiat
{
    OKWeakSelf(self)
    OKSendTxPreInfoViewController *sendVc = [OKSendTxPreInfoViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    weakself.sendTxPreInfoVc = sendVc;
    OKSendTxPreModel *model = [OKSendTxPreModel new];
    NSString *amount = weakself.amountTextField.text;
    if (_isClickBiggest) {
        NSDecimalNumber *blance = [NSDecimalNumber decimalNumberWithString:weakself.balanceLabel.text];
        NSDecimalNumber *fee = [NSDecimalNumber decimalNumberWithString:[dict safeStringForKey:@"fee"]];
        amount = [[blance decimalNumberBySubtracting:fee] stringValue];
    }
    model.amount = amount;
    model.coinType = weakself.coinTypeLabel.text;
    model.walletName = kWalletManager.currentWalletInfo.label;
    model.sendAddress = kWalletManager.currentWalletInfo.addr;
    model.rAddress = weakself.addressTextField.text;
    model.txType = @"";
    model.fee = [NSString stringWithFormat:@"%@ %@ ≈ %@%@",[dict safeStringForKey:@"fee"],[kWalletManager getUnitForCoinType],kWalletManager.currentFiatSymbol,fiat];
    sendVc.info = model;
    [sendVc showOnWindowWithParentViewController:self block:^(NSString * _Nonnull str) {

        if ([kWalletManager getWalletDetailType] == OKWalletTypeObserve) {
            OKLookWalletTipsViewController *lookVc = [OKLookWalletTipsViewController lookWalletTipsViewController:[dict safeStringForKey:@"tx"]];
            lookVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [weakself.OK_TopViewController presentViewController:lookVc animated:NO completion:nil];
            return;
        }

        if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
            [weakself broadcast_tx:weakself.hwSignData dict:[dict copy]];
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
    } cancle:^{
        [kPyCommandsManager callInterface:kInterface_set_user_cancel parameter:@{}];
        weakself.sendTxPreInfoVc = nil;
    }];
}
- (void)sendTxPwd:(NSString *)pwd dict:(NSDictionary *)dict
{
    OKWeakSelf(self)
    if ([weakself.coinType isEqualToString:COIN_BTC]) {
        NSString *feerateTx = [dict safeStringForKey:@"tx"];
        NSDictionary *dict1 =  [kPyCommandsManager callInterface:kInterfaceMktx parameter:@{@"tx":feerateTx}];
        NSString *unSignStr = [dict1 safeStringForKey:@"tx"];
        NSString *tx = unSignStr;
        NSString *password = pwd;
        NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx,@"password":password}];
        [weakself broadcast_tx:signTxDict dict:dict];
    }else if ([weakself.coinType isEqualToString:COIN_ETH]){
        NSDictionary *paramter = @{
            @"to_addr":weakself.addressTextField.text,
            @"value":weakself.amountTextField.text,
            @"password":pwd,
            @"gas_price":[dict safeStringForKey:@"gas_price"],
            @"gas_limit":[dict safeStringForKey:@"gas_limit"],
        };
        id result =  [kPyCommandsManager callInterface:kInterfacesign_eth_tx parameter:paramter];
        if (result != nil) {
            OKWeakSelf(self)
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
            dispatch_async(dispatch_get_main_queue(), ^{
                if (weakself.sendTxPreInfoVc != nil) {
                    [weakself.sendTxPreInfoVc closeView];
                }
                OKTransferCompleteController *transferCompleteVc = [OKTransferCompleteController transferCompleteController:[paramter safeStringForKey:@"value"] block:^{
                    OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
                    txDetailVc.tx_hash = result;
                    [weakself.navigationController pushViewController:txDetailVc animated:YES];
                }];
                [weakself.navigationController pushViewController:transferCompleteVc animated:YES];
            });
        }
    }
}

#pragma mark - 广播交易接口  （BTC会用到）
- (void)broadcast_tx:(NSDictionary *)signTxDict dict:(NSDictionary *)dict
{
    NSString *signTx = [signTxDict safeStringForKey:@"tx"];
    id result =  [kPyCommandsManager callInterface:kInterfaceBroadcast_tx parameter:@{@"tx":signTx}];
    if (result != nil) {
        OKWeakSelf(self)
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
            OKTransferCompleteController *transferCompleteVc = [OKTransferCompleteController transferCompleteController:[dict safeStringForKey:@"amount"] block:^{
                OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
                txDetailVc.tx_hash = [signTxDict safeStringForKey:@"txid"];
                [weakself.navigationController pushViewController:txDetailVc animated:YES];
            }];
            [weakself.navigationController pushViewController:transferCompleteVc animated:YES];
        });
    }
}

#pragma mark - OKHwNotiManagerDelegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (type == OKHWNotiTypeSendCoinConfirm && weakself.sendTxPreInfoVc == nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [MBProgressHUD hideHUDForView:weakself.view animated:YES];
            [weakself showPreInfoView:weakself.hwPredata fiat:weakself.hwFiat];
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

#pragma mark - 加载矿工费（BTC）
- (void)loadFee:(void(^)(void))block
{
    if ([self.coinType isEqualToString:COIN_ETH]) {
        block();
        return;
    }

    OKWeakSelf(self)
    NSString *address = [weakself.addressTextField.text copy];
    NSString *amount = [weakself.amountTextField.text copy];

    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_t group = dispatch_group_create();
    if (weakself.custom) {
        dispatch_group_async(group, queue, ^{
            [weakself loadCustomFee:address amount:amount];
        });
    }else{
        dispatch_group_async(group, queue, ^{
            [weakself loadZeroFee:address amount:amount];
        });
        dispatch_group_async(group, queue, ^{
            [weakself loadReRecommendFee:address amount:amount];
        });
        dispatch_group_async(group, queue, ^{
            [weakself loadFastFee:address amount:amount];
        });
    }
    dispatch_group_notify(group,dispatch_get_main_queue(), ^{
        if (weakself.custom) {
            if (block && weakself.customFeeDict) {
                block();
            }
        }else{
            if (block && weakself.lowFeeDict && weakself.recommendFeeDict && weakself.fastFeeDict) {
                block();
            }
        }
    });
}
#pragma mark - 加载快矿工费（BTC）
- (void)loadFastFee:(NSString *)address amount:(NSString *)amount
{
    OKWeakSelf(self)
    NSString *status = [NSString stringWithFormat:@"%zd",[weakself.defaultFeeInfoModel.fast.feerate integerValue] * 2];
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    weakself.fastFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    weakself.defaultFeeInfoModel.fast.fee = feesat;
    weakself.defaultFeeInfoModel.fast.time = [dict safeStringForKey:@"time"];
    weakself.defaultFeeInfoModel.fast.size = [dict safeStringForKey:@"size"];
    weakself.defaultFeeInfoModel.fast.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];;
}
#pragma mark - 加载推荐矿工费（BTC）
- (void)loadReRecommendFee:(NSString *)address amount:(NSString *)amount
{
    OKWeakSelf(self)
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":weakself.defaultFeeInfoModel.normal.feerate}];
    weakself.recommendFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    weakself.defaultFeeInfoModel.normal.fee = feesat;
    weakself.defaultFeeInfoModel.normal.time = [dict safeStringForKey:@"time"];
    weakself.defaultFeeInfoModel.normal.size = [dict safeStringForKey:@"size"];
    weakself.defaultFeeInfoModel.normal.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];
}
#pragma mark - 加载慢矿工费（BTC）
- (void)loadZeroFee:(NSString *)address amount:(NSString *)amount
{
    OKWeakSelf(self)
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":weakself.defaultFeeInfoModel.slow.feerate}];
    weakself.lowFeeDict = dict;
    if (dict == nil) {
        return;
    }
    NSString *feesat = [dict safeStringForKey:@"fee"];
    weakself.defaultFeeInfoModel.slow.fee = feesat;
    weakself.defaultFeeInfoModel.slow.time = [dict safeStringForKey:@"time"];
    weakself.defaultFeeInfoModel.slow.size = [dict safeStringForKey:@"size"];
    weakself.defaultFeeInfoModel.slow.fiat =  [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];
}
#pragma mark - 加载自定义矿工费（BTC）
- (void)loadCustomFee:(NSString *)address amount:(NSString *)amount
{
    OKWeakSelf(self)
    NSString *status = weakself.feeBit;
    //输入地址和转账额度 获取fee
    NSDictionary *outputsDict = @{address:amount};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":status}];
    weakself.customFeeDict = dict;
    if (dict == nil) {
        return;
    }
    weakself.customFeeModel = [OKSendFeeModel mj_objectWithKeyValues:dict];
    NSString *feesat = [dict safeStringForKey:@"fee"];
    weakself.customFeeModel.fee = feesat;
    weakself.customFeeModel.time = [dict safeStringForKey:@"time"];
    weakself.customFeeModel.size = [dict safeStringForKey:@"size"];
    weakself.customFeeModel.fiat = [kPyCommandsManager callInterface:kInterfaceget_exchange_currency parameter:@{@"type":kExchange_currencyTypeBase,@"amount":feesat == nil ? @"0":feesat}];;
}

#pragma mark - 加载最大矿工费（BTC）
- (void)loadBiggestFeeDict:(NSString *)address
{
    OKWeakSelf(self)
    NSDictionary *outputsDict = @{address:@"!"};
    NSArray *outputsArray = @[outputsDict];
    NSString *outputs = [outputsArray mj_JSONString];
    NSString *memo = @"";
    NSString *feerate = weakself.feeBit;
    if (!weakself.custom) {
        feerate = [weakself getCurrentFiat];
    }
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceGet_fee_by_feerate parameter:@{@"outputs":outputs,@"message":memo,@"feerate":feerate}];
    weakself.biggestFeeDict = dict;
}

- (IBAction)tapSlowBgClick:(UITapGestureRecognizer *)sender {
    OKWeakSelf(self)
    if (weakself.currentFeeType != OKFeeTypeSlow) {
        [weakself changeFeeType:OKFeeTypeSlow];
        if (_isClickBiggest) {
            [weakself moreBtnClick:nil];
        }
    }
}

- (IBAction)tapRecommendBgClick:(UITapGestureRecognizer *)sender
{
    OKWeakSelf(self)
    if (weakself.currentFeeType != OKFeeTypeRecommend) {
        [weakself changeFeeType:OKFeeTypeRecommend];
        if (_isClickBiggest) {
            [weakself moreBtnClick:nil];
        }
    }
}
- (IBAction)tapFastBgClick:(UITapGestureRecognizer *)sender
{
    OKWeakSelf(self)
    if (weakself.currentFeeType != OKFeeTypeFast) {
        [weakself changeFeeType:OKFeeTypeFast];
        if (_isClickBiggest) {
            [weakself moreBtnClick:nil];
        }
    }
}

#pragma mark - OKFeeType
- (void)changeFeeType:(OKFeeType)feeType
{
    OKWeakSelf(self)
    _currentFeeType = feeType;
    switch (_currentFeeType) {
        case OKFeeTypeSlow:
        {
            weakself.slowSelectBtn.hidden = NO;
            weakself.recommendSelectBtn.hidden = YES;
            weakself.fastSelectBtn.hidden = YES;
            [weakself.slowBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeRecommend:
        {
            weakself.slowSelectBtn.hidden = YES;
            weakself.recommendSelectBtn.hidden = NO;
            weakself.fastSelectBtn.hidden = YES;
            [weakself.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.recommendedBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.fastBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        case OKFeeTypeFast:
        {
            weakself.slowSelectBtn.hidden = YES;
            weakself.recommendSelectBtn.hidden = YES;
            weakself.fastSelectBtn.hidden = NO;
            [weakself.slowBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.recommendedBg shadowWithLayerCornerRadius:20 borderColor:nil borderWidth:0 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
            [weakself.fastBg shadowWithLayerCornerRadius:20 borderColor:HexColor(RGB_THEME_GREEN) borderWidth:2 shadowColor:RGBA(0, 0, 0, 0.1) shadowOffset:CGSizeMake(0, 4) shadowOpacity:1 shadowRadius:10];
        }
            break;
        default:
            break;
    }
}

#pragma mark - UITextFieldDelegate
- (void)textChange:(NSString *)str
{
    OKWeakSelf(self)
    _isClickBiggest = NO;
    [weakself changeBtn];
    if (![weakself checkTextField:NO])
    {
        [weakself getNoDataRates];
        return;
    }
    if ([weakself.coinType isEqualToString:COIN_BTC]) {
        [weakself loadFee:^(void) {
            [weakself refreshFeeSelect];
        }];
    }
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
    if (self.ethTimer) {
        [self.ethTimer stop];
    }
}

#pragma mark - 获取当前的Fiat（BTC）
- (NSString *)getCurrentFiat
{
    OKWeakSelf(self)
    NSString *fiat = @"";
    if (_custom) {
        fiat = weakself.customFeeModel.fiat;
    }else{
        switch (_currentFeeType) {
            case OKFeeTypeSlow:
            {
                fiat = weakself.defaultFeeInfoModel.slow.fiat;
            }
                break;
            case OKFeeTypeRecommend:
            {
                fiat = weakself.defaultFeeInfoModel.normal.fiat;
            }
                break;
            case OKFeeTypeFast:
            {
                fiat = weakself.defaultFeeInfoModel.fast.fiat;
            }
                break;
            default:
                fiat = weakself.defaultFeeInfoModel.normal.fiat;
                break;
        }
    }
    return fiat;
}
#pragma mark - 获取当前的Fee（BTC）
- (NSString *)getCurrentDefaultFee
{
    OKWeakSelf(self)
    NSString *fee = @"";
    if (_custom) {
        fee = weakself.customFeeModel.fee;
    }else{
        switch (_currentFeeType) {
            case OKFeeTypeSlow:
            {
                fee = weakself.defaultFeeInfoModel.slow.fee;
            }
                break;
            case OKFeeTypeRecommend:
            {
                fee = weakself.defaultFeeInfoModel.normal.fee;
            }
                break;
            case OKFeeTypeFast:
            {
                fee = weakself.defaultFeeInfoModel.fast.fee;
            }
                break;
            default:
                fee = weakself.defaultFeeInfoModel.normal.fee;
                break;
        }
    }
    return fee;
}
#pragma mark - 获取当前的交易构造字典
- (NSDictionary *)getTxDict
{
    OKWeakSelf(self)
    NSDictionary *dict = [NSDictionary dictionary];
    if ([weakself.coinType isEqualToString:COIN_ETH]) {
        if (weakself.custom) {
            dict = [weakself.customFeeModel mj_JSONObject];
        }else{
            switch (weakself.currentFeeType) {
                case OKFeeTypeSlow:
                    dict = [weakself.defaultFeeInfoModel.slow mj_JSONObject];
                    break;
                case OKFeeTypeRecommend:
                    dict = [weakself.defaultFeeInfoModel.normal mj_JSONObject];
                    break;
                case OKFeeTypeFast:
                    dict = [weakself.defaultFeeInfoModel.fast mj_JSONObject];;
                    break;
                default:
                    break;
            }
        }
    }else if ([weakself.coinType isEqualToString:COIN_BTC]){
        if (weakself.isClickBiggest) {
            dict = weakself.biggestFeeDict;
        }else{
            if (weakself.custom) {
                dict = weakself.customFeeDict;
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
    }
    return dict;
}

- (NSString *)coinType
{
    return [_coinType uppercaseString];
}
- (UIColor *)navBarTintColor
{
    return UIColor.BG_W02;
}
@end
