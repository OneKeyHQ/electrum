//
//  OKAddTokenSheet.m
//  OneKey
//
//  Created by zj on 2021/3/3.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKAddTokenSheet.h"
@interface OKAddTokenSheet()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *coinIcon;
@property (weak, nonatomic) IBOutlet UILabel *coinNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;

@end

@implementation OKAddTokenSheet
+ (instancetype)getView {
    for (id obj in [[NSBundle mainBundle] loadNibNamed:@"TokenViews" owner:nil options:nil]) {
        if ([obj isMemberOfClass:[self class]]) {
            return obj;
        }
    }
    return nil;
}

- (void)setToken:(OKToken *)token {
    _token = token;
    self.coinNameLabel.text = self.token.symbol;
    self.addressLabel.text = self.token.address;
    [self.coinIcon sd_setImageWithURL:self.token.logoURI.toURL placeholderImage:[UIImage imageNamed:@"icon_ph"]];
}

- (IBAction)cancel:(id)sender {
    if (self.cancel) {
        self.cancel();
    }
}

- (IBAction)addToken:(id)sender {
    if (self.addTokenCallback) {
        self.addTokenCallback();
    }
}

@end
