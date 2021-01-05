//
//  OKWalletListTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletListTableViewCell.h"
#import "OKWalletListTableViewCellModel.h"

@interface OKWalletListTableViewCell()
@property (weak, nonatomic) IBOutlet UILabel *walletNameLabel;
@property (weak, nonatomic) IBOutlet UIView *walletTypeBgView;
@property (weak, nonatomic) IBOutlet UILabel *walletTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UIButton *btnCopy;
- (IBAction)btnCopyClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *selectedBtn;
@property (weak, nonatomic) IBOutlet UIImageView *rightCoinTypeBgImageView;

@property (weak, nonatomic) IBOutlet UIView *cellBgView;


@end

@implementation OKWalletListTableViewCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
    self.walletTypeBgView.backgroundColor = RGBA(255, 255, 255, 0.3);
    [self.walletTypeBgView setLayerRadius:10];
    [self.cellBgView setLayerDefaultRadius];
}

- (void)setModel:(OKWalletListTableViewCellModel *)model
{
    _model = model;
    self.walletNameLabel.text = model.label;
    self.walletTypeBgView.backgroundColor = model.backColor;
    NSString *address = model.address;
    if (model.address.length > 12) {
        address = [NSString stringWithFormat:@"%@...%@",[model.address substringToIndex:6],[model.address substringFromIndex:model.address.length - 6]];
    }
    self.addressLabel.text = address;
    self.rightCoinTypeBgImageView.image = [UIImage imageNamed:model.iconName];
    self.walletTypeLabel.text = model.walletTypeShowStr;
    self.walletTypeBgView.hidden = model.walletTypeShowStr.length == 0;
    self.selectedBtn.hidden = !model.isCurrent;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)btnCopyClick:(UIButton *)sender {
    [kTools pasteboardCopyString:self.model.address msg:MyLocalizedString(@"Copied", nil)];
}
@end
