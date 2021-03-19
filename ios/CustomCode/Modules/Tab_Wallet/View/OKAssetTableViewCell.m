//
//  OKAssetTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKAssetTableViewCell.h"

@interface  OKAssetTableViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *coinTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *moneyLabel;
@end


@implementation OKAssetTableViewCell

- (void)setModel:(OKAssetTableViewCellModel *)model
{
    _model = model;
    [self.iconImageView sd_setImageWithURL:model.iconImage.toURL placeholderImage:[UIImage imageNamed:model.iconImage]];
    self.coinTypeLabel.text = [model.coinType uppercaseString];
    if (!kWalletManager.showAsset) {
        NSString *key = model.contract_addr.length > 0 ? [NSString stringWithFormat:@"token_%@",[kWalletManager.currentWalletInfo.coinType lowercaseString]]:[kWalletManager.currentWalletInfo.coinType lowercaseString];
        self.balanceLabel.text = model.balance?[kTools decimalNumberHandlerWithValue:[NSDecimalNumber decimalNumberWithString:model.balance] roundingMode:NSRoundDown scale:[kWalletManager getPrecision:key]].stringValue:@"0";
        NSArray *moneyArray = [model.money componentsSeparatedByString:@" "];
        NSString *money = [moneyArray firstObject];
        if (money.length > 0 && money != nil) {
            self.moneyLabel.text = [NSString stringWithFormat:@"≈ %@ %@",kWalletManager.currentFiatSymbol,money];
        }else{
            self.moneyLabel.text = @"";
        }

    }else{
        self.balanceLabel.text = @"****";
        self.moneyLabel.text = @"****";
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
