//
//  OKDeviceLanguageViewController.m
//  OneKey
//
//  Created by liuzj on 2021/1/10.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceLanguageViewController.h"

@interface OKDeviceLanguageViewController ()
@property (weak, nonatomic) IBOutlet UIView *chineseCell;
@property (weak, nonatomic) IBOutlet UIView *englishCell;
@property (weak, nonatomic) IBOutlet UIImageView *chineseCheckView;
@property (weak, nonatomic) IBOutlet UIImageView *englishCheckView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (nonatomic, assign) BOOL isChinese;
@end

@implementation OKDeviceLanguageViewController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceLanguageViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"language".localized;
    self.descLabel.text = @"hardwareWallet.lanuage.desc".localized;

    self.isChinese = YES; // TODO: sync with device.
    UITapGestureRecognizer *tapCN = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeLanguageCN)];
    [self.chineseCell addGestureRecognizer:tapCN];
    UITapGestureRecognizer *tapEN = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(changeLanguageEN)];
    [self.englishCell addGestureRecognizer:tapEN];

}

- (void)changeLanguageCN {
    self.isChinese = YES;
}

- (void)changeLanguageEN {
    self.isChinese = NO;
}

- (void)setIsChinese:(BOOL)isChinese {
    if (_isChinese == isChinese) {
        return;
    }
    _isChinese = isChinese;
    self.chineseCheckView.hidden = !isChinese;
    self.englishCheckView.hidden = !self.chineseCheckView.hidden;

    // TODO: sync with device.
}

@end
