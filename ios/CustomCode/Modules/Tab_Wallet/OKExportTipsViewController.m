//
//  OKExportTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKExportTipsViewController.h"

@interface OKExportTipsViewController ()
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *cancleBtn;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *desclabel2;



- (IBAction)closeBtnClick:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UIButton *confirmDeleteBtn;

- (IBAction)confirmDeleteBtnClick:(UIButton *)sender;
- (IBAction)cancleBtnClick:(UIButton *)sender;
@property (nonatomic,copy)ConfirmBtnClick block;
@end

@implementation OKExportTipsViewController

+ (instancetype)exportTipsViewController:(ConfirmBtnClick)btnClick
{
    OKExportTipsViewController *vc = [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKExportTipsViewController"];
    vc.block = btnClick;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.contentView setLayerRadius:20];
    [self.confirmDeleteBtn setLayerRadius:20];
    [self.cancleBtn setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    self.titleLabel.text = MyLocalizedString(@"Export hints", nil);
    self.descLabel.text = MyLocalizedString(@"To master the private data of the wallet is to master the assets of the wallet itself", nil);
    self.desclabel2.text = MyLocalizedString(@"The only way to protect your assets is to back them up correctly", nil);
    [self.confirmDeleteBtn setTitle:MyLocalizedString(@"I have known", nil) forState:UIControlStateNormal];
    [self.cancleBtn setTitle:MyLocalizedString(@"cancel", nil) forState:UIControlStateNormal];
}
- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (IBAction)cancleBtnClick:(UIButton *)sender {
    [self closeBtnClick:nil];
}

- (IBAction)confirmDeleteBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:^{
        if (self.block) {
            self.block();
        }
    }];
}
@end
