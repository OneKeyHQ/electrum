//
//  OKDeviceIdInconsistentViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/2/7.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceIdInconsistentViewController.h"

@interface OKDeviceIdInconsistentViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *iKnowBtn;
- (IBAction)iKnowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@end

@implementation OKDeviceIdInconsistentViewController
+ (instancetype)deviceIdInconsistentViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeviceIdInconsistentViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.iKnowBtn setLayerRadius:20];
    [self.contentView setLayerRadius:20];
}

- (IBAction)iKnowBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    [weakself dismissViewControllerAnimated:NO completion:^{
        [weakself.OK_TopViewController.navigationController popViewControllerAnimated:YES];
    }];
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    [weakself dismissViewControllerAnimated:NO completion:^{
        [weakself.OK_TopViewController.navigationController popViewControllerAnimated:YES];
    }];
}
@end
