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
    NSString *iconName =  [NSString stringWithFormat:@"token_%@",[model.coin lowercaseString]];
    self.iconImageView.image = [UIImage imageNamed:iconName];
    self.checkBtn.selected = model.isSelected;
    self.walletNameLabel.text = model.label;
    self.balanceLabel.text = model.blance;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
