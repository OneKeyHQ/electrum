//
//  OKDeleteBackUpTipsController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/2.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKDeleteBackUpTipsController.h"

@interface OKDeleteBackUpTipsController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *iKnowBtn;
- (IBAction)iKnowBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (nonatomic,copy)DeleteBackUpTipBlock block;
@end

@implementation OKDeleteBackUpTipsController
+ (instancetype)deleteBackUpTipsController:(DeleteBackUpTipBlock)block
{
    OKDeleteBackUpTipsController *vc = [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeleteBackUpTipsController"];
    vc.block = block;
    return vc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.titleLabel.text = MyLocalizedString(@"Make a backup of your wallet before deleting it", nil);
    [self.iKnowBtn setTitle:MyLocalizedString(@"return", nil) forState:UIControlStateNormal];
    self.detailLabel.attributedText = [NSString lineSpacing:10 content:MyLocalizedString(@"This action deletes all data for the wallet. In order to avoid loss of your property, please complete the backup of your wallet first.", nil)];
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
