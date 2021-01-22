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
    NSString *descLabelTtext = MyLocalizedString(@"hardwareWallet.verify.connectingTip", nil);
    self.descLabel.attributedText = [NSString lineSpacing:8 content:descLabelTtext];
    self.descLabel.textAlignment = NSTextAlignmentCenter;
    self.processLabel.text = MyLocalizedString(@"hardwareWallet.verify.processing", nil);
    self.stage1Label.text = MyLocalizedString(@"hardwareWallet.verify.connectingDevice", nil);
    self.stage2Label.text = MyLocalizedString(@"hardwareWallet.verify.getSign", nil);
    self.stage3Label.text = MyLocalizedString(@"hardwareWallet.verify.submitting", nil);
    
    [self.tagLabel setLayerRadius:self.tagLabel.height * 0.5];
    [self.processView setLayerRadius:20];
    [self.stage1ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage2ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    [self.stage3ImageView.layer addAnimation:self.rotationAnimation forKey:@"rotationAnimation"];
    self.phase = OKDeviceVerifyPhaseConnecting;

    [self verifyHardware];
}

- (void)verifyHardware {

    self.phase = OKDeviceVerifyPhaseFetching;

    dispatch_async(dispatch_get_global_queue(0, 0), ^{

        id json = [kPyCommandsManager callInterface:kInterfacehardware_verify parameter:@{@"msg":[NSUUID UUID].UUIDString}];
        if (json) {
            dispatch_async(dispatch_get_main_queue(), ^{
                self.phase = OKDeviceVerifyPhaseSubmitting;
            });
            NSURLSession *session = [NSURLSession sharedSession];
            NSURL *url = [NSURL URLWithString:@"https://key.bixin.com/lengqian.bo"];
            NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
            request.HTTPMethod = @"POST";
            NSData *data = [NSJSONSerialization dataWithJSONObject:json options:NSJSONWritingPrettyPrinted error:nil];
            request.HTTPBody = [NSData dataWithData:data];
            
            NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {

                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
                BOOL is_bixinkey = [[dict objectForKey:@"is_bixinkey"] boolValue];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self showResult:is_bixinkey];
                });

              }];

          [dataTask resume];
        }
    });

}

- (void)showResult:(BOOL)isPassed {
    self.phase = OKDeviceVerifyPhaseDone;
    OKDeviceVerifyResultController *vc = [OKDeviceVerifyResultController controllerWithStoryboard];
    vc.isPassed = isPassed;
    vc.doneCallback = ^{
        [self.navigationController popViewControllerAnimated:YES];
        [self.navigationController popViewControllerAnimated:YES];
    };
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
