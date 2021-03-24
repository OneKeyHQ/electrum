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
    if (model.walletType == OKWalletTypeHardware) {
        self.badgeLabel.text = nil;
        self.badgeLabel.attributedText = [self hwWalletTypeDesc];
    } else {
        self.badgeLabel.attributedText = nil;
        self.badgeLabel.text = model.walletTypeDesc;
        self.badge.hidden = !self.badgeLabel.text.length;
    }
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

- (NSAttributedString *)hwWalletTypeDesc {
    NSString *desc;
    NSString *deviceName = [[OKDevicesManager sharedInstance] getDeviceModelWithID:self.model.device_id].deviceInfo.label;
    if (deviceName.length) {
        desc = [NSString stringWithFormat:@"  %@", deviceName];;
        if (desc.length > 16) {
            desc = [desc substringToIndex:16];
        }
    } else {
        desc = [NSString stringWithFormat:@"  %@", @"hardware".localized];;
    }

    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:desc];
    NSTextAttachment *attchment = [[NSTextAttachment alloc] init];
    attchment.bounds = CGRectMake(0,-2,8,12);
    attchment.image = [UIImage imageNamed:@"device_white"];
    NSAttributedString *attchmentStr = [NSAttributedString attributedStringWithAttachment:attchment];
    [attributedString insertAttributedString:attchmentStr atIndex:1];

    return attributedString;
}

@end


@interface OKChangeWalletSubCell()
@property (weak, nonatomic) IBOutlet UIImageView *icon;
@end

@implementation OKChangeWalletSubCell
- (void)setType:(OKWalletCoinType)type {
    NSString *iconName = @"cointype_eth";
    switch (type) {
        case OKWalletCoinTypeBTC: iconName = @"cointype_btc"; break;
        case OKWalletCoinTypeBSC: iconName = @"cointype_bsc"; break;
        case OKWalletCoinTypeHECO: iconName = @"cointype_heco"; break;
        default: break;
    }
    if (self.chosen) {
        iconName = [iconName stringByAppendingString:@"_selected"];
    }
    self.icon.image = [UIImage imageNamed:iconName];
}
@end
