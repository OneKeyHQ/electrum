//
//  OKValidationPwdController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/6.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKValidationPwdController.h"
#import "CLPasswordInputView.h"
typedef enum {
    PwdTypeShort = 0,
    PwdTypeLong
}PwdType;

@interface OKValidationPwdController ()<UINavigationControllerDelegate,CLPasswordInputViewDelegate>

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
//界面导航标题
@property (weak, nonatomic) IBOutlet UILabel *navTitleFirstLabel;
//底部切换长短密码的按钮和提示
@property (weak, nonatomic) IBOutlet UIView *switchPwdViewBtn;
@property (weak, nonatomic) IBOutlet UILabel *switchPwdLabel;
@property (weak, nonatomic) IBOutlet UILabel *switchPwdViewTips;

- (IBAction)eyeBtnClick:(UIButton *)sender;




@property (nonatomic,assign)PwdType type;
@property (nonatomic,assign)BOOL isDis;
@end

@implementation OKValidationPwdController
+ (instancetype)validationPwdController
{
    return [[UIStoryboard storyboardWithName:@"OKPwd" bundle:nil]instantiateViewControllerWithIdentifier:@"OKValidationPwdController"];
}

+ (void)showValidationPwdPageOn:(UIViewController *)vc isDis:(BOOL)isDis complete:(ValidationComplete)complete
{
    OKValidationPwdController *validationVc = [OKValidationPwdController validationPwdController];
    validationVc.block = complete;
    validationVc.isDis = isDis;
    BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:validationVc];
    [vc.OK_TopViewController presentViewController:baseVc animated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    if (kUserSettingManager.isLongPwd) {
        _type = PwdTypeLong;
        [self.longPwdFirstTextField becomeFirstResponder];
    }else{
        _type = PwdTypeShort;
        [self.pwdInputViewFirst becomeFirstResponder];
    }
    [self setNavigationBarBackgroundColorWithClearColor];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(switchPwdViewBtnClick)];
    [self.switchPwdViewBtn addGestureRecognizer:tap];
    [self stupUI];
    [self changePwdView];
}
- (void)stupUI
{
    self.navTitleFirstLabel.text = MyLocalizedString(@"Check the password", nil);
    self.titleLabelFirst.text  = MyLocalizedString(@"Enter your password", nil);
    self.titleDescLabelFirst.text = MyLocalizedString(@"Don't reveal your password to anyone else", nil);
    self.switchPwdViewBtn.layer.cornerRadius = 14;
    self.switchPwdViewBtn.layer.masksToBounds = YES;
    [self.longPwdViewFirst setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.nextBtnFirst setLayerRadius:20];
    self.eyeBtnFirst.selected = NO;
    self.navigationController.delegate = self;
    self.pwdInputViewFirst.delegate = self;
    // Do any additional setup after loading the view.
    [self.pwdInputViewFirst updateWithConfigure:^(CLPasswordInputViewConfigure * _Nonnull configure) {
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
    if (_type == PwdTypeShort) {
        [self.pwdInputViewFirst becomeFirstResponder];
    }else if (_type == PwdTypeLong){
        [self.longPwdFirstTextField becomeFirstResponder];
    }
    [self changePwdView];
}

#pragma mark - 切换长短密码界面
- (void)changePwdView
{
    switch (_type) {
        case PwdTypeShort:
        {
            self.longPwdViewFirst.hidden = YES;
            self.pwdInputViewFirst.hidden = NO;
            self.nextBtnFirst.hidden = YES;
            self.switchPwdLabel.text = MyLocalizedString(@"Use longer passwords that are more complex and more secure", nil);
            self.switchPwdViewTips.text =  MyLocalizedString(@"We do not store any of your information, so if you forget your password, we will not be able to retrieve it for you", nil);
        }
            break;
        case PwdTypeLong:
        {
            self.longPwdViewFirst.hidden = NO;
            self.pwdInputViewFirst.hidden = YES;
            self.nextBtnFirst.hidden = NO;
            self.switchPwdLabel.text = MyLocalizedString(@"Use a short password", nil);
            self.switchPwdViewTips.text =  MyLocalizedString(@"We do not store any of your information, so if you forget your password, we will not be able to retrieve it for you", nil);
        }
        default:
            break;
    }
}

#pragma mark - PwdInputViewDelegate
- (void)passwordInputViewDidChange:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"正在输入");
}
- (void)passwordInputViewCompleteInput:(CLPasswordInputView *)passwordInputView {
    //NSLog(@"输入完毕");
    if (passwordInputView == self.pwdInputViewFirst) {
        [self validationPwd:self.pwdInputViewFirst.text];
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
- (IBAction)nextBtnFirstClick:(UIButton *)sender {
    if (self.longPwdFirstTextField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The password cannot be empty", nil)];
        return;
    }

    if(self.longPwdFirstTextField.text.length < 8 || self.longPwdFirstTextField.text.length > 34) {
        [kTools tipMessage:MyLocalizedString(@"The password length is between 8 and 34 digits", nil)];
        return;
    }
    if ([self.longPwdFirstTextField.text containsChinese]) {
        [kTools tipMessage:MyLocalizedString(@"The password cannot contain Chinese", nil)];
        return;
    }
    [self validationPwd:self.longPwdFirstTextField.text];
}

- (void)validationPwd:(NSString *)pwd
{
    id result = [kPyCommandsManager callInterface:kInterfacecheck_password parameter:@{@"password":pwd}];
    if (result != nil) {
        if (self.block) {
            self.block(pwd);
            if (_isDis == YES) {
                [self dismissViewControllerAnimated:YES completion:nil];
            }
        }
    }
}


- (IBAction)eyeBtnClick:(UIButton *)sender {
    self.eyeBtnFirst.selected = !self.eyeBtnFirst.selected;
    self.longPwdFirstTextField.secureTextEntry = !self.eyeBtnFirst.selected;
}

@end
