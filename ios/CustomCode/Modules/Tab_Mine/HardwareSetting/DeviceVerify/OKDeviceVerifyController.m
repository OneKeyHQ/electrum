//
//  OKDeviceVerifyController.m
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceVerifyController.h"

@interface OKDeviceVerifyController ()
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
@end

@implementation OKDeviceVerifyController
+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceVerifyController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"hardwareWallet.verify", nil);
    self.titleLabel.text = MyLocalizedString(@"hardwareWallet.verify.connecting", nil);
    self.descLabel.text = MyLocalizedString(@"hardwareWallet.verify.connectingTip", nil);
    self.processLabel.text = MyLocalizedString(@"hardwareWallet.verify.processing", nil);
    self.stage1Label.text = MyLocalizedString(@"hardwareWallet.verify.connectingDevice", nil);
    self.stage2Label.text = MyLocalizedString(@"hardwareWallet.verify.getSign", nil);
    self.stage3Label.text = MyLocalizedString(@"hardwareWallet.verify.submitting", nil);
    
    [self.tagLabel setLayerRadius:self.tagLabel.height * 0.5];
    [self.processView setLayerRadius:20];
    [self.stage1ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage2ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage3ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
}

- (CABasicAnimation *)rotationAnimation {
    if (!_rotationAnimation) {
        _rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
        _rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0 ];
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
