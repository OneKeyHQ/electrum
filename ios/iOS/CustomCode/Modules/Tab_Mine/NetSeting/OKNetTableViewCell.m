//
//  OKNetTableViewCell.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/23.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKNetTableViewCell.h"
#import "OKNetTableViewCellModel.h"
@interface OKNetTableViewCell()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIImageView *checkImageView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;

@end


@implementation OKNetTableViewCell

- (void)setModel:(OKNetTableViewCellModel *)model
{
    _model = model;
    switch (model.type) {
        case OKNetTableViewCellModelTypeB:
        {
            self.titleLabel.text = model.titleStr;
            if ([kUserSettingManager.currentBtcBrowser isEqualToString:model.titleStr]) {
                self.checkImageView.hidden = NO;
            }else{
                self.checkImageView.hidden = YES;
            }
        }
            break;
        case OKNetTableViewCellModelTypeM:
        {
            self.titleLabel.text = model.titleStr;
            if ([kUserSettingManager.currentMarketSource isEqualToString:model.titleStr]) {
                self.checkImageView.hidden = NO;
            }else{
                self.checkImageView.hidden = YES;
            }
        }
            break;
        default:
            break;
    }
}

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}
@end
