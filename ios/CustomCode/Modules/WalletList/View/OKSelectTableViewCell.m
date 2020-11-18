//
//  OKSelectTableViewCell.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSelectTableViewCell.h"
#import "OKSelectCellModel.h"

@interface OKSelectTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *icon;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@end


@implementation OKSelectTableViewCell

- (void)setModel:(OKSelectCellModel *)model
{
    _model = model;
    self.icon.image = [UIImage imageNamed:model.imageStr];
    self.titleLabel.text = model.titleStr;
    self.descLabel.text = model.descStr;
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
