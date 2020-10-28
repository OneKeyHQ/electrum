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


- (IBAction)txHashBtnClick:(UIButton *)sender {
    NSLog(@"点击了交易号后面的浏览器链接");
}

- (IBAction)blockNumBtnClick:(UIButton *)sender {
    NSLog(@"点击了块高度");
}
@end
