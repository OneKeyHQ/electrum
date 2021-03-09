//
//  OKTokenCell.m
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenCell.h"
#import "OKTokenManager.h"

@implementation NSString(OKAddressFormatted)
- (NSString *)addressFormatted {
    if (self.length < 20) {
        return [@"error: " stringByAppendingString:self];
    }
    NSString *head = [self substringToIndex:8];
    NSString *tail = [self substringFromIndex:self.length - 8];
    return [NSString stringWithFormat:@"%@...%@", head, tail];
}
@end


@interface OKTokenCell()
@property (weak, nonatomic) IBOutlet UIView *bg;
@property (weak, nonatomic) IBOutlet UIImageView *icon;
@property (weak, nonatomic) IBOutlet UILabel *name;
@property (weak, nonatomic) IBOutlet UILabel *address;
@property (weak, nonatomic) IBOutlet UISwitch *tokenSwitch;
@property (strong, nonatomic) UIView *mask;
@end

@implementation OKTokenCell
- (void)awakeFromNib {
    [super awakeFromNib];
    _mask = [[UIView alloc] initWithFrame:CGRectZero];
    _mask.backgroundColor = [UIColor whiteColor];
    [self insertSubview:_mask atIndex:0];
}

- (void)setModel:(OKToken *)model {
    _model = model;

    if (self.isTop && self.isBottom) {
        self.mask.frame = CGRectZero;
    } else if (self.isTop) {
        self.mask.frame = CGRectMake(16, 32, SCREEN_WIDTH - 32, 32);
    } else if (self.isBottom) {
        self.mask.frame = CGRectMake(16, 0, SCREEN_WIDTH - 32, 32);
    } else {
        self.mask.frame = CGRectMake(16, 0, SCREEN_WIDTH - 32, 64);
    }

    [self.bg setLayerRadius:13];
    self.name.text = model.symbol;
    self.isOn = NO;
    for (NSString *address in kOKTokenManager.currentAddress) {
        if ([address isEqualToString:model.address]) {
            self.isOn = YES;
            break;
        }
    }
    self.address.text = model.address.addressFormatted;
    [self.icon sd_setImageWithURL:model.logoURI.toURL placeholderImage:[UIImage imageNamed:@"icon_ph"]];
}

- (void)setIsOn:(BOOL)isOn {
    _isOn = isOn;
    self.tokenSwitch.on = isOn;
}

- (IBAction)switchTo:(UISwitch *)sender {
    if (sender.isOn) {
        [kOKTokenManager addToken:self.model.address symbol:self.model.symbol];
    } else {
        [kOKTokenManager delToken:self.model.address];
    }
}

@end
