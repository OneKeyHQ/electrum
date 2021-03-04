//
//  OKTokenNoResultView.m
//  OneKey
//
//  Created by zj on 2021/3/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenNoResultView.h"
@interface OKTokenNoResultView()
@property (weak, nonatomic) IBOutlet UILabel *addTokenLabel;
@property (weak, nonatomic) IBOutlet OKLabel *noResultText;
@property (weak, nonatomic) IBOutlet OKButtonView *addTokenView;
@end

@implementation OKTokenNoResultView
+ (instancetype)getView {
    for (id obj in [[NSBundle mainBundle] loadNibNamed:@"TokenViews" owner:nil options:nil]) {
        if ([obj isMemberOfClass:[self class]]) {
            return obj;
        }
    }
    return nil;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    self.noResultText.textColor = UIColor.FG_B02;
    self.addTokenLabel.textColor = UIColor.FG_W01;
    self.addTokenView.backgroundColor = UIColor.TintBrand;
    [self.addTokenView setLayerRadius:8];
    OKWeakSelf(self)
    self.addTokenView.buttonClick = ^{
        [weakself addToken];
    };
}

- (void)addToken {
    if (self.addTokenCallback) {
        self.addTokenCallback();
    }
}

@end
