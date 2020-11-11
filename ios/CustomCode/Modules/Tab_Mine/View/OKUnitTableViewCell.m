//
//  OKUnitTableViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKUnitTableViewCell.h"
#import "OKUnitTableViewCellModel.h"

@interface  OKUnitTableViewCell()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@end

@implementation OKUnitTableViewCell

- (void)setModel:(OKUnitTableViewCellModel *)model
{
    _model = model;
    self.titleLabel.text = model.titleStr;
    
    if (model.type == GroupTypeFait) {
        NSString *fiat = kWalletManager.currentFiat;
        self.checkImageView.hidden = ![fiat isEqualToString:model.typeString];
    }else if (model.type == GroupTypeBitcoinUnit){
        NSString *bitcoinUnit = kWalletManager.currentBitcoinUnit;
        self.checkImageView.hidden = ![bitcoinUnit isEqualToString:model.typeString];
    }else if (model.type == GroupTypeBitcoinETH){
        self.checkImageView.hidden = NO;
    }else{
        self.checkImageView.hidden = YES;
    }
 
    
    if (model.descStr.length == 0 || model.descStr == nil) {
        self.descLabel.hidden = YES;
    }else{
        self.descLabel.hidden = NO;
        self.descLabel.text = model.descStr;
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
