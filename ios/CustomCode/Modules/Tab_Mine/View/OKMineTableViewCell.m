//
//  OKMineTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/20.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKMineTableViewCell.h"
#import "OKMineTableViewCellModel.h"

@interface OKMineTableViewCell()

@property (weak, nonatomic) IBOutlet UIImageView *iconView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *rightArrow;
@property (weak, nonatomic) IBOutlet UISwitch *rightSwitch;
- (IBAction)switchClick:(UISwitch *)sender;
@property (nonatomic,strong)YZAuthID *authIDControl;
@end

@implementation OKMineTableViewCell

- (void)setModel:(OKMineTableViewCellModel *)model
{
    _model = model;
    
    self.iconView.image = [UIImage imageNamed:model.imageName];
    self.titleLabel.text = model.menuName;
    
    if (_model.isAuth) {
        self.rightSwitch.hidden = NO;
        self.rightArrow.hidden = YES;
        [self.rightSwitch setOn:kWalletManager.isOpenAuthBiological];
    }else{
        self.rightSwitch.hidden = YES;
        self.rightArrow.hidden = NO;
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
- (YZAuthID *)authIDControl {
    if (!_authIDControl) {
        _authIDControl = [[YZAuthID alloc] init];
    }
    return _authIDControl;
}
- (IBAction)switchClick:(UISwitch *)sender {
    [self.authIDControl yz_showAuthIDWithDescribe:@"aaa" BlockState:^(YZAuthIDState state, NSError *error) {
        if (state == YZAuthIDStateNotSupport
            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
            [kTools tipMessage:MyLocalizedString(@"Does not support FaceID", nil)];
            [self.rightSwitch setOn:NO];
        } else if(state == YZAuthIDStateFail) { // 认证失败
            self.rightSwitch.on = !sender.isOn;
        } else if(state == YZAuthIDStateTouchIDLockout) {   // 多次错误，已被锁定
            self.rightSwitch.on = !sender.isOn;
        } else if (state == YZAuthIDStateSuccess) { // TouchID/FaceID验证成功
            kWalletManager.isOpenAuthBiological = self.rightSwitch.on;
        }else{
            self.rightSwitch.on = !sender.isOn;
        }
    }];
}
@end
