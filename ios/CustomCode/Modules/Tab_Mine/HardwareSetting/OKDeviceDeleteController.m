//
//  OKDeviceDeleteController.m
//  OneKey
//
//  Created by liuzj on 2021/1/13.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceDeleteController.h"

@interface OKDeviceDeleteController ()
@property (weak, nonatomic) IBOutlet UIButton *backButton;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UILabel *tipLabel;
@property (weak, nonatomic) IBOutlet UIView *bgView;
@end

@implementation OKDeviceDeleteController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceDeleteController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.backButton addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    [self.cancelButton addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    [self.deleteButton addTarget:self action:@selector(deleteDevice) forControlEvents:UIControlEventTouchUpInside];
    [self setupUI];
}

- (void)setupUI {
    [self.bgView setLayerRadius:20];

    self.titleLabel.text = @"hardwareWallet.delete".localized;
    NSString *tip = @"hardwareWallet.delete.tip".localized;
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:tip];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = 15;
    [attributedString addAttribute:NSParagraphStyleAttributeName value:paragraphStyle range:NSMakeRange(0, tip.length)];
    self.tipLabel.attributedText = attributedString;

    self.deleteButton.titleLabel.text = @"hardwareWallet.delete".localized;
    [self.deleteButton setLayerRadius:20];

    self.cancelButton.titleLabel.text = @"hardwareWallet.delete.cancel".localized;
    [self.cancelButton setLayerRadius:20];
    [self.cancelButton setLayerBorderWithColor:HexColor(0xbebebe) andWidth:1];

}

- (void)deleteDevice {
    if (self.deleteDeviceCallback) {
        self.deleteDeviceCallback();
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)back {
    if (self.dismissCallback) {
        self.dismissCallback();
    } else {
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

@end
