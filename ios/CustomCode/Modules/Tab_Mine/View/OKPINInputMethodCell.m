//
//  OKPINInputMethodCell.m
//  OneKey
//
//  Created by zj on 2021/2/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKPINInputMethodCell.h"
@implementation OKPINInputMethodCellModel
@end

@interface OKPINInputMethodCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;
@end

@implementation OKPINInputMethodCell

- (void)setModel:(OKPINInputMethodCellModel *)model {
    _model = model;
    self.titleLabel.text = model.titleStr;
    self.checkImageView.hidden = !model.isSelected;
}

@end
