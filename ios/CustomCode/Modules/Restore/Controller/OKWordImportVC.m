//
//  OKWordImportVC.m
//  OneKey
//
//  Created by xiaoliang on 2020/9/28.
//

#import "OKWordImportVC.h"
#import "OKWordImportView.h"
#import "OKBiologicalViewController.h"
#import "OKPwdViewController.h"
#import "OKFindFollowingWalletController.h"

@interface OKWordImportVC ()<UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIView *leftBgView;
@property (weak, nonatomic) IBOutlet UIView *fromBgView;

@end

@implementation OKWordImportVC

+ (OKWordImportVC *)initViewController {
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"importWords" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:NSStringFromClass(self)];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(backToPrevious)];
    [_wordInputView configureData:_wordsArr];
    [self checkButtonEnabled];
    [self.nextBtn setTitle:MyLocalizedString(@"restore", nil) forState:UIControlStateNormal];
    [self.leftBgView setLayerRadius:2];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapFromClick)];
    [self.fromBgView addGestureRecognizer:tap];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    // 关闭键盘事件相应 (解决IQKeyboard导致页面上移问题)
    [IQKeyboardManager sharedManager].enable = NO;
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    // 打开键盘事件相应
    [IQKeyboardManager sharedManager].enable = YES;
}

-(void)awakeFromNib {
    [super awakeFromNib];
    self.title = MyLocalizedString(@"Recover HD Wallet", nil);
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [self.view endEditing:YES];
}

- (void)checkButtonEnabled {
    OKWeakSelf(self)
    self.wordInputView.completed = ^(BOOL isCompleted) {
        if (isCompleted) {
            [weakself.nextBtn enabled:YES alpha:1];
        } else {
           NSArray *arrays = [[UIPasteboard generalPasteboard].string componentsSeparatedByString:@" "];
            if (arrays.count == 12 || arrays.count == 15 || arrays.count == 18 || arrays.count == 21 || arrays.count == 24) {
                [kTools tipMessage:MyLocalizedString(@"Incorrect phrase", nil)];
                [UIPasteboard removePasteboardWithName:UIPasteboardNameGeneral];
            }
            [weakself.nextBtn enabled:NO alpha:0.5];
        }
    };
}

- (IBAction)next:(id)sender {
    OKWeakSelf(self)
    if (_wordInputView.wordsArr.count == 12 || _wordInputView.wordsArr.count == 24) {
        __block NSString *mnemonicStr = [_wordInputView.wordsArr componentsJoinedByString:@" "];
        if ([kWalletManager checkIsHavePwd]) {
            [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                [weakself createWallet:pwd mnemonicStr:mnemonicStr];
            }];
        }else{
            OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
                [weakself createWallet:pwd mnemonicStr:mnemonicStr];
            }];
            [self.navigationController pushViewController:pwdVc animated:YES];
        }
    }else{
        [kTools tipMessage:MyLocalizedString(@"Incorrect phrase", nil)];
    }
}

- (void)createWallet:(NSString *)pwd mnemonicStr:(NSString *)mnemonicStr
{
    NSString *seed = mnemonicStr;
    __block NSArray *restoreHD = [NSArray array];
    [kTools showIndicatorView];
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        restoreHD =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
        if (restoreHD != nil) {
            dispatch_sync(dispatch_get_main_queue(), ^{
                if (restoreHD.count == 0) {
                    [kPyCommandsManager callInterface:kInterfacerecovery_confirmed parameter:@{@"name_list":@[]}];
                    NSString *defaultName = @"BTC-1";
                    [OKStorageManager saveToUserDefaults:defaultName key:kCurrentWalletName];
                    NSString *cuurentWalletAddress = [kWalletManager getCurrentWalletAddress:defaultName];
                    [OKStorageManager saveToUserDefaults:cuurentWalletAddress key:kCurrentWalletAddress];
                    [OKStorageManager saveToUserDefaults:@"btc-hd-standard" key:kCurrentWalletType];
                    OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" biologicalViewBlock:^{
                        //创建HD成功刷新首页的UI
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"1"}];;
                    }];
                    [kTools hideIndicatorView];
                    [weakself.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
                    
                }else{
                    OKFindFollowingWalletController *findFollowingWalletVc = [OKFindFollowingWalletController findFollowingWalletController];
                    findFollowingWalletVc.pwd = pwd;
                    findFollowingWalletVc.restoreHD = restoreHD;
                    [kTools hideIndicatorView];
                    [weakself.OK_TopViewController.navigationController pushViewController:findFollowingWalletVc animated:YES];
                }
            });
        }else{
            [kTools hideIndicatorView];
        }
    });
}



#pragma mark - scrollView
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self.view endEditing:YES];
}

#pragma mark - backToPrevious
- (void)backToPrevious
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)tapFromClick
{
    NSLog(@"tapFromClick");
}
@end
