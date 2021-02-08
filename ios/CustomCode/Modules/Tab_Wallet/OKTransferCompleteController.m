//
//  OKTransferCompleteController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTransferCompleteController.h"

@interface OKTransferCompleteController ()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UIView *showDetailBgView;
@property (weak, nonatomic) IBOutlet UILabel *showDetailLabel;
@property (weak, nonatomic) IBOutlet UIButton *completeBtn;
@property (nonatomic,strong)NSDictionary *dict;
@property (nonatomic,copy)ViewTxDetailsBlock block;
- (IBAction)completeBtnClick:(UIButton *)sender;

@end

@implementation OKTransferCompleteController
+ (instancetype)transferCompleteController:(NSDictionary *)dict block:(ViewTxDetailsBlock)block
{
    OKTransferCompleteController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKTransferCompleteController"];
    vc.dict = dict;
    vc.block = block;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"The deal", nil);
    self.statusLabel.text = MyLocalizedString(@"Trade Confirmation", nil);
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(viewTransactionDetails)];
    [self.showDetailBgView addGestureRecognizer:tap];
    [self.showDetailBgView setLayerRadius:15];
    self.showDetailLabel.text = MyLocalizedString(@"View Transaction Details", nil);
    [self.completeBtn setLayerRadius:30];
    NSArray *arrayAmount = [[_dict safeStringForKey:@"amount"]componentsSeparatedByString:@" "];
    self.amountLabel.text = [NSString stringWithFormat:@"%@ %@",[arrayAmount firstObject],[kWalletManager.currentWalletInfo.coinType uppercaseString]];
}
- (void)viewTransactionDetails
{
    OKWeakSelf(self)
    [MBProgressHUD showHUDAddedTo:weakself.view animated:YES];
    dispatch_time_t delayTime =  dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC));
    dispatch_after(delayTime, dispatch_get_main_queue(), ^{
        [MBProgressHUD hideHUDForView:weakself.view animated:YES];
        if (self.block) {
            self.block();
        }
    });
}

- (IBAction)completeBtnClick:(UIButton *)sender {
    [self.navigationController popToRootViewControllerAnimated:YES];
}

@end
