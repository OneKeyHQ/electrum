//
//  OKAddBottomViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKAddBottomViewController.h"
#import "OKSelectCoinTypeViewController.h"

@interface OKAddBottomViewController ()
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *contentViewBottomConstraint;
@property (weak, nonatomic) IBOutlet UILabel *viewTitleLabel;

@property (weak, nonatomic) IBOutlet UIButton *createButton;
- (IBAction)createButtonClick:(UIButton *)sender;

@property (weak, nonatomic) IBOutlet UIButton *importButton;
- (IBAction)importButtonClick:(UIButton *)sender;
@property (nonatomic,copy)BtnClickBlock clickBlock;
@end

@implementation OKAddBottomViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.importButton setLayerBoarderColor:HexColor(0xBDBDBD) width:1 radius:20];
    [self.createButton setLayerDefaultRadius];
    [self.contentView setLayerDefaultRadius];
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
        self.clickBlock(BtnClickTypeCreate);
    }
}
- (IBAction)importButtonClick:(UIButton *)sender {
    if (self.clickBlock) {
        [self closeView:nil];
        self.clickBlock(BtnClickTypeImport);
    }
}
@end
