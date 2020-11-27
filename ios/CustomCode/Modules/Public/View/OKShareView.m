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
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descTopLabel;
@property (weak, nonatomic) IBOutlet UIImageView *qrImageView;
@property (weak, nonatomic) IBOutlet UILabel *descBottomLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIView *logoBgView;
@property (weak, nonatomic) IBOutlet UIView *bgView;

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
    shareView.titleLabel.text = [NSString stringWithFormat:@"%@ %@",coinType,MyLocalizedString(@"Payment code", nil)];
    shareView.descTopLabel.text = MyLocalizedString(@"Open OneKey scan", nil);
    shareView.addressLabel.text = address;
    shareView.descBottomLabel.text = MyLocalizedString(@"The wallet address", nil);
    [shareView layoutIfNeeded];
    return shareView;
}

@end
