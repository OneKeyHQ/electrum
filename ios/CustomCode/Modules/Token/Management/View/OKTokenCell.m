//
//  OKTokenCell.m
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenCell.h"
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

- (void)setModel:(OKTokenModel *)model {

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
    self.name.text = model.name;
    self.address.text = model.address;
}



@end
