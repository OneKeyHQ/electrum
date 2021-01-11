//
//  OKVerifySignatureResultController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKVerifySignatureResultController.h"

@interface OKVerifySignatureResultController ()

@property (weak, nonatomic) IBOutlet UILabel *topLabel;

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *descLabel;

@property (weak, nonatomic) IBOutlet UIButton *confirmBtn;

- (IBAction)confirmBtnClick:(UIButton *)sender;

@end

@implementation OKVerifySignatureResultController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    [self.confirmBtn setTitle:MyLocalizedString(@"return", nil) forState:UIControlStateNormal];
    self.title = MyLocalizedString(@"Verify the signature", nil);
    [self.confirmBtn setLayerRadius:20];
    switch (_type) {
        case OKVerifySignatureTypeSuccess:
        {
            self.topLabel.text = MyLocalizedString(@"Verification by", nil);
            self.descLabel.text = MyLocalizedString(@"The signature information is consistent with the public key/original informatio", nil);
            self.topLabel.textColor = HexColor(0x00B812);
            self.iconImageView.image = [UIImage imageNamed:@"icon-color-coins"];
        }
            break;
        case OKVerifySignatureTypeFailure:
        {
            self.topLabel.text = MyLocalizedString(@"Validation fails", nil);
            self.descLabel.text = MyLocalizedString(@"The signature information does not match the public key/original information", nil);
            self.topLabel.textColor = HexColor(0xEB5757);
            self.iconImageView.image = [UIImage imageNamed:@"icon-color-coinf"];
        }
            break;
        default:
            break;
    }
}

- (IBAction)confirmBtnClick:(UIButton *)sender {
    [self.navigationController popViewControllerAnimated:YES];
}
@end
