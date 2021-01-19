//
//  OKHardwareListBaseCell.m
//  OneKey
//
//  Created by liuzj on 07/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKHardwareListBaseCell.h"

@implementation OKHardwareListBaseCellModel
@end


@interface OKHardwareListBaseCell()
@property (nonatomic, assign) OKHardwareListBaseCellType cellType;
@property (weak, nonatomic) IBOutlet UIImageView *iconView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailsLabel;
@property (weak, nonatomic) IBOutlet UILabel *textOnlyLabel;
@property (weak, nonatomic) IBOutlet UIImageView *rightArrow;
@property (weak, nonatomic) IBOutlet UIView *tagView;
@property (weak, nonatomic) IBOutlet UILabel *tagLabel;
@end

@implementation OKHardwareListBaseCell

- (void)setModel:(OKHardwareListBaseCellModel *)model {
    _model = model;
    self.cellType = model.cellType;
    self.iconView.image = [UIImage imageNamed:model.imageName];
    if (model.imageName) {
        self.titleLabel.text = model.title;
        self.titleLabel.textColor = UIColorFromRGB(model.titleColor);
        self.textOnlyLabel.hidden = YES;
    } else {
        self.textOnlyLabel.text = model.title;
        self.textOnlyLabel.textColor = UIColorFromRGB(model.titleColor);
        self.textOnlyLabel.hidden = NO;
    }
    self.titleLabel.hidden = !self.textOnlyLabel.hidden;
    self.detailsLabel.text = model.details;
    self.rightArrow.hidden = model.hideRightArrow;

    if (model.tagText) {
        self.tagView.hidden = NO;
        [self.tagView setLayerRadius:self.tagView.height * 0.5];
        self.tagView.backgroundColor = HexColor(model.tagBgColor);
        self.tagLabel.text = model.tagText;
        self.tagLabel.textColor = HexColor(model.tagTextColor);
    } else {
        self.tagView.hidden = YES;
    }
}


@end
