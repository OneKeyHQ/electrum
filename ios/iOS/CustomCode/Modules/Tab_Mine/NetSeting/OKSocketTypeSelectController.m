//
//  OKSocketTypeSelectController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/25.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKSocketTypeSelectController.h"

@interface OKSocketTypeSelectController ()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
- (IBAction)closeBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *socket4Label;
@property (weak, nonatomic) IBOutlet UILabel *socket5Label;
@property (weak, nonatomic) IBOutlet UIView *socket4BgView;
@property (weak, nonatomic) IBOutlet UIView *scoket5BgView;
@property (weak, nonatomic) IBOutlet UIView *contentView;

@property (nonatomic,copy)ConfirmBtnClick block;

@end

@implementation OKSocketTypeSelectController

+ (instancetype)socketTypeSelectController:(ConfirmBtnClick)block
{
    OKSocketTypeSelectController *socketVc = [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSocketTypeSelectController"];
    socketVc.block = block;
    return socketVc;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.titleLabel.text = MyLocalizedString(@"Select node type", nil);
    self.socket4Label.text = @"Socks4";
    self.socket5Label.text = @"Socks5";
    [self.contentView setLayerRadius:20];

    UITapGestureRecognizer *socket4Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(socket4TapClick)];
    [self.socket4BgView addGestureRecognizer:socket4Tap];
    
    UITapGestureRecognizer *socket5Tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(socket5TapClick)];
    [self.scoket5BgView addGestureRecognizer:socket5Tap];
    
}

- (IBAction)closeBtnClick:(UIButton *)sender {
    if (self.block) {
        self.block(@"");
    }
    [self dismissViewControllerAnimated:NO completion:nil];
}

- (void)socket4TapClick {
    if (self.block) {
        self.block(self.socket4Label.text);
        [self.OK_TopViewController dismissViewControllerAnimated:NO completion:nil];;
    }
}
- (void)socket5TapClick
{
    if (self.block) {
        self.block(self.socket5Label.text);
        [self.OK_TopViewController dismissViewControllerAnimated:NO completion:nil];;
    }
}
@end
