//
//  OKAddTokenController.m
//  OneKey
//
//  Created by zj on 2021/3/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKAddTokenController.h"
#import "OKAddTokenSheetController.h"

@interface OKAddTokenController ()
@property (weak, nonatomic) IBOutlet OKButtonView *addView;
@property (weak, nonatomic) IBOutlet UILabel *warnLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *warnIcon;
@property (weak, nonatomic) IBOutlet UITextView *addressView;
@property (weak, nonatomic) IBOutlet UIView *addressViewContainer;
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
    self.warnLabel.text = @"token.contractAddress.notFound".localized;
    self.addView.backgroundColor = UIColor.TintBrand;
    [self.addView setLayerRadius:8];
    [self.addressViewContainer setLayerRadius:8];
    OKWeakSelf(self)
    self.addView.buttonClick = ^{
        [weakself addToken];
    };


}


- (void)addToken {
    OKAddTokenSheetController *sheet = [[OKAddTokenSheetController alloc] init];
    sheet.modalPresentationStyle = UIModalPresentationOverCurrentContext;
    [self.navigationController presentViewController:sheet animated:NO completion:nil];
}
@end
