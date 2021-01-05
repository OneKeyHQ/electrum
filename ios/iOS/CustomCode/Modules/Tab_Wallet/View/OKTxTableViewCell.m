//
//  OKTxTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
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
    self.statusImageView.image = [UIImage imageNamed:[model.is_mine boolValue] == YES ?@"txout":@"txin"];
    self.statusLabel.text = model.tx_status;
    self.timeLabel.text = model.date;
    NSArray *amountArray = [model.amount componentsSeparatedByString:@"("];
    NSString *bStr = [NSString stringWithFormat:@"%@%@",[model.is_mine boolValue] == NO ? @"+":@"-" ,[amountArray firstObject]];
    self.amountLabel.text = bStr;
    self.addressLabel.text = model.address;
    
    if ([model.is_mine boolValue]) {
        self.amountLabel.textColor = HexColor(0x3E70F2);
    }else{
        self.amountLabel.textColor = HexColor(0x00B812);
    }
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
