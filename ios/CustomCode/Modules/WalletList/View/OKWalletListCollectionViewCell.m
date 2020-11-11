//
//  OKWalletListCollectionViewCell.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWalletListCollectionViewCell.h"
#import "OKWalletListCollectionViewCellModel.h"
@interface OKWalletListCollectionViewCell()
@property (weak, nonatomic) IBOutlet UIImageView *iconImageView;

@end

@implementation OKWalletListCollectionViewCell

- (void)setModel:(OKWalletListCollectionViewCellModel *)model
{
    _model = model;
    NSString *iconName = model.iconName;
    if (model.isSelected) {
        iconName = [model.iconName stringByAppendingString:@"_selected"];
    }
    self.iconImageView.image = [UIImage imageNamed:iconName];
    
}

@end
