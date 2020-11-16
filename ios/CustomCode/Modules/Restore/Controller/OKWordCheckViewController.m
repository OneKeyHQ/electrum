//
//  OKWordCheckViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWordCheckViewController.h"
#import "OKCheckWordView.h"
#import "OKYouareDoneViewController.h"
#import "OKWordWrongViewController.h"

@interface OKWordCheckViewController ()<OKCheckWordViewDelegate>
@property (weak, nonatomic) IBOutlet UILabel *checktopLabel;
@property (weak, nonatomic) IBOutlet OKCheckWordView *checktopView;
@property (weak, nonatomic) IBOutlet UILabel *checkmidLabel;
@property (weak, nonatomic) IBOutlet OKCheckWordView *checkmidView;
@property (weak, nonatomic) IBOutlet UILabel *checkbottomLabel;
@property (weak, nonatomic) IBOutlet OKCheckWordView *checkbottomView;
@property (weak, nonatomic) IBOutlet UILabel *checkAgainLabel;
@property (weak, nonatomic) IBOutlet UIButton *checkBtn;
- (IBAction)checkBtnClick:(UIButton *)sender;

@property (nonatomic,assign)int indexTop;
@property (nonatomic,assign)int indexMid;
@property (nonatomic,assign)int indexBottom;

@property (nonatomic,copy)NSString *userTop;
@property (nonatomic,copy)NSString *userMid;
@property (nonatomic,copy)NSString *userBottom;


@end

@implementation OKWordCheckViewController

+ (instancetype)wordCheckViewController
{
    return [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKWordCheckViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Backup the purse", nil);
    [self.checktopView setLayerRadius:20];
    [self.checkmidView setLayerRadius:20];
    [self.checkbottomView setLayerRadius:20];
    
    self.indexTop = [self getRandomNumber:0 to:3];
    self.indexMid = [self getRandomNumber:4 to:7];
    self.indexBottom = [self getRandomNumber:8 to:11];
    
    self.checktopLabel.text = [NSString stringWithFormat:@"选择刚才抄写的第%d个单词",self.indexTop + 1];
    self.checkmidLabel.text = [NSString stringWithFormat:@"选择刚才抄写的第%d个单词",self.indexMid + 1];
    self.checkbottomLabel.text = [NSString stringWithFormat:@"选择刚才抄写的第%d个单词",self.indexBottom + 1];
    
    if (self.words.count > 0) {
        [self.checktopView configureData:[self getConfusedWords:self.indexTop] delegate:self];
        [self.checkmidView configureData:[self getConfusedWords:self.indexMid] delegate:self];
        [self.checkbottomView configureData:[self getConfusedWords:self.indexBottom] delegate:self];
    }
    [self checkBtnStatus];
}
-(int)getRandomNumber:(int)from to:(int)to
{
    return (int)(from + (arc4random() % ((to - from) + 1)));
}

- (NSArray *)getConfusedWords:(int)currentIndex
{
    NSMutableArray *cWords = [NSMutableArray array];
    while (cWords.count < 2) {
        int num =  [self getRandomNumber:0 to:11];
        if (num != currentIndex) {
            NSString *cWord =  self.words[num];
            if (![cWords containsObject:cWord]) {
                [cWords addObject:cWord];
            }
        }
    }
    int random = [self getRandomNumber:0 to:2];
    [cWords insertObject:self.words[currentIndex] atIndex:random];
    return cWords;
}


- (IBAction)checkBtnClick:(UIButton *)sender {
    if ([self.userTop isEqualToString:self.words[self.indexTop]] && [self.userMid isEqualToString:self.words[self.indexMid]] && [self.userBottom isEqualToString:self.words[self.indexBottom]]) {
        [kPyCommandsManager callInterface:kInterfacedelete_backup_info parameter:@{}];
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiBackUPWalletComplete object:nil];
        OKYouareDoneViewController *youareVc = [OKYouareDoneViewController youareDoneViewController];
        [self.navigationController pushViewController:youareVc animated:YES];
    }else{
        OKWeakSelf(self)
        OKWordWrongViewController *wordWrongVc = [OKWordWrongViewController wordWrongViewController:^{
            [weakself.checktopView clearStatus];
            [weakself.checkmidView clearStatus];
            [weakself.checkbottomView clearStatus];
            weakself.userTop = @"";
            weakself.userMid = @"";
            weakself.userBottom = @"";
            [weakself checkBtnStatus];
        }];
        wordWrongVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
        [self presentViewController:wordWrongVc animated:NO completion:nil];
    }
}

- (void)checkBtnStatus
{
    if (self.userTop.length == 0 || self.userMid.length == 0 || self.userBottom.length == 0) {
        self.checkBtn.enabled = NO;
        self.checkBtn.alpha = 0.5;
    }else{
        self.checkBtn.enabled = YES;
        self.checkBtn.alpha = 1.0;
    }
}

#pragma mark - OKCheckWordViewDelegate
- (void)checkWordView:(OKCheckWordView *)checkView delegateBtnClick:(NSString *)word
{
    if (checkView == self.checktopView) {
        self.userTop = word;
    }else if (checkView == self.checkmidView){
        self.userMid = word;
    }else if (checkView == self.checkbottomView){
        self.userBottom = word;
    }
    [self checkBtnStatus];
}
@end
