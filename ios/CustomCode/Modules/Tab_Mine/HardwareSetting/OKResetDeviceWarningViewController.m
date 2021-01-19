//
//  OKResetDeviceWarningViewController.m
//  OneKey
//
//  Created by liuzj on 09/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKResetDeviceWarningViewController.h"

@interface OKResetDeviceWarningViewController ()
@property (weak, nonatomic) IBOutlet UILabel *warningText;
@property (weak, nonatomic) IBOutlet UIButton *warning;
@property (weak, nonatomic) IBOutlet UIButton *readButton;
@property (weak, nonatomic) IBOutlet UIButton *confirmButton;
@end

@implementation OKResetDeviceWarningViewController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKResetDeviceWarningViewController"];
}

- (IBAction)agree:(id)sender {
}

- (IBAction)confirm:(id)sender {
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"hardwareWallet.recover.title", nil);
    self.warningText.text = MyLocalizedString(@"hardwareWallet.recover.tip", nil);
    self.warning.titleLabel.text = MyLocalizedString(@"⚠️ risk warning", nil);
    self.readButton.titleLabel.text = MyLocalizedString(@"I am aware of the above risk", nil);
    self.confirmButton.titleLabel.text = MyLocalizedString(@"hardwareWallet.recover.title", nil);

}



@end
