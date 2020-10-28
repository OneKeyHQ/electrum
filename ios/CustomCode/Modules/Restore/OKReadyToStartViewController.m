//
//  OKReadyToStartViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKReadyToStartViewController.h"

@interface OKReadyToStartViewController ()
@property (weak, nonatomic) IBOutlet UILabel *tips1Label;
@property (weak, nonatomic) IBOutlet UILabel *tips2Label;
@property (weak, nonatomic) IBOutlet UILabel *tips3Label;
@property (weak, nonatomic) IBOutlet UIButton *startBtn;
- (IBAction)startBtnClick:(UIButton *)sender;
@end

@implementation OKReadyToStartViewController

+ (instancetype)readyToStartViewController
{
    return  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKReadyToStartViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.tips1Label.text = MyLocalizedString(@"Be ready to copy down your mnemonic", nil);
    self.tips2Label.text = MyLocalizedString(@"Once your phone is lost or stolen, you can use mnemonics to recover your entire wallet, take out paper and pen, let's get started", nil);
    self.tips3Label.text = MyLocalizedString(@"A standalone wallet does not support backing up to a hardware device", nil);
    [self.startBtn setTitle:MyLocalizedString(@"Ready to star", nil) forState:UIControlStateNormal];
    self.title = MyLocalizedString(@"Backup the purse", nil);
    [self.startBtn setLayerDefaultRadius];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)startBtnClick:(UIButton *)sender {
    
    NSLog(@"点击了准备开始");

}
@end
