//
//  OKAssetTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKAssetTableViewCell.h"

@interface  OKAssetTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *coinTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *moneyLabel;
@end


@implementation OKAssetTableViewCell

- (void)setModel:(OKAssetTableViewCellModel *)model
{
    _model = model;
    self.iconImageView.image = [UIImage imageNamed:model.iconImage == nil ? @"token_btc" :model.iconImage];
    self.coinTypeLabel.text = model.coinType;
    if (!kWalletManager.showAsset) {
        self.balanceLabel.text = [NSString stringWithFormat:@"%@ %@",model.balance,kWalletManager.currentBitcoinUnit];
        self.moneyLabel.text = model.money;
    }else{
        self.balanceLabel.text = @"****";
        self.moneyLabel.text = @"****";
    }
}


- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
