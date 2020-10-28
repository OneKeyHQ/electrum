//
//  OKMineTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/20.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKMineTableViewCell.h"
#import "OKMineTableViewCellModel.h"

@interface OKMineTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *rightArrow;

@end

@implementation OKMineTableViewCell

- (void)setModel:(OKMineTableViewCellModel *)model
{
    _model = model;
    
    self.iconView.image = [UIImage imageNamed:model.imageName];
    self.titleLabel.text = model.menuName;
    
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
