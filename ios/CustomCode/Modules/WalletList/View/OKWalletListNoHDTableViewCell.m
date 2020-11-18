//
//  OKWalletListNoHDTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletListNoHDTableViewCell.h"
#import "OKWalletListNoHDTableViewCellModel.h"

@interface OKWalletListNoHDTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIView *bgView;
@end

@implementation OKWalletListNoHDTableViewCell

- (void)setModel:(OKWalletListNoHDTableViewCellModel *)model
{
    _model = model;
    self.iconImageView.image = [UIImage imageNamed:model.iconName];
    self.titleLabel.text = model.titleStr;
    self.descLabel.text = model.descStr;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    [self.bgView setLayerRadius:20];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
