//
//  OKWordImportVC.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import "OKWordImportVC.h"
#import "OKWordImportView.h"

@interface OKWordImportVC ()

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;
@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;

@end

@implementation OKWordImportVC

+ (OKWordImportVC *)initViewController {
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"importWords" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:NSStringFromClass(self)];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self checkButtonEnabled];
    [_wordInputView configureData:_wordsArr];

    [self.nextBtn setTitle:MyLocalizedString(@"Next", nil) forState:UIControlStateNormal];
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
    self.wordInputView.completed = ^(BOOL isCompleted) {
        if (isCompleted) {
            [_nextBtn enabled:YES alpha:1];
        } else {
           NSArray *arrays = [[UIPasteboard generalPasteboard].string componentsSeparatedByString:@" "];
            if (arrays.count == 12 || arrays.count == 15 || arrays.count == 18 || arrays.count == 21 || arrays.count == 24) {
//                JZWeakSelf(self)
//                [kTools showMessage:MyLocalizedString(@"Incorrect phrase", nil) HUDType:ATProgressHUDTypeFailure onView:weakself.view];
                [UIPasteboard removePasteboardWithName:UIPasteboardNameGeneral];
            }
            [_nextBtn enabled:NO alpha:0.5];
        }
    };
}

- (IBAction)next:(id)sender {
    NSString *mnemonicStr = [_wordInputView.wordsArr componentsJoinedByString:@" "];
//    NSData *data = [ATCreateWalletService mnemonicProcess:&mnemonicStr wordType:nil];
//
//    if (data) {
//        ATImportWalletVC *vc = [ATImportWalletVC initViewController];
//        vc.isFromSpread = YES;
//        vc.wordsArr = _wordInputView.wordsArr;
//        vc.enterPath = self.enterPath;
//        [self.navigationController pushViewController:vc animated:YES];
//    }else{
//        [kTools showMessage:MyLocalizedString(@"Incorrect phrase", nil) HUDType:ATProgressHUDTypeFailure onView:self.view];
//    }
}

@end
