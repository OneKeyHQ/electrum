//
//  OKIndexView.m
//  OneKey
//
//  Created by zj on 2021/2/26.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKIndexView.h"

@interface OKIndexView()
@property (nonatomic, assign)NSInteger lastIndex;
@property (nonatomic, strong)UIImpactFeedbackGenerator *generator;
@end

@implementation OKIndexView
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _label = [[UILabel alloc] initWithFrame:self.bounds];
        _label.font = [UIFont boldSystemFontOfSize:11];
        _label.textAlignment = NSTextAlignmentCenter;
        _label.numberOfLines = 0;
        [self addSubview:_label];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self.generator prepare];
    self.width = 16;
    self.height = _label.height;
    _label.centerX = self.width / 2;
}

- (void)setTitles:(NSArray<NSString *> *)titles {
    _titles = titles;
    NSMutableString *text = [@"" mutableCopy];
    for (NSString *c in titles) {
        [text appendFormat:@"%@\n", c];
    }
    self.label.attributedText = [NSString lineSpacing:4 content:[text substringToIndex:text.length - 1]];
    [self.label sizeToFit];
}

- (UIImpactFeedbackGenerator *)generator {
    if (!_generator) {
        _generator = [[UIImpactFeedbackGenerator alloc] initWithStyle:UIImpactFeedbackStyleLight];
    }
    return _generator;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    if ([touches count]) {
        [self invokeCallbackIfNeeded:[[touches anyObject] locationInView:self].y];
    }
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesMoved:touches withEvent:event];
    if ([touches count]) {
        [self invokeCallbackIfNeeded:[[touches anyObject] locationInView:self].y];
    }
}

- (void)invokeCallbackIfNeeded:(CGFloat)y {
    NSInteger index = self.titles.count * y / self.height;
    NSInteger indexForTitle = MIN(MAX(0, index), self.titles.count - 1);
    if (self.lastIndex != indexForTitle) {
        if (self.callback) {
            self.callback(self.titles[indexForTitle], indexForTitle);
            [self.generator impactOccurred];
        }
        self.lastIndex = indexForTitle;
    }
    [self.generator prepare];
}

@end
