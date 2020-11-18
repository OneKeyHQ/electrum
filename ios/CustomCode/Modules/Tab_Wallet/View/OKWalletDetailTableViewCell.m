//
//  OKWalletDetailTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/29.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletDetailTableViewCell.h"
#import "OKWalletDetailModel.h"
@interface OKWalletDetailTableViewCell()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *rightLabel;
@property (weak, nonatomic) IBOutlet UIImageView *imageViewCopy;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;



@end


@implementation OKWalletDetailTableViewCell

- (void)setModel:(OKWalletDetailModel *)model
{
    _model = model;
    self.titleLabel.text = model.titleStr;
    self.rightLabel.text = model.rightLabelStr;
    self.rightLabel.textColor = model.rightLabelColor;
    self.titleLabel.textColor = model.leftLabelColor;
    
    if (model.isShowDesc == YES) { //显示DESC
        self.descLabel.hidden = NO;
        self.titleLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.imageViewCopy.hidden = YES;
        self.descLabel.text = model.rightLabelStr;
        return;
    }else{
        self.descLabel.hidden = YES;
        self.titleLabel.hidden = NO;
        self.rightLabel.hidden = NO;
        self.imageViewCopy.hidden = NO;
    }
    
    if (model.isShowCopy==NO &&model.isShowArrow==NO) {
        self.imageViewCopy.hidden = YES;
    }else{
        if (model.isShowCopy) {
            self.imageViewCopy.image = [UIImage imageNamed:@"copy_small"];
            UITapGestureRecognizer *tapCopy = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(copy_smallClick)];
            self.imageViewCopy.userInteractionEnabled = YES;
            [self.imageViewCopy addGestureRecognizer:tapCopy];
        }else if (model.isShowArrow){
            self.imageViewCopy.image = [UIImage imageNamed:@"right_arrow_small"];
        }
        self.imageViewCopy.hidden = NO;
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

- (void)copy_smallClick
{
    [kTools pasteboardCopyString:self.rightLabel.text msg:MyLocalizedString(@"Copied", nil)];
}

@end
