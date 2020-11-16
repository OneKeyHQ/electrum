//
//  OKFindWalletTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKFindWalletTableViewCell.h"
#import "OKFindWalletTableViewCellModel.h"

@interface  OKFindWalletTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;
@end


@implementation OKFindWalletTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setModel:(OKFindWalletTableViewCellModel *)model
{
    _model = model;
    self.iconImageView.image = [UIImage imageNamed:model.iconName];
    self.walletNameLabel.text = model.walletName;
    self.balanceLabel.text = model.balanceName;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
