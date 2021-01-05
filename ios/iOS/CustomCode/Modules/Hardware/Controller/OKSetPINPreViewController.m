//
//  OKSetPINPreViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSetPINPreViewController.h"
#import "OKDeviceSuccessViewController.h"

@interface OKSetPINPreViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;
@end

@implementation OKSetPINPreViewController
+ (instancetype)setPINPreViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSetPINPreViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Activate hardware wallet", nil);
    self.titleLabel.text = MyLocalizedString(@"Set the PIN", nil);
    self.descLabel.text = MyLocalizedString(@"Each use requires a PIN code to gain access to the hardware wallet", nil);
    self.iconImageView.image = [UIImage imageNamed:@"device_confirm"];
    [self.nextBtn setLayerDefaultRadius];
}


- (IBAction)nextBtnClick:(UIButton *)sender {
    OKDeviceSuccessViewController *devicevc = [OKDeviceSuccessViewController deviceSuccessViewController];
    [self.navigationController pushViewController:devicevc animated:YES];

}
@end
