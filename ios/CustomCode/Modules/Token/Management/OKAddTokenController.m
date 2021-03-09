//
//  OKAddTokenController.m
//  OneKey
//
//  Created by zj on 2021/3/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKAddTokenController.h"
#import "OKAddTokenSheetController.h"

@interface OKAddTokenController () <UITextViewDelegate>
@property (weak, nonatomic) IBOutlet OKButtonView *addView;
@property (weak, nonatomic) IBOutlet UILabel *warnLabel;
@property (weak, nonatomic) IBOutlet UIImageView *warnImage;
@property (weak, nonatomic) IBOutlet UITextView *addressView;
@property (weak, nonatomic) IBOutlet UIView *addressViewContainer;
@property (weak, nonatomic) IBOutlet UILabel *phLabel;
@property (weak, nonatomic) IBOutlet UIImageView *loadingView;
@property (assign, nonatomic) BOOL showWarn;
@end

@implementation OKAddTokenController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tokens" bundle:nil] instantiateViewControllerWithIdentifier:@"OKAddTokenController"];
}

- (UIColor *)navBarTintColor {
    return UIColor.BG_W02;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = UIColor.BG_W02;
    self.title = @"token.add".localized;
    self.addressView.delegate = self;
    self.warnLabel.text = @"token.contractAddress.notFound".localized;
    self.addView.backgroundColor = UIColor.TintBrand;
    self.addView.disable = YES;
    self.showWarn = NO;
    [self.addView setLayerRadius:8];
    [self.addressViewContainer setLayerRadius:8];
    OKWeakSelf(self)
    self.addView.buttonClick = ^{
        [weakself addToken];
    };

    CABasicAnimation *rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0];
    rotationAnimation.duration = 1;
    rotationAnimation.cumulative = YES;
    rotationAnimation.repeatCount = MAXFLOAT;
    [self.loadingView.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
    self.loadingView.hidden = YES;
}

- (void)setShowWarn:(BOOL)showWarn {
    _showWarn = showWarn;
    self.warnLabel.hidden = !showWarn;
    self.warnImage.hidden = !showWarn;
}

- (void)textViewDidChange:(UITextView *)textView {
    BOOL hasText = textView.text.length;
    self.addView.disable = !hasText;
    self.phLabel.hidden = hasText;
}

- (void)addToken {
    [self.addressView endEditing:YES];
    self.loadingView.hidden = NO;
    NSString *address = [self.addressView.text copy];
    address = [address stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] ?: @"";
    OKWeakSelf(self)
    [kPyCommandsManager asyncCall:kInterface_get_customer_token_info parameter:@{
        @"contract_address": address
    } callback:^(id  _Nonnull result) {
        weakself.loadingView.hidden = YES;
        if (!result) {
            weakself.showWarn = YES;
        } else {
            weakself.showWarn = NO;
            OKAddTokenSheetController *sheet = [[OKAddTokenSheetController alloc] init];
            OKToken *token = [OKToken mj_objectWithKeyValues:result];
            sheet.token = token;
            sheet.modalPresentationStyle = UIModalPresentationOverCurrentContext;
            [weakself.navigationController presentViewController:sheet animated:NO completion:nil];
        }
    }];

}
@end
