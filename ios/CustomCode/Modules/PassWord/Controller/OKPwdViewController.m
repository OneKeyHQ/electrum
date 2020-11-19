//
//  OKPwdViewController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKPwdViewController.h"
#import "CLPasswordInputView.h"
#import "OKBiologicalViewController.h"

typedef enum {
    PageTypeFirst = 0,
    PageTypeSecond
}PageType;

typedef enum {
    PwdTypeShort = 0,
    PwdTypeLong
}PwdType;


@interface OKPwdViewController ()<UINavigationControllerDelegate,CLPasswordInputViewDelegate>
@property (weak, nonatomic) IBOutlet UIScrollView *scrollViewBg;
@property (nonatomic,assign)PageType page;

//FirstView
@property (weak, nonatomic) IBOutlet CLPasswordInputView *pwdInputViewFirst;
- (IBAction)backFirstBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *backBtnFirst;
@property (weak, nonatomic) IBOutlet UILabel *titleLabelFirst;
@property (weak, nonatomic) IBOutlet UILabel *titleDescLabelFirst;

@property (weak, nonatomic) IBOutlet UIView *longPwdViewFirst;
@property (weak, nonatomic) IBOutlet UITextField *longPwdFirstTextField;
@property (weak, nonatomic) IBOutlet UIButton *eyeBtnFirst;
@property (weak, nonatomic) IBOutlet UIButton *nextBtnFirst;
- (IBAction)nextBtnFirstClick:(UIButton *)sender;


//SecondView
@property (weak, nonatomic) IBOutlet CLPasswordInputView *pwdInputViewSecond;
- (IBAction)backSecondBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *backBtnSecond;
@property (weak, nonatomic) IBOutlet UILabel *titleLabelSecond;
@property (weak, nonatomic) IBOutlet UILabel *titleDescLabelSecond;
@property (weak, nonatomic) IBOutlet UIButton *nextBtnSecond;
- (IBAction)nextBtnSecondClick:(UIButton *)sender;


@property (weak, nonatomic) IBOutlet UIView *longPwdViewSecond;
@property (weak, nonatomic) IBOutlet UITextField *longPwdSecondTextField;
@property (weak, nonatomic) IBOutlet UIButton *eyeBtnSecond;


//界面导航标题
@property (weak, nonatomic) IBOutlet UILabel *navTitleFirstLabel;
@property (weak, nonatomic) IBOutlet UILabel *navTitleSecondLabel;

//底部切换长短密码的按钮和提示
@property (weak, nonatomic) IBOutlet UIView *switchPwdViewBtn;
@property (weak, nonatomic) IBOutlet UILabel *switchPwdLabel;
@property (weak, nonatomic) IBOutlet UILabel *switchPwdViewTips;


@property (nonatomic,assign)PwdType type;


@end

@implementation OKPwdViewController

+ (instancetype)pwdViewController
{
    return [[UIStoryboard storyboardWithName:@"OKPwd" bundle:nil]instantiateViewControllerWithIdentifier:@"OKPwdViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    _type = PwdTypeShort;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(switchPwdViewBtnClick)];
    [self.switchPwdViewBtn addGestureRecognizer:tap];
    [self stupUI];
    [self changePwdView];
}

- (void)stupUI
{
    switch (_pwdUseType) {
        case OKPwdUseTypeInitPassword:
        {
            self.navTitleFirstLabel.text = MyLocalizedString(@"Create a new wallet", nil);
            self.navTitleSecondLabel.text = MyLocalizedString(@"Create a new wallet", nil);
            self.titleLabelFirst.text  = MyLocalizedString(@"Set the password", nil);
            self.titleDescLabelFirst.text = MyLocalizedString(@"Only you can unlock your wallet", nil);
            self.titleLabelSecond.text  = MyLocalizedString(@"Enter your password again", nil);
            self.titleDescLabelSecond.text = MyLocalizedString(@"Don't reveal your password to anyone else", nil);
        }
            break;
        case OKPwdUseTypeUpdatePassword:
        {
            self.navTitleFirstLabel.text = MyLocalizedString(@"Change the password", nil);;
            self.navTitleSecondLabel.text = MyLocalizedString(@"Change the password", nil);;
            self.titleLabelFirst.text  = MyLocalizedString(@"Enter your original password", nil);
            self.titleDescLabelFirst.text = MyLocalizedString(@"After changing the password, your original biometrics (face, fingerprint) will become invalid and need to be reset", nil);
            self.titleLabelSecond.text  = MyLocalizedString(@"Set your new password", nil);
            self.titleDescLabelSecond.text = MyLocalizedString(@"After changing the password, your original biometrics (face, fingerprint) will become invalid and need to be reset", nil);
        }
            break;
            
        default:
            break;
    }
    self.switchPwdViewBtn.layer.cornerRadius = 14;
    self.switchPwdViewBtn.layer.masksToBounds = YES;
    
    [self.longPwdViewFirst setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.longPwdViewSecond setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    
    [self.nextBtnFirst setLayerRadius:20];
    [self.nextBtnSecond setLayerRadius:20];
    
    self.navigationController.delegate = self;
    self.pwdInputViewFirst.delegate = self;
    self.pwdInputViewSecond.delegate = self;
    
    [self.pwdInputViewFirst becomeFirstResponder];
    // Do any additional setup after loading the view.
    [self.pwdInputViewFirst updateWithConfigure:^(CLPasswordInputViewConfigure * _Nonnull configure) {
        configure.pointColor = HexColor(APP_MAIN_BLACK_COLOR);
        configure.rectColor = HexColor(0xF2F2F2);
        configure.rectBackgroundColor = HexColor(0xF2F2F2);
        configure.backgroundColor = [UIColor whiteColor];
        configure.threePartyKeyboard = NO;
    }];
    [self.pwdInputViewSecond updateWithConfigure:^(CLPasswordInputViewConfigure * _Nonnull configure) {
        configure.pointColor = HexColor(APP_MAIN_BLACK_COLOR);
        configure.rectColor = HexColor(0xF2F2F2);
        configure.rectBackgroundColor = HexColor(0xF2F2F2);
        configure.backgroundColor = [UIColor whiteColor];
        configure.threePartyKeyboard = NO;
    }];
}

- (void)switchPwdViewBtnClick
{
    _type = !_type;
    self.page = PageTypeFirst;
    [self resetViewWithAnimated:YES];
    [self changePwdView];
}

#pragma mark - 切换长短密码界面
- (void)changePwdView
{
    switch (_type) {
        case PwdTypeShort:
        {
            self.longPwdViewFirst.hidden = YES;
            self.longPwdViewSecond.hidden = YES;
            self.pwdInputViewFirst.hidden = NO;
            self.pwdInputViewSecond.hidden = NO;
            self.nextBtnFirst.hidden = YES;
            self.nextBtnSecond.hidden = YES;
            self.switchPwdLabel.text = MyLocalizedString(@"Use longer passwords that are more complex and more secure", nil);
            if (self.page == PageTypeFirst) {
                self.switchPwdViewTips.text = MyLocalizedString(@"It is used to encrypt your wallet, is simple and easy to remember, and is suitable for most people", nil);
            }else{
                self.switchPwdViewTips.text =  MyLocalizedString(@"We do not store any of your information, so if you forget your password, we will not be able to retrieve it for you", nil);
            }
        }
            break;
        case PwdTypeLong:
        {
            self.longPwdViewFirst.hidden = NO;
            self.longPwdViewSecond.hidden = NO;
            self.pwdInputViewFirst.hidden = YES;
            self.pwdInputViewSecond.hidden = YES;
            self.nextBtnFirst.hidden = NO;
            self.nextBtnSecond.hidden = NO;
            self.switchPwdLabel.text = MyLocalizedString(@"Use a short password", nil);
            if (self.page == PageTypeFirst) {
                self.switchPwdViewTips.text = MyLocalizedString(@"Complex passwords are relatively more secure, so keep in mind that it's best not to set them the same as your passwords for other sites", nil);
            }else{
                self.switchPwdViewTips.text =  MyLocalizedString(@"We do not store any of your information, so if you forget your password, we will not be able to retrieve it for you", nil);
            }
        }
        default:
            break;
    }
}
#pragma mark - 刷新底部的提示信息
- (void)resetPwdViewTips
{
    if (self.page == PageTypeFirst) {
        self.switchPwdViewTips.text = MyLocalizedString(@"It is used to encrypt your wallet, is simple and easy to remember, and is suitable for most people", nil);
    }else if (self.page == PageTypeSecond){
        self.switchPwdViewTips.text = MyLocalizedString(@"We do not store any of your information, so if you forget your password, we will not be able to retrieve it for you", nil);
    }
}

#pragma mark - 重置跳转
- (void)resetViewWithAnimated:(BOOL)animated {
    
    //短密码
    if (self.type == PwdTypeShort) {
        if (self.page == PageTypeFirst) { //第一页
            [self.pwdInputViewFirst becomeFirstResponder];
        }else if (self.page == PageTypeSecond){
            [self.pwdInputViewSecond becomeFirstResponder];
        }
        [self.scrollViewBg setContentOffset:CGPointMake(self.page * SCREEN_WIDTH, 0) animated:animated];
        return;
    }
    
    //长密码
    if (self.page == PageTypeFirst) { //第一页
        [self.longPwdFirstTextField becomeFirstResponder];
    }else if (self.page == PageTypeSecond){
        [self.longPwdSecondTextField becomeFirstResponder];
    }
    [self.scrollViewBg setContentOffset:CGPointMake(self.page * SCREEN_WIDTH, 0) animated:animated];
}


#pragma mark - PwdInputViewDelegate
- (void)passwordInputViewDidChange:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"正在输入");
}
- (void)passwordInputViewCompleteInput:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"输入完毕");
    if (passwordInputView == self.pwdInputViewFirst) {//第一页输入结束
        self.page = 1;
        [self resetViewWithAnimated:YES];
        [self resetPwdViewTips];
    }else if (passwordInputView ==self.pwdInputViewSecond){
        if (self.pwdUseType == OKPwdUseTypeInitPassword) {
            if ([self.pwdInputViewFirst.text isEqualToString:self.pwdInputViewSecond.text] && self.pwdInputViewFirst.text.length > 0) {
                [self passwordIsCorrect:self.pwdInputViewFirst.text];
            }else{
                [self pwdWrongTip];
            }
        }else if (self.pwdUseType == OKPwdUseTypeUpdatePassword){
            NSString *old_password  = self.pwdInputViewFirst.text;
            NSString *new_password = self.pwdInputViewSecond.text;
            if ([old_password isEqualToString:new_password]) {
                [kTools tipMessage:MyLocalizedString(@"The new password cannot be the same as the old one", nil)];
                return;
            }
            [kPyCommandsManager callInterface:kInterfaceUpdate_password parameter:@{@"old_password":old_password,@"new_password":new_password}];
            [kTools tipMessage:MyLocalizedString(@"Password changed successfully", nil)];
            [self dismissViewControllerAnimated:YES completion:nil];
        }
    }
}
- (void)passwordInputViewDidDeleteBackward:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"点击删除");
}
- (void)passwordInputViewBeginInput:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"开始输入");
}
- (void)passwordInputViewEndInput:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"结束输入");
 
}


#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowPwdPage = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowPwdPage animated:YES];
}

- (IBAction)backFirstBtnClick:(UIButton *)sender {
    [self.view endEditing:YES];
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)backSecondBtnClick:(UIButton *)sender
{
    self.page = PageTypeFirst;
    [self resetViewWithAnimated:YES];
    [self resetPwdViewTips];
}
- (IBAction)nextBtnFirstClick:(UIButton *)sender {
    self.page = PageTypeSecond;
    [self resetViewWithAnimated:YES];
    [self resetPwdViewTips];
}
- (IBAction)nextBtnSecondClick:(UIButton *)sender {
    switch (self.pwdUseType) {
        case OKPwdUseTypeInitPassword:
        {
            if ([self.longPwdFirstTextField.text isEqualToString:self.longPwdSecondTextField.text]&&self.longPwdFirstTextField.text.length > 0) { //密码一致
                [self passwordIsCorrect:self.longPwdSecondTextField.text];
            }else{
                [self pwdWrongTip];
            }
        }
            break;
        case OKPwdUseTypeUpdatePassword:
        {
            NSString *old_password  = self.longPwdFirstTextField.text;
            NSString *new_password = self.longPwdSecondTextField.text;
            if ([old_password isEqualToString:new_password]) {
                [kTools tipMessage:MyLocalizedString(@"The new password cannot be the same as the old one", nil)];
                return;
            }
            NSString *result = [kPyCommandsManager callInterface:kInterfaceUpdate_password parameter:@{@"old_password":old_password,@"new_password":new_password}];
            if (result != nil) {
                [kTools tipMessage:MyLocalizedString(@"Password changed successfully", nil)];
            }
            [self dismissViewControllerAnimated:YES completion:nil];
        }
        default:
            break;
    }
}

#pragma mark -  两次输入密码相同
- (void)passwordIsCorrect:(NSString *)pwd
{
    NSString *seed = @"";
    if (self.words.length > 0) {
        seed = self.words;
    }
    NSString *createHD =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
    NSArray *words = [createHD componentsSeparatedByString:@" "];
    if (words.count > 0) {
        [OKStorageManager saveToUserDefaults:@"BTC-1" key:kCurrentWalletName];
        //创建HD成功刷新首页的UI
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletFirstCreateComplete object:@{@"pwd":pwd}];
        OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController];
        [self.navigationController pushViewController:biologicalVc animated:YES];
    }
}


#pragma mark - 输入错误的提示信息
- (void)pwdWrongTip
{
    [kTools tipMessage:MyLocalizedString(@"The two passwords are different. Please re-enter them", nil)];
}
@end
