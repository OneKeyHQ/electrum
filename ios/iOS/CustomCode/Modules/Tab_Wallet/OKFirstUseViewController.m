//
//  OKFirstUseViewController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFirstUseViewController.h"
#import "AppDelegate.h"

@interface OKFirstUseViewController ()<UINavigationControllerDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *iconImage;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *icon1;
@property (weak, nonatomic) IBOutlet UIImageView *icon2;
@property (weak, nonatomic) IBOutlet UILabel *desc1Label;
@property (weak, nonatomic) IBOutlet UILabel *desc1Labelsub;
@property (weak, nonatomic) IBOutlet UILabel *desc2Label;
@property (weak, nonatomic) IBOutlet UILabel *desc2Labelsub;

@property (weak, nonatomic) IBOutlet OKClickTextView *textViewClick;

@property (weak, nonatomic) IBOutlet UIButton *startBtn;
- (IBAction)startBtnClick:(UIButton *)sender;
- (IBAction)checkBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *checkBtn;

@end

@implementation OKFirstUseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
    
}

- (void)stupUI
{
    self.iconImage.image = [UIImage imageNamed:@"logo_square"];
    self.titleLabel.text = MyLocalizedString(@"With OneKey, you can certainly have it both safe and easy to use", nil);
    self.icon1.image = [UIImage imageNamed:@"home"];
    self.icon2.image = [UIImage imageNamed:@"safe"];
    self.desc1Label.text = MyLocalizedString(@"Your assets are only in your hands", nil);
    self.desc1Labelsub.attributedText = [NSString lineSpacing:13 content:MyLocalizedString(@"Our server will not store your private key or mnemonic in any way. Both software and hardware are open source and can be used safely", nil)];
    self.desc2Label.text = MyLocalizedString(@"End-to-end encryption", nil);
    self.desc2Labelsub.attributedText = [NSString lineSpacing:13 content:MyLocalizedString(@"We use industry-leading encryption technology to store information locally. Only you can decrypt the information, we will not and cannot view, use or sell any of your data", nil)];
    
    NSString *content = MyLocalizedString(@"By starting to use, you agree to Onekey's User Agreement and Privacy Policy", nil);
    // 设置文字
    self.textViewClick.text = content;
    self.textViewClick.scrollEnabled = NO;
    self.textViewClick.editable = NO;
    XXWeakSelf(self);
    NSRange range1 = [content rangeOfString:MyLocalizedString(@"User Agreement", nil)];
    [self.textViewClick setUnderlineTextWithRange:range1 withUnderlineColor:HexColor(RGB_THEME_GREEN) withClickCoverColor:nil withBlock:^(NSString *clickText) {
        WebViewVC *webVc = [WebViewVC loadWebViewControllerWithTitle:@"OneKey" url:kTheServiceAgreement];
        [weakself.navigationController pushViewController:webVc animated:YES];
        
    }];
    NSRange range2 = [content rangeOfString:MyLocalizedString(@"Privacy policy", nil)];
    [self.textViewClick setUnderlineTextWithRange:range2 withUnderlineColor:HexColor(RGB_THEME_GREEN) withClickCoverColor:nil withBlock:^(NSString *clickText) {
        WebViewVC *webVc = [WebViewVC loadWebViewControllerWithTitle:@"OneKey" url:kPrivacyAgreement];
        [weakself.navigationController pushViewController:webVc animated:YES];
    }];
    
    [self.startBtn setTitle:MyLocalizedString(@"Begin to use", nil) forState:UIControlStateNormal];
    self.navigationController.delegate = self;
    
    [self checkUI];
}

+ (instancetype)firstUseViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil] instantiateViewControllerWithIdentifier:@"OKFirstUseViewController"];
}

- (IBAction)checkBtnClick:(UIButton *)sender {
    sender.selected = !sender.selected;
    [self checkUI];
}
- (void)checkUI
{
    if (self.checkBtn.selected) {
        self.startBtn.enabled = YES;
        self.startBtn.alpha = 1.0;
    }else{
        self.startBtn.enabled = NO;
        self.startBtn.alpha = 0.5;
    }
}

- (IBAction)startBtnClick:(UIButton *)sender {
    [OKStorageManager saveToUserDefaults:@"1" key:kFirstUsedShowed];
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowFirstVc = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowFirstVc animated:YES];
}
@end
