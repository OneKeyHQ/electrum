//
//  OKBiologicalViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/22.
//  Copyright © 2020 OneKey. All rights reserved..
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
    [self hideBackBtn];
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
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if ([self.navigationController respondsToSelector:@selector(interactivePopGestureRecognizer)]){
        self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    }
}
- (void)viewDidDisappear:(BOOL)animated {
  [super viewDidDisappear:animated];
  self.navigationController.interactivePopGestureRecognizer.enabled = YES;
}
- (YZAuthID *)authIDControl {
    if (!_authIDControl) {
        _authIDControl = [[YZAuthID alloc] init];
    }
    return _authIDControl;
}

- (IBAction)startBtnClick:(UIButton *)sender {
    [self.authIDControl yz_showAuthIDWithDescribe:@"OneKey" BlockState:^(YZAuthIDState state, NSError *error) {
        if (state == YZAuthIDStateNotSupport
            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
            
        } else if(state == YZAuthIDStateFail) { // 认证失败
            
        } else if(state == YZAuthIDStateTouchIDLockout) {   // 多次错误，已被锁定
            
        } else if (state == YZAuthIDStateSuccess) { // TouchID/FaceID验证成功
            kWalletManager.isOpenAuthBiological = YES;
            [self.OK_TopViewController dismissViewControllerAnimated:NO completion:nil];
        }
        
    }];
}
- (IBAction)nextBtnClick:(UIButton *)sender {
    [self.OK_TopViewController dismissViewControllerAnimated:YES completion:nil];
}


@end
