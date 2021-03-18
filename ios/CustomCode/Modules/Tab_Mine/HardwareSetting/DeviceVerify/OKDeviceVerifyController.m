//
//  OKDeviceVerifyController.m
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceVerifyController.h"
#import "OKDeviceVerifyResultController.h"

@interface OKDeviceVerifyController ()
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *tagLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *processLabel;
@property (weak, nonatomic) IBOutlet UIView *processView;
@property (weak, nonatomic) IBOutlet UILabel *stage1Label;
@property (weak, nonatomic) IBOutlet UILabel *stage2Label;
@property (weak, nonatomic) IBOutlet UILabel *stage3Label;
@property (weak, nonatomic) IBOutlet UIImageView *stage1ImageView;
@property (weak, nonatomic) IBOutlet UIImageView *stage2ImageView;
@property (weak, nonatomic) IBOutlet UIImageView *stage3ImageView;
@property (strong, nonatomic) CABasicAnimation* rotationAnimation;
@property (assign, nonatomic) BOOL netErr;
@end

@implementation OKDeviceVerifyController
+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceVerifyController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"hardwareWallet.verify".localized;
    self.titleLabel.text = @"hardwareWallet.verify.connecting".localized;
    NSString *descLabelTtext = @"hardwareWallet.verify.connectingTip".localized;
    self.descLabel.attributedText = [NSString lineSpacing:8 content:descLabelTtext];
    self.descLabel.textAlignment = NSTextAlignmentCenter;
    self.processLabel.text = @"hardwareWallet.verify.processing".localized;
    self.stage1Label.text = @"hardwareWallet.verify.connectingDevice".localized;
    self.stage2Label.text = @"hardwareWallet.verify.getSign".localized;
    self.stage3Label.text = @"hardwareWallet.verify.submitting".localized;

    [self.tagLabel setLayerRadius:self.tagLabel.height * 0.5];
    [self.processView setLayerRadius:20];
    [self.stage1ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage2ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage3ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    self.phase = OKDeviceVerifyPhaseConnecting;
    OKDeviceModel *deviceModel = [[OKDevicesManager sharedInstance] getDeviceModelWithID:self.deviceId];
    if (deviceModel.deviceInfo.ble_name) {
        self.nameLabel.text = deviceModel.deviceInfo.ble_name;
    }
    [self verifyHardware];
}

- (void)verifyHardware {

    self.phase = OKDeviceVerifyPhaseFetching;

    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        BOOL pass = NO;
        NSDictionary *result;
        NSString *json = [kPyCommandsManager callInterface:kInterfacehardware_verify parameter:@{@"msg":[NSUUID UUID].UUIDString}];

        if ([json isKindOfClass:NSString.class]) {
            // 非标准 json, 临时解决
            json = [json stringByReplacingOccurrencesOfString:@": True" withString:@": true"];
            result = json.toDict;
        } else if ([json isKindOfClass:NSDictionary.class]) {
            result = (NSDictionary *)json;
        } else {
            self.netErr = YES;
        }
        BOOL is_bixinkey = [result objectForKey:@"is_bixinkey"];
        BOOL is_verified = [result objectForKey:@"is_verified"];
        pass = is_bixinkey && is_verified;

        dispatch_async(dispatch_get_main_queue(), ^{
            self.phase = OKDeviceVerifyPhaseSubmitting;
            [self showResult:pass];
        });
    });

}

- (void)showResult:(BOOL)isPassed {
    self.phase = OKDeviceVerifyPhaseDone;
    OKDeviceVerifyResultController *vc = [OKDeviceVerifyResultController controllerWithStoryboard];
    if (self.netErr) {
        vc.verifyResult = OKDeviceVerifyResultNetworkError;
    } else {
        vc.verifyResult = isPassed ? OKDeviceVerifyResultPass : OKDeviceVerifyResultFail;
    }
    if (isPassed) {
        OKDeviceModel *deviceModel = [[OKDevicesManager sharedInstance] getDeviceModelWithID:self.deviceId];
        deviceModel.verifiedDevice = YES;
        [[OKDevicesManager sharedInstance] updateDevices:deviceModel];
    }
    vc.name = self.nameLabel.text;
    OKWeakSelf(self)
    vc.doneCallback = ^{
        [weakself.navigationController popViewControllerAnimated:YES];
        [weakself.navigationController popViewControllerAnimated:YES];
    };
    if (self.resultCallback) {
        self.resultCallback(isPassed);
    }
    [self.navigationController pushViewController:vc animated:YES];
}

- (CABasicAnimation *)rotationAnimation {
    if (!_rotationAnimation) {
        _rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
        _rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0];
        _rotationAnimation.duration = 0.5;
        _rotationAnimation.cumulative = YES;
        _rotationAnimation.repeatCount = MAXFLOAT;
    }
    return _rotationAnimation;
}

- (void)setPhase:(OKDeviceVerifyPhase)phase {

    _phase = phase;
    if (phase == OKDeviceVerifyPhaseConnecting) {

    } else if (phase == OKDeviceVerifyPhaseFetching) {
        [self.stage1ImageView.layer removeAllAnimations];
        self.stage1ImageView.image = [UIImage imageNamed:@"isselected"];
    } else if (phase == OKDeviceVerifyPhaseSubmitting) {
        [self.stage2ImageView.layer removeAllAnimations];
        self.stage2ImageView.image = [UIImage imageNamed:@"isselected"];
    } else if (phase == OKDeviceVerifyPhaseDone) {
        [self.stage3ImageView.layer removeAllAnimations];
        self.stage3ImageView.image = [UIImage imageNamed:@"isselected"];
    }

}

@end
