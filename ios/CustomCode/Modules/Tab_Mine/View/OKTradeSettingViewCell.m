//
//  OKTradeSettingViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
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

- (IBAction)rightSwitchClick:(UISwitch *)sender {
    
    if ([self.delegate respondsToSelector:@selector(switchClick:on:)]) {
        [self.delegate switchClick:self.model on:sender.isOn];
    }
}
@end
