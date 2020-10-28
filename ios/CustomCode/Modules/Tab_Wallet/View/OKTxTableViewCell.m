//
//  OKTxTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTxTableViewCell.h"
#import "OKTxTableViewCellModel.h"

@interface OKTxTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *statusImageView;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *amountLabel;
@property (weak, nonatomic) IBOutlet UILabel *statusLabel;

@end



@implementation OKTxTableViewCell

- (void)setModel:(OKTxTableViewCellModel *)model
{
    _model = model;
    self.statusImageView.image = [UIImage imageNamed:model.imageStr];
    self.statusLabel.text = model.statusStr;
    self.timeLabel.text = model.timeStr;
    self.amountLabel.text = model.amountStr;
    self.addressLabel.text = model.addressStr;
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
