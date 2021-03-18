//
//  OKDeviceVerifyResultController.m
//  OneKey
//
//  Created by liuzj on 2021/1/14.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceVerifyResultController.h"

@interface OKDeviceVerifyResultController ()
@property (weak, nonatomic) IBOutlet UILabel *resultLabel;
@property (weak, nonatomic) IBOutlet UILabel *tagLabel;
@property (weak, nonatomic) IBOutlet UIView *tagView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *doneButton;
@property (weak, nonatomic) IBOutlet UIImageView *deviceImageView;

@end

@implementation OKDeviceVerifyResultController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceVerifyResultController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(back)];

    self.title = @"hardwareWallet.verify".localized;
    NSString *descLabelText;
    if (self.verifyResult == OKDeviceVerifyResultPass) {
        self.resultLabel.text = @"hardwareWallet.verify.pass".localized;
        self.resultLabel.textColor = HexColor(0x00b812);
        descLabelText = @"hardwareWallet.verify.passDesc".localized;
        self.deviceImageView.image = [UIImage imageNamed:@"device_success"];
    } else if (self.verifyResult == OKDeviceVerifyResultFail) {
        self.resultLabel.text = @"hardwareWallet.verify.fail".localized;
        self.resultLabel.textColor = HexColor(0xeb5757);
        descLabelText = @"hardwareWallet.verify.failDesc".localized;
        self.deviceImageView.image = [UIImage imageNamed:@"device_failed"];
    } else {
        self.resultLabel.text = @"hardwareWallet.verify.fail".localized;
        self.resultLabel.textColor = HexColor(0xeb5757);
        descLabelText = @"hardwareWallet.verify.netErr".localized;
        self.deviceImageView.image = [UIImage imageNamed:@"device_failed"];
    }
    self.descLabel.attributedText = [NSString lineSpacing:16 content:descLabelText];
    self.descLabel.textAlignment = NSTextAlignmentCenter;

    self.tagLabel.text = self.name;
    [self.tagView setLayerRadius:self.tagView.height * 0.5];
    self.doneButton.titleLabel.text = @"hardwareWallet.verify.return".localized;
    [self.doneButton setLayerRadius:20];
}
- (IBAction)doneClick:(id)sender {
    [self back];
}

- (void)back {
    if (self.doneCallback) {
        self.doneCallback();
    } else {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}


@end
