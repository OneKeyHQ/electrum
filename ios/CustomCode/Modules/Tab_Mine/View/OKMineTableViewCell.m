//
//  OKMineTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/20.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKMineTableViewCell.h"
#import "OKMineTableViewCellModel.h"

@interface OKMineTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *rightArrow;
@property (weak, nonatomic) IBOutlet UISwitch *rightSwitch;
- (IBAction)switchClick:(UISwitch *)sender;
@end

@implementation OKMineTableViewCell

- (void)setModel:(OKMineTableViewCellModel *)model
{
    _model = model;
    
    self.iconView.image = [UIImage imageNamed:model.imageName];
    self.titleLabel.text = model.menuName;
    
    if (_model.isAuth) {
        self.rightSwitch.hidden = NO;
        self.rightArrow.hidden = YES;
        [self.rightSwitch setOn:kWalletManager.isOpenAuthBiological];
    }else{
        self.rightSwitch.hidden = YES;
        self.rightArrow.hidden = NO;
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

- (IBAction)switchClick:(UISwitch *)sender {
    if ([self.delegate respondsToSelector:@selector(mineTableViewCellModelDelegateSwitch:)]) {
        [self.delegate mineTableViewCellModelDelegateSwitch:sender];
    }
}
@end
