//
//  OKDeviceSuccessViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKDeviceSuccessViewController.h"

@interface OKDeviceSuccessViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UIView *nameBgView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *completeBtn;
- (IBAction)completeBtnClick:(UIButton *)sender;
@end

@implementation OKDeviceSuccessViewController

+ (instancetype)deviceSuccessViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKDeviceSuccessViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Wallet activation successful", nil);
    self.iconImageView.image = [UIImage imageNamed:@"device_success"];
    [self.descLabel setText:MyLocalizedString(@"Your hardware wallet has been successfully activated and we have nothing to remind you of. In a word, please take good care of it. No one can help you get it back. I wish you play in the chain of blocks in the world happy", nil) lineSpacing:20];
    [self.completeBtn setLayerDefaultRadius];
    [self.nameBgView setLayerDefaultRadius];
}


- (IBAction)completeBtnClick:(UIButton *)sender {
    
    
}

@end
