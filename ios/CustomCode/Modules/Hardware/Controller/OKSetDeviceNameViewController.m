//
//  OKSetDeviceNameViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSetDeviceNameViewController.h"
#import "OKDeviceReadyToStartViewController.h"

@interface OKSetDeviceNameViewController ()
@property (weak, nonatomic) IBOutlet UILabel *setDeviceNameLabel;
@property (weak, nonatomic) IBOutlet UITextField *deviceNameTextfield;
@property (weak, nonatomic) IBOutlet UIButton *createBtn;
- (IBAction)createBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *nameBgView;
@end

@implementation OKSetDeviceNameViewController
+ (instancetype)setDeviceNameViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSetDeviceNameViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"Activate hardware wallet", nil);
    self.setDeviceNameLabel.text = MyLocalizedString(@"Set device name", nil);
    [self.createBtn setLayerDefaultRadius];
    [self.nameBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.deviceNameTextfield becomeFirstResponder];
}

- (IBAction)createBtnClick:(UIButton *)sender
{
    OKDeviceReadyToStartViewController *deviceReadyToStartVc = [OKDeviceReadyToStartViewController deviceReadyToStartViewController];
    deviceReadyToStartVc.deviceName = self.deviceNameTextfield.text;
    [self.navigationController pushViewController:deviceReadyToStartVc animated:YES];
}
@end
