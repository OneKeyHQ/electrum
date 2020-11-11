//
//  OKLanguageTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKLanguageTableViewCell.h"
@interface OKLanguageTableViewCell()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;

@end
@implementation OKLanguageTableViewCell

- (void)setModel:(OKLanguageCellModel *)model
{
    _model = model;
    self.titleLabel.text = model.titleStr;
    self.checkImageView.hidden = !model.isSelected;
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
