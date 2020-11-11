//
//  OKTradeSettingViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTradeSettingViewCell.h"

@interface OKTradeSettingViewCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UISwitch *rightSwitch;
- (IBAction)rightSwitchClick:(id)sender;
@end

@implementation OKTradeSettingViewCell

- (void)setModel:(OKTradeSettingViewCellModel *)model
{
    _model = model;
    self.titleLabel.text = model.titleStr;
    self.rightSwitch.on = model.switchOn;
}


- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)rightSwitchClick:(id)sender {
}
@end
