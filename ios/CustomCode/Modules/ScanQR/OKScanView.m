//
//  OKScanView.m
//  OneKey
//
//  Created by xiaoliang on 2020/9/28.
//

#import "OKScanView.h"
#import "NSTimer+TimerBlock.h"


@interface OKScanView ()

@property (weak, nonatomic) IBOutlet UIView *borderView;
@property (weak, nonatomic) IBOutlet UIImageView *scanBorderImageView;
@property (weak, nonatomic) IBOutlet UIButton *lightUpBtn;
@property (weak, nonatomic) IBOutlet UIButton *lightDownBtn;
@property (nonatomic, strong) UIImage *lightBtnImage;

@end

@implementation OKScanView

- (UIImage *)lightBtnImage {
    if (_lightBtnImage == nil) {
        _lightBtnImage = [UIImage imageNamed:@"QRCode_Light"];
    }
    return _lightBtnImage;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.borderView.clipsToBounds = YES;
    self.borderView.layer.borderWidth = 1;
    self.borderView.layer.borderColor = [UIColor clearColor].CGColor;
}

// 光源开关
- (IBAction)lightBtnAction:(id)sender {
    self.lightUpBtn.selected = !self.lightUpBtn.selected;
    if (self.lightUpBtn.selected) {
        [self.lightUpBtn setImage:[self.lightBtnImage imageWithColor:HexColor(0x00B812)] forState:UIControlStateNormal];
        [self.lightDownBtn setTitle:MyLocalizedString(@"Touch Off", nil) forState:UIControlStateNormal];
    } else {
        [self.lightUpBtn setImage:self.lightBtnImage forState:UIControlStateNormal];
        [self.lightDownBtn setTitle:MyLocalizedString(@"Touch Light", nil) forState:UIControlStateNormal];
    }
    if (self.lightBtnEventBlocks) {
        self.lightBtnEventBlocks(self.lightUpBtn.selected);
    }
}

- (void)showTorch {
    self.lightUpBtn.hidden = NO;
    self.lightDownBtn.hidden = NO;
}

- (void)hideTorch {
    self.lightUpBtn.hidden = YES;
    self.lightDownBtn.hidden = YES;
    self.lightUpBtn.selected = NO;
    [self.lightUpBtn setImage:self.lightBtnImage forState:UIControlStateNormal];
    [self.lightDownBtn setTitle:MyLocalizedString(@"Touch Light", nil) forState:UIControlStateNormal];
}

- (BOOL)lightBtnIsShowing {
    return !self.lightUpBtn.hidden;
}

@end
