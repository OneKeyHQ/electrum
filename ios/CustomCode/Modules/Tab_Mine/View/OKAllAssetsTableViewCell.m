//
//  OKAllAssetsTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKAllAssetsTableViewCell.h"
#import "OKAllAssetsTableViewCellModel.h"

@interface OKAllAssetsTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *cointypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *moneyLabel;

@end


@implementation OKAllAssetsTableViewCell

- (void)setModel:(OKAllAssetsTableViewCellModel *)model
{
    _model = model;
    
    self.iconImageView.image = [UIImage imageNamed:@"token_btc"];
    self.cointypeLabel.text = model.name;
    self.balanceLabel.text = model.btc;
    self.moneyLabel.text = model.fiat;
    
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
