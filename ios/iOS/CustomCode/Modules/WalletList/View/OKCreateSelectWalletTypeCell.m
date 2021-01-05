//
//  OKCreateSelectWalletTypeCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKCreateSelectWalletTypeCell.h"
#import "OKCreateSelectWalletTypeModel.h"

@interface OKCreateSelectWalletTypeCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;

@property (weak, nonatomic) IBOutlet UILabel *tipsLabel;

@end

@implementation OKCreateSelectWalletTypeCell

 - (void)setModel:(OKCreateSelectWalletTypeModel *)model
{
    _model = model;
    self.iconImageView.image = [UIImage imageNamed:model.iconName];
    self.titleLabel.text = model.createWalletType;
    self.tipsLabel.text = model.tipsString;
}

@end
