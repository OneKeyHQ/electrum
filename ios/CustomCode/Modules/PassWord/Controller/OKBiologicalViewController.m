//
//  OKBiologicalViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/22.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKBiologicalViewController.h"
#import "YZAuthID.h"

@interface OKBiologicalViewController ()
@property (weak, nonatomic) IBOutlet UILabel *setTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descTitleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *descDetailLabel;
@property (weak, nonatomic) IBOutlet UIButton *startBtn;
- (IBAction)startBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
- (IBAction)nextBtnClick:(UIButton *)sender;

@property (strong, nonatomic) YZAuthID *authIDControl;

@end

@implementation OKBiologicalViewController

+ (instancetype)biologicalViewController
{
    return [[UIStoryboard storyboardWithName:@"OKPwd" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBiologicalViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
}

- (void)stupUI
{
    
    [self setNavigationBarBackgroundColorWithClearColor];
    
    
//    "Set face recognition" = "设置面容识别";
//    "Set fingerprint identification" = "设置指纹识别";
//    "You can more easily unlock your wallet without having to type in your password every time" = "您可以更方便的解锁钱包，不用每次都输密码。";
//    "Your face, fingerprints and other biological data are stored on this machine, encrypted by the operating system of your phone manufacturer, and we can neither access nor save these data" = "您的面容、指纹等生物数据都留存在本机， 由您手机厂商的操作系统加密保存， 我们既无法接触，也不会保存这些数据。";
//    "Turn on Face recognition" = "开启面容识别";
//    "Turn on fingerprint identification" = "开启指纹识别";
//    "The next time again say" = "下次再说";
    self.title = MyLocalizedString(@"Create a new wallet", nil);
    self.setTitleLabel.text = MyLocalizedString(@"Set face recognition", nil);
    self.descTitleLabel.text = MyLocalizedString(@"You can more easily unlock your wallet without having to type in your password every time", nil);
    self.descDetailLabel.text = MyLocalizedString(@"Your face, fingerprints and other biological data are stored on this machine, encrypted by the operating system of your phone manufacturer, and we can neither access nor save these data", nil);
    [self.startBtn setTitle:MyLocalizedString(@"Turn on Face recognition", nil) forState:UIControlStateNormal];
    [self.nextBtn setTitle:MyLocalizedString(@"The next time again say", nil) forState:UIControlStateNormal];
    self.iconImageView.image = [UIImage imageNamed:@"face_id"];
    [self.startBtn setLayerRadius:20];
    [self.nextBtn setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
}

- (YZAuthID *)authIDControl {
    if (!_authIDControl) {
        _authIDControl = [[YZAuthID alloc] init];
    }
    return _authIDControl;
}

- (IBAction)startBtnClick:(UIButton *)sender {
    [self.authIDControl yz_showAuthIDWithDescribe:@"aaa" BlockState:^(YZAuthIDState state, NSError *error) {
        if (state == YZAuthIDStateNotSupport
            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
            
            [self dismissViewControllerAnimated:NO completion:nil];
            NSLog(@"对不起，当前设备不支持指纹/面部ID");
        } else if(state == YZAuthIDStateFail) { // 认证失败
            NSLog(@"指纹/面部ID不正确，认证失败");
        } else if(state == YZAuthIDStateTouchIDLockout) {   // 多次错误，已被锁定
            NSLog(@"多次错误，指纹/面部ID已被锁定，请到手机解锁界面输入密码");
        } else if (state == YZAuthIDStateSuccess) { // TouchID/FaceID验证成功
            NSLog(@"认证成功！");
            [self dismissViewControllerAnimated:NO completion:nil];
        }
        
    }];
    
    NSLog(@"开启面容识别");
}
- (IBAction)nextBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end
