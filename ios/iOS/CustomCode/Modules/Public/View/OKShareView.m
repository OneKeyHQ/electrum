//
//  OKShareView.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/26.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKShareView.h"

@interface OKShareView()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *descTopLabel;
@property (weak, nonatomic) IBOutlet UIImageView *qrImageView;
@property (weak, nonatomic) IBOutlet UILabel *descBottomLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIView *logoBgView;
@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UILabel *tuijianLabel;
@property (weak, nonatomic) IBOutlet UILabel *urlLabel;
@property (weak, nonatomic) IBOutlet UIView *qrBgView;

@end

@implementation OKShareView

- (void)awakeFromNib
{
    [super awakeFromNib];
}

+(OKShareView *)initViewWithImage:(UIImage *)image coinType:(NSString *)coinType address:(NSString *)address;
{
    OKShareView *shareView = [[[NSBundle mainBundle] loadNibNamed:@"OKShareView" owner:self options:nil] firstObject];
    shareView.qrImageView.image = image;
    shareView.iconImageView.image = [UIImage imageNamed:[NSString stringWithFormat:@"token_%@",[coinType lowercaseString]]];
    shareView.descTopLabel.text = [NSString stringWithFormat:@"%@ %@",MyLocalizedString(@"Scan transfer", nil),[coinType uppercaseString]];
    shareView.addressLabel.text = address;
    shareView.descBottomLabel.text = [NSString stringWithFormat:@"%@ %@",coinType,MyLocalizedString(@"The wallet address", nil)];
    shareView.tuijianLabel.text = MyLocalizedString(@"It is recommended to use", nil);
    shareView.urlLabel.text = @"download.onekey.so";
    [shareView.qrBgView setLayerRadius:20];
    [shareView layoutIfNeeded];
    return shareView;
}

@end
