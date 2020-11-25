//
//  OKScreenshotsTipsController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/21.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKScreenshotsTipsController.h"

@interface OKScreenshotsTipsController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIView *contentView;

@end

@implementation OKScreenshotsTipsController

+ (instancetype)screenshotsTipsController
{
    return [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKScreenshotsTipsController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.titleLabel.text = MyLocalizedString(@"You seem to have taken a screenshot just now", nil);
    self.detailLabel.attributedText = [NSString lineSpacing:30 content:MyLocalizedString(@"Mobile photo albums are very insecure and can be accessed by any app. Go to the phone photo album immediately and permanently delete the screen capture. Your future self will thank you for your decision now.", nil)];
    [self.contentView setLayerRadius:20];
}
- (IBAction)closeBtnClick:(UIButton *)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}
@end
