//
//  OKDeviceOffController.m
//  OneKey
//
//  Created by liuzhijie on 2021/1/15.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceOffController.h"

@interface OKDeviceOffController ()
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *secondLabel;
@property (weak, nonatomic) IBOutlet UILabel *unitLabel;
@property (weak, nonatomic) IBOutlet UIView *timeView;
@end

@implementation OKDeviceOffController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceOffController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"hardwareWallet.autoOff", nil);
    self.descLabel.text = MyLocalizedString(@"hardwareWallet.autoOff.desc", nil);
    self.timeLabel.text = MyLocalizedString(@"hardwareWallet.autoOff.timeout", nil);
    self.unitLabel.text = MyLocalizedString(@"hardwareWallet.autoOff.second", nil);
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(timeViewClicked)];
    [self.timeView addGestureRecognizer:tap];
}

- (void)timeViewClicked {
    
}


@end
