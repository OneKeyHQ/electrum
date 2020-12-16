//
//  OKVerifyOnTheDeviceController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKVerifyOnTheDeviceController.h"
#import "OKSetPINPreViewController.h"

@interface OKVerifyOnTheDeviceController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;



@end

@implementation OKVerifyOnTheDeviceController
+ (instancetype)verifyOnTheDeviceController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKVerifyOnTheDeviceController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}
- (void)stupUI
{
    self.title = MyLocalizedString(@"Activate hardware wallet", nil);
    self.titleLabel.text = MyLocalizedString(@"Verify on the equipment", nil);
    self.iconImageView.image = [UIImage imageNamed:@"device_confirm"];
    [self.nextBtn setLayerDefaultRadius];
    
}

- (IBAction)nextBtnClick:(UIButton *)sender {
    OKSetPINPreViewController *setPinVc = [OKSetPINPreViewController setPINPreViewController];
    [self.navigationController pushViewController:setPinVc animated:YES];
}
@end
