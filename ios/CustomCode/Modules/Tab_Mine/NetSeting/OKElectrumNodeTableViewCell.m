//
//  OKElectrumNodeTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKElectrumNodeTableViewCell.h"
#import "OKElectrumNodeModel.h"
@interface OKElectrumNodeTableViewCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@end


@implementation OKElectrumNodeTableViewCell

- (void)setModel:(OKElectrumNodeModel *)model
{
    _model = model;
    NSString *server = [NSString stringWithFormat:@"%@:%@",model.ip,model.s];
    if ([server isEqualToString:kUserSettingManager.electrum_server]) {
        self.checkImageView.hidden = NO;
    }else{
        self.checkImageView.hidden = YES;
    }
    self.titleLabel.text = [NSString stringWithFormat:@"%@:%@",model.ip,model.s];
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
