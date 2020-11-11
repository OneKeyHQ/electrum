//
//  OKTxDetailViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTxDetailViewController.h"

@interface OKTxDetailViewController ()
@property (weak, nonatomic) IBOutlet UIImageView *statusIcon;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;

@property (weak, nonatomic) IBOutlet UIView *fromBg;
@property (weak, nonatomic) IBOutlet UILabel *fromTitleLabel;
@property (weak, nonatomic) IBOutlet UIView *fromAddressBg;
@property (weak, nonatomic) IBOutlet UILabel *fromAddressLabel;

@property (weak, nonatomic) IBOutlet UIView *toBg;
@property (weak, nonatomic) IBOutlet UILabel *toTitleLabel;
@property (weak, nonatomic) IBOutlet UIView *toAddressBg;
@property (weak, nonatomic) IBOutlet UILabel *toAddressLabel;



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
    
}

- (void)loadList
{
    NSDictionary *txInfo =  [kPyCommandsManager callInterface:kInterfaceGet_tx_info parameter:@{@"tx_hash":self.tx_hash}];
    NSLog(@"txInfo == %@",txInfo);
    self.txInfo = txInfo;
    [self refreshUI];
}

/**
 {
     amount = "100 mBTC";
     "can_broadcast" = 0;
     cosigner =     (
         vpub5YAZjbw8qrbTnhYd3e1QUT4Wc6Fc9gcQWRii69VKiiXkf61fWJe2qmNP7y7Vx6jRyV4R3ysxjLp1izrNNsrYdYwKMpeXcgBSvMp2z8X597g
     );
     description = "";
     fee = "unknown mBTC";
     height = 3885;
     "input_addr" =     (
     );
     "output_addr" =     (
                 {
             addr = bcrt1qzg78dz3hg0p7lytecsyv82g58htckx6zyfdtgc;
             amount = "100 mBTC";
             "is_change" = 0;
         },
                 {
             addr = bcrt1q04l7vrcqy4qd62gjdunasw49jsthvpphdmyhm4;
             amount = "10267.78392 mBTC";
             "is_change" = 1;
         }
     );
     "sign_status" =     (
         1,
         1
     );
     tx = 02000000000101f21b0d7ab6072c08190d061ba04f4ab6bf5232e08e2b12b89d36371cf0ec39590100000000feffffff028096980000000000160014123c768a3743c3ef9179c408c3a9143dd78b1b421865333d000000001600147d7fe60f002540dd29126f27d83aa594177604370247304402203a1e2385648c73814b33afa67393bae21c0e97e875afa4aef393fc0546cb11370220439863002e7d3e998fd40f0adcc0a6b95e5842b9d6cc1f78c8a2801a1b124d310121036425503cfb0baa6f2d9523469295d11ad2b1485c838781866bf495a4511c627400000000;
     "tx_status" = "10 confirmations";
     txid = d6764d84389e5015e1008e23469f81b9f5bb1446f02d4cb18351e9bbf45e019f;
 }
 */
- (void)refreshUI
{
    self.amountLabel.text = [self.txInfo safeStringForKey:@"amount"];
    self.statusLabel.text = [self getStatusLabel:[self.txInfo safeStringForKey:@"tx_status"]];
    self.tx_hash = [self.txInfo safeStringForKey:@"txid"];
    NSArray *output_addr_array = self.txInfo[@"output_addr"];
    NSDictionary *output_addr_dict = [output_addr_array firstObject];
    self.fromAddressLabel.text = kWalletManager.currentWalletAddress;
    self.toAddressLabel.text  = [output_addr_dict safeStringForKey:@"addr"];
    self.blockNumLabel.text = [self.txInfo safeStringForKey:@"height"];
    self.feeLabel.text = [self.txInfo safeStringForKey:@"fee"];
    self.memoLabel.text = [self.txInfo safeStringForKey:@"description"];
    self.txDateLabel.text = self.txDate;
    NSString *confirmationNum = @"--";
    if ([[self.txInfo safeStringForKey:@"tx_status"]containsString:@"confirmations"]) {
        NSArray *strlist =  [[self.txInfo safeStringForKey:@"tx_status"] componentsSeparatedByString:@" "];
        confirmationNum = [strlist firstObject];
    }
    self.confirmNumLabel.text = confirmationNum;
}

- (NSString *)getStatusLabel:(NSString *)status
{
    //Unconfirmed 未确认 3 confirmations已确认 Signed签名完成 Partially signed 2/3部分签名 Unsigned 未签名
    if ([status containsString:@"Unconfirmed"]) {
        return MyLocalizedString(@"未确认", nil);
    }else if ([status containsString:@"confirmations"]){
        return MyLocalizedString(@"已确认", nil);
    }else if ([status containsString:@"Signed"]){
        return MyLocalizedString(@"签名完成", nil);
    }else if ([status containsString:@"Partially signed"]){
        return MyLocalizedString(@"部分签名", nil);
    }else if ([status containsString:@"Unsigned"]){
        return MyLocalizedString(@"未签名", nil);
    }else{
        return @"";
    }
}


- (IBAction)txHashBtnClick:(UIButton *)sender {
    [kTools tipMessage:@"跳转到浏览器"];;
}

- (IBAction)blockNumBtnClick:(UIButton *)sender {
    [kTools tipMessage:@"跳转到浏览器"];
}
@end
