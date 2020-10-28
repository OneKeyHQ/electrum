//
//  OKPwdViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKPwdViewController.h"
#import "CLPasswordInputView.h"

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

@property (nonatomic,strong)OKPythonExecute *pythonExecute;

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
    self.title = MyLocalizedString(@"Create a new wallet", nil);
    self.titleLabelFirst.text  = MyLocalizedString(@"Set the password", nil);
    self.titleDescLabelFirst.text = MyLocalizedString(@"Only you can unlock your wallet", nil);
    self.titleLabelSecond.text  = MyLocalizedString(@"Enter your password again", nil);
    self.titleDescLabelSecond.text = MyLocalizedString(@"Don't reveal your password to anyone else", nil);
    
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
        if ([self.pwdInputViewFirst.text isEqualToString:self.pwdInputViewSecond.text] && self.pwdInputViewFirst.text.length > 0) {//两次输入相同 加密种子进入下一步
            //创建钱包加密保存
            [self passwordIsCorrect];
        }else{
            [self pwdWrongTip];
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
    if ([self.longPwdFirstTextField.text isEqualToString:self.longPwdSecondTextField.text]&&self.longPwdFirstTextField.text.length > 0) { //密码一致
        [self passwordIsCorrect];
    }else{
        [self pwdWrongTip];
    }
}

#pragma mark -  两次输入密码相同
- (void)passwordIsCorrect
{
    OKPythonExecute *pythonExecute = [[OKPythonExecute alloc] initWithModuleDirName:@"android" moduleName:@"console"];
    _pythonExecute = pythonExecute;
    NSDictionary *params = @{@"kkkk":@"helo"};
    [pythonExecute executeWithClass:@"AndroidCommands" methodName:@"create_hd_wallet" parameter:params success:^(id result) {
        NSLog(@"===resutl = %@",result);
    } fail:^(NSError *error) {
        NSLog(@"====error = %@",error.domain);
    }];
}


#pragma mark - 输入错误的提示信息
- (void)pwdWrongTip
{
    NSLog(@"两次密码不同，请重新输入");
}
@end
