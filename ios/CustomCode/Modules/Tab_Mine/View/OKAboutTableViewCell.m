//
//  OKAboutTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKAboutTableViewCell.h"
#import "OKAboutTableViewCellModel.h"

@interface OKAboutTableViewCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;



@end

@implementation OKAboutTableViewCell


- (void)setModel:(OKAboutTableViewCellModel *)model
{
    _model = model;
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
