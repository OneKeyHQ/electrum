//
//  OKDeviceConfirmController.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/25.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceConfirmController.h"

@interface OKDeviceConfirmController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;

@end

@implementation OKDeviceConfirmController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceConfirmController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.titleLabel.text = self.titleText;
    self.descLabel.text = self.descText;
    [self.nextBtn setLayerRadius:20];
    [self.nextBtn setTitle:self.btnText forState:UIControlStateNormal];
}

- (IBAction)btnClick:(id)sender {
    if (self.btnCallback) {
        self.btnCallback();
    }
}

@end
