//
//  OKChangeWalletCell.m
//  OneKey
//
//  Created by zj on 2021/3/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKChangeWalletCell.h"
@interface OKChangeWalletCell()
@property (weak, nonatomic) IBOutlet UIView *bgView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UIImageView *selectImageView;
@property (weak, nonatomic) IBOutlet UIView *badge;
@property (weak, nonatomic) IBOutlet UILabel *badgeLabel;
@end

@implementation OKChangeWalletCell

- (void)awakeFromNib {
    [super awakeFromNib];
    [self.bgView setLayerRadius:13];
    [self.badge setLayerRadius:9];
    self.bgView.backgroundColor = HexColor(0xF7931B);
}

- (void)setModel:(OKWalletInfoModel *)model {
    _model = model;
    self.addressLabel.text = model.addr.addressFormatted;
    self.nameLabel.text = model.label;
    self.badgeLabel.attributedText = model.walletTypeDesc;
    self.selectImageView.hidden = ![model.name isEqualToString:kWalletManager.currentWalletInfo.name];
    NSInteger precision = [kWalletManager getPrecision:@"btc"];
    if (model.chainType == OKWalletChainTypeETHLike) {
        precision = [kWalletManager getPrecision:@"eth"];
        self.bgView.backgroundColor = HexColor(0x3E5BF2);
    }
    NSString *balance = [model.additionalData objectForKey:@"balance"];
    balance = [balance numStrPrecition:precision];

    self.balanceLabel.text = balance ?: @"0";
}

@end


@interface OKChangeWalletSubCell()
@property (weak, nonatomic) IBOutlet UIImageView *icon;
@end

@implementation OKChangeWalletSubCell
- (void)setType:(OKWalletCoinType)type {
    NSString *iconName = @"cointype_eth_selected";
    switch (type) {
        case OKWalletCoinTypeBTC:
            iconName = @"cointype_btc_selected";
            break;
        default:
            break;
    }
    self.icon.image = [UIImage imageNamed:iconName];
}
@end
