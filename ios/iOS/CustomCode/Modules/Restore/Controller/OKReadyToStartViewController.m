//
//  OKReadyToStartViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKReadyToStartViewController.h"
#import "OKBackUpViewController.h"
#import "OKDontScreenshotTipsViewController.h"

@interface OKReadyToStartViewController ()
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UILabel *tips2Label;
@property (weak, nonatomic) IBOutlet UILabel *tips3Label;
@property (weak, nonatomic) IBOutlet UIButton *startBtn;
@property (weak, nonatomic) IBOutlet UIView *bottomBtnBgView;
@property (weak, nonatomic) IBOutlet UILabel *dontwanttocopyLabel;
- (IBAction)startBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *onekeyTipsLabel;
@end

@implementation OKReadyToStartViewController
+ (instancetype)readyToStartViewController
{
    return  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKReadyToStartViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Backup the purse", nil);
    _type = [kWalletManager getWalletDetailType];
    [self setNavigationBarBackgroundColorWithClearColor];
    self.tips1Label.text = MyLocalizedString(@"Be ready to copy down your mnemonic", nil);
    self.tips2Label.text = MyLocalizedString(@"Once your phone is lost or stolen, you can use mnemonics to recover your entire wallet, take out paper and pen, let's get started", nil);
    self.tips3Label.text = MyLocalizedString(@"A standalone wallet does not support backing up to a hardware device", nil);
    [self.startBtn setTitle:MyLocalizedString(@"Ready to star", nil) forState:UIControlStateNormal];
    self.title = MyLocalizedString(@"Backup the purse", nil);
    
    [self.startBtn setLayerDefaultRadius];
    
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(backUpToHardwareClick)];
    [self.bottomBtnBgView addGestureRecognizer:tap];
    
    if (self.isExport) {
        self.dontwanttocopyLabel.hidden = YES;
        self.bottomBtnBgView.hidden = YES;
        self.onekeyTipsLabel.hidden = YES;
        self.tips3Label.hidden = YES;
    }else{
        if (_type == OKWalletTypeIndependent) {
            self.dontwanttocopyLabel.hidden = YES;
            self.bottomBtnBgView.hidden = YES;
            self.onekeyTipsLabel.hidden = YES;
            self.tips3Label.hidden = NO;
        }else{
            self.dontwanttocopyLabel.hidden = NO;
            self.bottomBtnBgView.hidden = NO;
            self.onekeyTipsLabel.hidden = NO;
            self.tips3Label.hidden = YES;
        }
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (IBAction)startBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    if (self.isExport) {
        OKDontScreenshotTipsViewController *dontScreenshotTipsVc = [OKDontScreenshotTipsViewController dontScreenshotTipsViewController:^{
            OKBackUpViewController *backUpVc = [OKBackUpViewController backUpViewController];
            backUpVc.words = [self.words componentsSeparatedByString:@" "];
            backUpVc.showType = WordsShowTypeHDExport;
            [weakself.OK_TopViewController.navigationController pushViewController:backUpVc animated:YES];
        }];
        dontScreenshotTipsVc.modalPresentationStyle = UIModalPresentationOverCurrentContext;
        [weakself.OK_TopViewController presentViewController:dontScreenshotTipsVc animated:NO completion:nil];
    }else{
        OKBackUpViewController *backUpVc = [OKBackUpViewController backUpViewController];
        backUpVc.words = [self.words componentsSeparatedByString:@" "];
        backUpVc.showType = WordsShowTypeRestore;
        backUpVc.walletName = self.walletName;
        [self.navigationController pushViewController:backUpVc animated:YES];
    }
}

- (void)backUpToHardwareClick
{
    NSLog(@"备份到硬件钱包");
}

@end
