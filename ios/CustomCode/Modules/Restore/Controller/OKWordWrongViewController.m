//
//  OKWordWrongViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/4.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWordWrongViewController.h"

@interface OKWordWrongViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *iKnowBtn;
- (IBAction)iKnowBtnClick:(UIButton *)sender;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@end

@implementation OKWordWrongViewController

+ (instancetype)wordWrongViewController:(BlockComplete)block
{
    OKWordWrongViewController *vc = [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKWordWrongViewController"];
    vc.block = block;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.titleLabel.text = MyLocalizedString(@"The mnemonic is incorrect", nil);
    [self.iKnowBtn setTitle:MyLocalizedString(@"To check the", nil) forState:UIControlStateNormal];
    [self.iKnowBtn setLayerRadius:20];
    [self.contentView setLayerRadius:20];
}

- (IBAction)iKnowBtnClick:(UIButton *)sender {
    if (self.block) {
        self.block();
    }
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
