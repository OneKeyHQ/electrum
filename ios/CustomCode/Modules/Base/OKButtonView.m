//
//  OKButtonView.m
//  OneKey
//
//  Created by zj on 2021/3/3.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKButtonView.h"

@implementation OKButtonView

- (void)awakeFromNib {
    [super awakeFromNib];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(click)];
    [self addGestureRecognizer:tap];
}

- (void)click {
    if (self.buttonClick) {
        self.buttonClick();
    }
}

- (void)setDisable:(BOOL)disable {
    _disable = disable;
    self.userInteractionEnabled = !disable;
    self.alpha = disable ? 0.6 : 1;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    [UIView animateWithDuration:0.1 animations:^{
        self.alpha = 0.6;
    }];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesEnded:touches withEvent:event];
    [UIView animateWithDuration:0.1 animations:^{
        self.alpha = 1;
    }];
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(nullable UIEvent *)event {
    [super touchesCancelled:touches withEvent:event];
    [UIView animateWithDuration:0.1 animations:^{
        self.alpha = 1;
    }];
}
@end
