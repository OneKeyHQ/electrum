//
//  OKAllAssetsTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKAllAssetsTableViewCell.h"
#import "OKAllAssetsCellModel.h"

@interface OKAllAssetsTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak, nonatomic) IBOutlet UILabel *cointypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *moneyLabel;

@end


@implementation OKAllAssetsTableViewCell

- (void)setModel:(OKAllAssetsCellModel *)model {
    _model = model;

    self.iconImageView.image = [UIImage imageNamed:@"token_btc"];
    self.cointypeLabel.text = model.coin;
    NSString *fiatStr = [[model.fiat split:@" "].firstObject numStrPrecition:[kWalletManager getPrecision:@"fiat"]];
    self.moneyLabel.text = [NSString stringWithFormat:@"%@ %@", kWalletManager.currentFiatSymbol, fiatStr];

    if (model.address.length && !model.isNativeToken) { // erc tokens
        self.balanceLabel.text = [model.balance numStrPrecition:[kWalletManager getPrecision:@"token_eth"]];
        OKToken *token = [kOKTokenManager tokensWithAddress:model.address];
        [self.iconImageView sd_setImageWithURL:token.logoURI.toURL placeholderImage:[UIImage imageNamed:@"icon_ph"]];
    } else { // native tokens
        self.balanceLabel.text = [model.balance numStrPrecition:[kWalletManager getPrecision:model.coin]];
        NSString *iconImageName = [NSString stringWithFormat:@"token_%@", model.coin];
        self.cointypeLabel.text = model.coin.uppercaseString;
        self.iconImageView.image = [UIImage imageNamed:iconImageName];
    }
}

@end
