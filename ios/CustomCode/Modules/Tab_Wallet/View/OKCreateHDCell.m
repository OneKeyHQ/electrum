//
//  OKCreateHDCell.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/10/9.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKCreateHDCell.h"
#import "OKSelectCellModel.h"

@interface  OKCreateHDCell()

@property (weak, nonatomic) IBOutlet UIImageView *icon;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *descMoreLabel;

@end


@implementation OKCreateHDCell
- (void)setModel:(OKSelectCellModel *)model
{
    _model = model;
    self.icon.image = [UIImage imageNamed:model.imageStr];
    self.titleLabel.text = model.titleStr;
    self.descLabel.text = model.descStr;
    self.descMoreLabel.text = model.descStrL;
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
