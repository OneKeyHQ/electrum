//
//  OKSetDeviceNameViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSetDeviceNameViewController.h"
#import "OKDeviceReadyToStartViewController.h"
#import "OKVerifyOnTheDeviceController.h"

@interface OKSetDeviceNameViewController ()
@property (weak, nonatomic) IBOutlet UILabel *setDeviceNameLabel;
@property (weak, nonatomic) IBOutlet UITextField *deviceNameTextfield;
@property (weak, nonatomic) IBOutlet OKButton *createBtn;
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
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(deviceNameTextfieldChange) name:UITextFieldTextDidChangeNotification object:nil];
    [self deviceNameTextfieldChange];
}

- (IBAction)createBtnClick:(UIButton *)sender
{
    switch (_type) {
        case OKMatchingTypeActivation:
        {
            OKDeviceReadyToStartViewController *deviceReadyToStartVc = [OKDeviceReadyToStartViewController deviceReadyToStartViewController];
            deviceReadyToStartVc.deviceName = self.deviceNameTextfield.text;
            [self.navigationController pushViewController:deviceReadyToStartVc animated:YES];
        }
            break;
        case OKMatchingTypeBackup2Hw:
        {
            OKVerifyOnTheDeviceController *verifyOnTheDevice = [OKVerifyOnTheDeviceController verifyOnTheDeviceController:OKVerifyOnTheDeviceTypeBackupSetPin];
            verifyOnTheDevice.deviceName = self.deviceNameTextfield.text;
            verifyOnTheDevice.words = self.words;
            [self.navigationController pushViewController:verifyOnTheDevice animated:YES];
        }
            break;
        default:
            break;
    }
}

- (void)deviceNameTextfieldChange
{
    if (self.deviceNameTextfield.text.length > 0) {
        [self.createBtn status:OKButtonStatusEnabled];
    }else{
        [self.createBtn status:OKButtonStatusDisabled];
    }
}
@end
