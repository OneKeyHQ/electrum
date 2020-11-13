//
//  OKDontScreenshotTipsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/12.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKDontScreenshotTipsViewController.h"
#import "OKBackUpViewController.h"

@interface OKDontScreenshotTipsViewController ()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *iKnowBtn;
- (IBAction)iKnowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (nonatomic,copy)DontScreenBtnClick block;

@end

@implementation OKDontScreenshotTipsViewController
+ (instancetype)dontScreenshotTipsViewController:(DontScreenBtnClick)click;
{
    OKDontScreenshotTipsViewController *vc = [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDontScreenshotTipsViewController"];
    vc.block = click;
    return vc;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.、
    self.titleLabel.text = MyLocalizedString(@"Don't a screenshot", nil);
    [self.iKnowBtn setTitle:MyLocalizedString(@"The next step", nil) forState:UIControlStateNormal];
    self.detailLabel.attributedText = [NSString lineSpacing:30 content:MyLocalizedString(@"Mnemonics are very sensitive and private content, once someone else gets it, your assets may be lost, so do not take screenshots, and pay attention to your surrounding cameras", nil)];
    [self.iKnowBtn setLayerRadius:20];
    [self.contentView setLayerRadius:20];
}

- (IBAction)iKnowBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:^{
        if (self.block) {
            self.block();
        }
    }];
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
