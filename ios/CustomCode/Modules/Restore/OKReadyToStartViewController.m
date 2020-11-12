//
//  OKReadyToStartViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKReadyToStartViewController.h"
#import "OKBackUpViewController.h"

@interface OKReadyToStartViewController ()
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UILabel *tips2Label;
@property (weak, nonatomic) IBOutlet UILabel *tips3Label;
@property (weak, nonatomic) IBOutlet UIButton *startBtn;
- (IBAction)startBtnClick:(UIButton *)sender;
@property (nonatomic,copy)NSString *words;
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
    [self setNavigationBarBackgroundColorWithClearColor];
    self.tips1Label.text = MyLocalizedString(@"Be ready to copy down your mnemonic", nil);
    self.tips2Label.text = MyLocalizedString(@"Once your phone is lost or stolen, you can use mnemonics to recover your entire wallet, take out paper and pen, let's get started", nil);
    self.tips3Label.text = MyLocalizedString(@"A standalone wallet does not support backing up to a hardware device", nil);
    [self.startBtn setTitle:MyLocalizedString(@"Ready to star", nil) forState:UIControlStateNormal];
    self.title = MyLocalizedString(@"Backup the purse", nil);
    [self.startBtn setLayerDefaultRadius];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.words = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":self.pwd}];
}

- (IBAction)startBtnClick:(UIButton *)sender {
    OKBackUpViewController *backUpVc = [OKBackUpViewController backUpViewController];
    backUpVc.words = [self.words componentsSeparatedByString:@" "];
    backUpVc.showType = WordsShowTypeRestore;
    [self.navigationController pushViewController:backUpVc animated:YES];
}

@end
