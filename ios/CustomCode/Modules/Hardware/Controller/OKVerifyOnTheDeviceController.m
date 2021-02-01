//
//  OKVerifyOnTheDeviceController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKVerifyOnTheDeviceController.h"
#import "OKPINCodeViewController.h"
#import "OKDeviceSuccessViewController.h"

@interface OKVerifyOnTheDeviceController ()<OKHwNotiManagerDelegate>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;
@property (nonatomic,assign)OKVerifyOnTheDeviceType type;
@end

@implementation OKVerifyOnTheDeviceController
+ (instancetype)verifyOnTheDeviceController:(OKVerifyOnTheDeviceType)type
{
    OKVerifyOnTheDeviceController *vc = [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKVerifyOnTheDeviceController"];
    vc.type = type;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
    switch (_type) {
        case OKVerifyOnTheDeviceTypeBackupSetPin:
            [self backupToHardwareInterface];
            break;
        case OKVerifyOnTheDeviceTypeBackupActiveSuccess:
        {
            [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(activeSuccess) name:kActiveSuccess object:nil];
        }
            break;
        case OKVerifyOnTheDeviceTypeNormalActiveSuccess:
        {
            [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(activeSuccess) name:kActiveSuccess object:nil];
        }
            break;
        default:
            break;
    }
}
- (void)stupUI
{
    self.title = MyLocalizedString(@"Activate hardware wallet", nil);
    self.titleLabel.text = MyLocalizedString(@"Verify on the equipment", nil);
    self.iconImageView.image = [UIImage imageNamed:@"device_confirm"];
    [self.nextBtn setLayerDefaultRadius];
    [self refreshBtn:NO];
}
- (void)backupToHardwareInterface
{
    OKWeakSelf(self)
    kHwNotiManager.delegate = self;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
       id result = [kPyCommandsManager callInterface:kInterfacebixin_load_device parameter:@{@"label":weakself.deviceName,@"mnemonics":weakself.words}];
       if (result != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[NSNotificationCenter defaultCenter]postNotificationName:kActiveSuccess object:nil];
            });
        }
    });
}
- (IBAction)nextBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    switch (_type) {
        case OKVerifyOnTheDeviceTypeBackupSetPin:
        {
            OKPINCodeViewController *pinVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                   id result = [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                   if (result != nil) {
                       NSLog(@"result = %@",result);
                       dispatch_async(dispatch_get_main_queue(), ^{
                           OKVerifyOnTheDeviceController *verifyVc = [OKVerifyOnTheDeviceController verifyOnTheDeviceController:OKVerifyOnTheDeviceTypeBackupActiveSuccess];
                           verifyVc.deviceName = weakself.deviceName;
                           [weakself.navigationController pushViewController:verifyVc animated:YES];
                       });
                    }
                });
            }];
            [weakself.navigationController pushViewController:pinVc animated:YES];
        }
            break;
        case OKVerifyOnTheDeviceTypeBackupActiveSuccess:
        {
            OKDeviceSuccessViewController *deviceVc = [OKDeviceSuccessViewController deviceSuccessViewController:OKDeviceSuccessHwBackup deviceName:self.deviceName];
            [self.navigationController pushViewController:deviceVc animated:YES];
        }
            break;
        case OKVerifyOnTheDeviceTypeNormalActiveSuccess:
        {
            OKDeviceSuccessViewController *deviceVc = [OKDeviceSuccessViewController deviceSuccessViewController:OKDeviceSuccessActivate deviceName:self.deviceName];
            [self.navigationController pushViewController:deviceVc animated:YES];
        }
            break;
        default:
            break;
    }
}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    NSLog(@"type = %zd",type);
    OKWeakSelf(self)
    if (type == OKHWNotiTypePin_New_First) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakself refreshBtn:YES];
        });
    }
}
- (void)refreshBtn:(BOOL)use
{
    if (use) {
        self.nextBtn.alpha = 1.0;
        self.nextBtn.enabled = YES;
    }else{
        self.nextBtn.alpha = 0.5;
        self.nextBtn.enabled = NO;
    }
}
- (void)activeSuccess
{
    [self refreshBtn:YES];
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:kActiveSuccess object:nil];
}
@end
