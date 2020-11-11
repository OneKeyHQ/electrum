//
//  OKSelectCoinTypeTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKSelectCoinTypeTableViewCell.h"
#import "OKSelectCoinTypeTableViewCellModel.h"

@interface OKSelectCoinTypeTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@end

@implementation OKSelectCoinTypeTableViewCell

- (void)setModel:(OKSelectCoinTypeTableViewCellModel *)model
{
    _model = model;
    self.iconImageView.image = [UIImage imageNamed:model.iconName];
    self.titleLabel.text = model.titleString;
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
