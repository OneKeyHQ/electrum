//
//  OKBluetoothViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/18.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKBluetoothViewCell.h"
#import "OKBluetoothViewCellModel.h"

@interface OKBluetoothViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UIView *bgView;

@end



@implementation OKBluetoothViewCell

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {

    }
    return self;
}

- (void)setModel:(OKBluetoothViewCellModel *)model
{
    _model = model;
    self.nameLabel.text = model.blueName;
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
