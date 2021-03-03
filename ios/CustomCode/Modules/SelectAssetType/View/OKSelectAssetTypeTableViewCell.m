//
//  OKFindWalletTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSelectAssetTypeTableViewCell.h"
#import "OKSelectAssetTypeModel.h"

@interface  OKSelectAssetTypeTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UIButton *checkBtn;
@property (weak, nonatomic) IBOutlet UIView *cellBgView;
@end


@implementation OKSelectAssetTypeTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setModel:(OKSelectAssetTypeModel *)model
{
    _model = model;
    NSString *iconName =  [NSString stringWithFormat:@"token_%@",[model.coin lowercaseString]];
    self.walletNameLabel.text = [model.coin uppercaseString];
    self.iconImageView.image = [UIImage imageNamed:iconName];
    self.checkBtn.selected = model.isSelected;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
