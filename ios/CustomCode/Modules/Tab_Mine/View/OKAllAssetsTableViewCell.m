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
    self.balanceLabel.text = model.balance;
    self.moneyLabel.text = model.fiat;

}

@end
