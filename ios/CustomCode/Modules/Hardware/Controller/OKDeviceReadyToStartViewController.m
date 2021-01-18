//
//  OKDeviceReadyToStartViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceReadyToStartViewController.h"
#import "OKChangeMnemonicLenController.h"

@interface OKDeviceReadyToStartViewController ()
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UIButton *startBtn;
@property (weak, nonatomic) IBOutlet UIView *bottomBtnBgView;
@property (weak, nonatomic) IBOutlet UILabel *dontwanttocopyLabel;
- (IBAction)startBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *onekeyTipsLabel;
@property (weak, nonatomic) IBOutlet UILabel *changeLabel;

@property (nonatomic,assign)OKMnemonicLengthType type;
@end

@implementation OKDeviceReadyToStartViewController
+ (instancetype)deviceReadyToStartViewController
{
    return  [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeviceReadyToStartViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Activate the hardware wallet", nil);
    [self setNavigationBarBackgroundColorWithClearColor];
    self.tips1Label.text = MyLocalizedString(@"Be ready to copy down your mnemonic", nil);
    self.onekeyTipsLabel.text = MyLocalizedString(@"", nil);
    [self.startBtn setTitle:MyLocalizedString(@"Ready to star", nil) forState:UIControlStateNormal];
    [self.startBtn setLayerDefaultRadius];
    _type = OKMnemonicLengthType12;
    [self updateLabelUI];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(changeMnemonicLength)];
    [self.bottomBtnBgView addGestureRecognizer:tap];
}

- (void)updateLabelUI
{
    switch (_type) {
        case OKMnemonicLengthType12:
        {
            self.onekeyTipsLabel.text = MyLocalizedString(@"The hardware wallet will generate 12-digit mnemonic words for you", nil);
            self.changeLabel.text = MyLocalizedString(@"Use 12-digit mnemonic words", nil);
        }
            break;
        case OKMnemonicLengthType24:
        {
            self.onekeyTipsLabel.text = MyLocalizedString(@"The hardware wallet will generate 24-digit mnemonic words for you", nil);
            self.changeLabel.text = MyLocalizedString(@"Use 24-digit mnemonic words", nil);
        }
            break;
        default:
            break;
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (IBAction)startBtnClick:(UIButton *)sender {
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [kPyCommandsManager callInterface:kInterfaceinit parameter:@{@"label":self.deviceName}];
    });
}

- (void)changeMnemonicLength
{
    OKWeakSelf(self)
    OKChangeMnemonicLenController *changeVc = [OKChangeMnemonicLenController changeMnemonicLenController];
    [changeVc showOnWindowWithParentViewController:self block:^(OKMnemonicLengthType type) {
        weakself.type = type;
        [self updateLabelUI];
    }];
}

@end
