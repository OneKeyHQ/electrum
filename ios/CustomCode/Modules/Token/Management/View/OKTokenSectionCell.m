//
//  OKTokenSectionCell.m
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenSectionCell.h"
@interface OKTokenSectionCell()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation OKTokenSectionCell

- (void)setTitle:(NSString *)title {
    self.titleLabel.text = title;
}
@end
