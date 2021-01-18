//
//  OKChangeMnemonicLenController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/16.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKChangeMnemonicLenController.h"

@interface OKChangeMnemonicLenController ()
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewBottomConstraint;
@property (weak, nonatomic) IBOutlet UILabel *viewTitleLabel;

@property (weak, nonatomic) IBOutlet UIButton *createButton;
- (IBAction)createButtonClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *importButton;
- (IBAction)importButtonClick:(UIButton *)sender;
@property (nonatomic,copy)BtnClickBlock clickBlock;
@property (weak, nonatomic) IBOutlet UILabel *label12;
@property (weak, nonatomic) IBOutlet UILabel *label24;
@end

@implementation OKChangeMnemonicLenController
+ (instancetype)changeMnemonicLenController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKChangeMnemonicLenController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.createButton setLayerDefaultRadius];
    [self.contentView setLayerDefaultRadius];
    self.label12.text = MyLocalizedString(@"12", nil);
    self.label24.text = MyLocalizedString(@"24", nil);
    self.viewTitleLabel.text = MyLocalizedString(@"Select the number of mnemonic words", nil);
}
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [UIView animateWithDuration:0.3 animations:^{
        self.contentViewBottomConstraint.constant = 30;
        [self.view layoutIfNeeded];
    }];
}
- (void)showOnWindowWithParentViewController:(UIViewController *)viewController block:(BtnClickBlock)block{
    [[[UIApplication sharedApplication] keyWindow] addSubview:self.view];
    [viewController addChildViewController:self];
    self.clickBlock = block;
    self.contentViewBottomConstraint.constant = -292;
}
- (IBAction)closeView:(id)sender {
    [self.view removeFromSuperview];
    [self removeFromParentViewController];
}

- (IBAction)createButtonClick:(UIButton *)sender {
    if (self.clickBlock) {
        [self closeView:nil];
        self.clickBlock(OKMnemonicLengthType12);
    }
}
- (IBAction)importButtonClick:(UIButton *)sender {
    if (self.clickBlock) {
        [self closeView:nil];
        self.clickBlock(OKMnemonicLengthType24);
    }
}
@end
