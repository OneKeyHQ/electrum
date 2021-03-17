//
//  OKTxDetailViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTxDetailViewController.h"
#import "OKTxTableViewCellModel.h"

@interface OKTxDetailViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *statusIcon;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UIView *fromBg;
@property (weak, nonatomic) IBOutlet UILabel *fromTitleLabel;
@property (weak, nonatomic) IBOutlet UIView *fromAddressBg;
@property (weak, nonatomic) IBOutlet UIView *toBg;
@property (weak, nonatomic) IBOutlet UILabel *toTitleLabel;
@property (weak, nonatomic) IBOutlet UIView *toAddressBg;
@property (weak, nonatomic) IBOutlet TYAttributedLabel *fromAddressLabel;
@property (weak, nonatomic) IBOutlet TYAttributedLabel *toAddressLabel;
//Bottom
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel1;
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel2;
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel3;
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel4;
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel5;
@property (weak, nonatomic) IBOutlet UILabel *leftTitleLabel6;

@property (weak, nonatomic) IBOutlet UILabel *confirmNumLabel;
@property (weak, nonatomic) IBOutlet UILabel *blockNumLabel;
@property (weak, nonatomic) IBOutlet UILabel *txNumLabel;
@property (weak, nonatomic) IBOutlet UILabel *txDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *feeLabel;
@property (weak, nonatomic) IBOutlet UILabel *memoLabel;

- (IBAction)blockNumBtnClick:(UIButton *)sender;
- (IBAction)txHashBtnClick:(UIButton *)sender;
@property (nonatomic,strong)NSDictionary *txInfo;


@end

@implementation OKTxDetailViewController

+ (instancetype)txDetailViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKTxDetailViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"Transaction details", nil);
    [self stupUI];
    [self loadList];
}

- (void)stupUI
{
    self.fromTitleLabel.text = MyLocalizedString(@"The sender", nil);
    self.toTitleLabel.text = MyLocalizedString(@"The receiving party", nil);
    self.leftTitleLabel1.text = MyLocalizedString(@"Confirmation number", nil);
    self.leftTitleLabel2.text = MyLocalizedString(@"Block height", nil);
    self.leftTitleLabel3.text = MyLocalizedString(@"Transaction no", nil);
    self.leftTitleLabel4.text = MyLocalizedString(@"Trading hours", nil);
    self.leftTitleLabel5.text = MyLocalizedString(@"Miners fee", nil);
    self.leftTitleLabel6.text = MyLocalizedString(@"note", nil);
    [self.fromBg setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:20];
    [self.fromAddressBg setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:30];
    [self.toBg setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:20];
    [self.toAddressBg setLayerBoarderColor:HexColor(0xF2F2F2) width:1 radius:30];


    UITapGestureRecognizer *tapFrom = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapFrom)];
    [self.fromAddressLabel addGestureRecognizer:tapFrom];

    UITapGestureRecognizer *tapTo = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapTo)];
    [self.toAddressLabel addGestureRecognizer:tapTo];
}

- (void)loadList
{
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *txInfo =  [kPyCommandsManager callInterface:kInterfaceGet_tx_info parameter:@{@"tx_hash":self.tx_hash,@"coin":[kWalletManager.currentWalletInfo.coinType lowercaseString]}];
        self.txInfo = txInfo;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self refreshUI];
        });
    });
}

- (void)refreshUI
{
    NSString *amountStr = [[self.model.amount componentsSeparatedByString:@" ("]firstObject];
    if ([amountStr doubleValue] <= 0) {
        self.amountLabel.text = amountStr;
    }else{
        self.amountLabel.text = [self.model.is_mine integerValue] == NO ? [NSString stringWithFormat:@"+%@",amountStr] : [NSString stringWithFormat:@"-%@",amountStr];
    }
    self.statusLabel.text = [self getStatusLabel:[self.txInfo safeStringForKey:@"tx_status"]];
    self.txNumLabel.text = [self.txInfo safeStringForKey:@"txid"];

    NSString *coinType = [kWalletManager.currentWalletInfo.coinType uppercaseString];
    if ([coinType isEqualToString:COIN_BTC]) {
        NSArray *input_addr_array = self.txInfo[@"input_addr"];
        NSDictionary *input_addr_dict = [input_addr_array firstObject];
        [self.fromAddressLabel appendText:[input_addr_dict safeStringForKey:@"address"]];
        [self.fromAddressLabel appendImage:[UIImage imageNamed:@"copy_small"]];

        NSArray *output_addr_array = self.txInfo[@"output_addr"];
        NSDictionary *output_addr_dict = [output_addr_array firstObject];
        [self.toAddressLabel appendText:[output_addr_dict safeStringForKey:@"addr"]];
        [self.toAddressLabel appendImage:[UIImage imageNamed:@"copy_small"]];

    }else if ([kWalletManager isETHClassification:coinType]){
        NSDictionary *tx = [self.txInfo[@"tx"] mj_JSONObject];

        NSArray *vinArray =  tx[@"vin"];
        NSDictionary *vin = [vinArray firstObject];
        NSArray *vinaddress = vin[@"addresses"];
        [self.fromAddressLabel appendText:[vinaddress firstObject]];
        [self.fromAddressLabel appendImage:[UIImage imageNamed:@"copy_small"]];

        NSArray *tokenTransfersArray = [tx[@"tokenTransfers"] mj_JSONObject];
        if (tokenTransfersArray.count == 0 || tokenTransfersArray == nil) {
            NSArray *voutArray =  tx[@"vout"];
            NSDictionary *vout = [voutArray firstObject];
            NSArray *voutaddress = vout[@"addresses"];
            [self.toAddressLabel appendText:[voutaddress firstObject]];
            [self.toAddressLabel appendImage:[UIImage imageNamed:@"copy_small"]];
        }else{
            NSDictionary *tokenInfo = [tokenTransfersArray firstObject];
            [self.toAddressLabel appendText:tokenInfo[@"to"]];
            [self.toAddressLabel appendImage:[UIImage imageNamed:@"copy_small"]];
        }
    }
    self.blockNumLabel.text = [self.txInfo safeStringForKey:@"height"];
    NSString *feeresult = [self.txInfo safeStringForKey:@"fee"];
    if ([feeresult containsString:@"("]) {
        NSArray *feeArray = [feeresult componentsSeparatedByString:@"("];
        self.feeLabel.text = [feeArray firstObject];
    }else{
        self.feeLabel.text = feeresult;
    }
    NSString *memo = [self.txInfo safeStringForKey:@"description"];
    self.memoLabel.text = memo.length == 0 ? MyLocalizedString(@"There is no", nil):memo;
    self.txDateLabel.text = self.txDate;
    NSString *confirmationNum = @"--";
    NSArray *strlist =  [[self.txInfo safeStringForKey:@"tx_status"] componentsSeparatedByString:@" "];
    confirmationNum = [strlist firstObject];
    self.confirmNumLabel.text = confirmationNum;
}

- (void)tapFrom
{
    [kTools pasteboardCopyString:self.fromAddressLabel.text msg:MyLocalizedString(@"Copied", nil)];
}
- (void)tapTo
{
    [kTools pasteboardCopyString:self.toAddressLabel.text msg:MyLocalizedString(@"Copied", nil)];
}

- (NSString *)getStatusLabel:(NSString *)status
{
    //Unconfirmed 未确认 3 confirmations已确认 Signed签名完成 Partially signed 2/3部分签名 Unsigned 未签名
    if ([status containsString:@"Unconfirmed"]) {
        return MyLocalizedString(@"unconfirmed", nil);
    }else if ([status containsString:@"confirmations"]){
        return MyLocalizedString(@"confirmations", nil);
    }else if ([status containsString:@"Signed"]){
        return MyLocalizedString(@"Signed", nil);
    }else if ([status containsString:@"Partially signed"]){
        return MyLocalizedString(@"Partially signed", nil);
    }else if ([status containsString:@"Unsigned"]){
        return MyLocalizedString(@"Unsigned", nil);
    }else{
        return @"";
    }
}


- (IBAction)txHashBtnClick:(UIButton *)sender {
    [self urlToWeb];
}
- (IBAction)blockNumBtnClick:(UIButton *)sender {
    [self urlToWeb];
}
- (void)urlToWeb
{
    NSString *txId = [self.txInfo safeStringForKey:@"txid"];
    NSString *url = [NSString stringWithFormat:@"%@%@",kUserSettingManager.currentBtcBrowser,txId];
    if ([kWalletManager isETHClassification:kWalletManager.currentWalletInfo.coinType]) {
        url = [NSString stringWithFormat:@"%@%@",kUserSettingManager.currentEthBrowser,txId];
    }
    WebViewVC *vc = [WebViewVC loadWebViewControllerWithTitle:nil url:url];
    [self.navigationController pushViewController:vc animated:YES];
}
@end
