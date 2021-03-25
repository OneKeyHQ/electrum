//
//  OKFindWalletTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFindWalletTableViewCell.h"
#import "OKFindWalletTableViewCellModel.h"

@interface  OKFindWalletTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UIButton *checkBtn;
@property (weak, nonatomic) IBOutlet UIView *cellBgView;
@property (weak, nonatomic) IBOutlet OKLabel *rightDescLabel;
@property (weak, nonatomic) IBOutlet UIImageView *exitImageView;
@end


@implementation OKFindWalletTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setModel:(OKFindWalletTableViewCellModel *)model
{
    _model = model;
    [self.cellBgView setLayerRadius:20];
    NSString *cointype = model.coin == nil ? model.coin_type : model.coin;
    NSString *iconName =  [NSString stringWithFormat:@"token_%@",[cointype lowercaseString]];
    self.iconImageView.image = [UIImage imageNamed:iconName];
    self.checkBtn.selected = model.isSelected;
    self.walletNameLabel.text = model.label;
    self.balanceLabel.text = model.blance;

    if (model.exist) {
        self.checkBtn.hidden = YES;
        self.checkBtn.userInteractionEnabled = NO;
        self.exitImageView.hidden = NO;
        self.rightDescLabel.hidden = NO;
    }else{
        self.checkBtn.hidden = NO;
        self.checkBtn.userInteractionEnabled = NO;
        self.exitImageView.hidden = YES;
        self.rightDescLabel.hidden = YES;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
