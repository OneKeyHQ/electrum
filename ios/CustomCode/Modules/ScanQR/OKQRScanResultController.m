//
//  OKQRScanResultController.m
//  OneKey
//
//  Created by zj on 2021/3/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKQRScanResultController.h"
@implementation OKCopyLabel

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self pressAction];
    }
    return self;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    [self pressAction];
}

// 初始化设置
- (void)pressAction {
    self.userInteractionEnabled = YES;
    UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressAction:)];
    longPress.minimumPressDuration = 0.1;
    [self addGestureRecognizer:longPress];
}

// 使label能够成为响应事件
- (BOOL)canBecomeFirstResponder {
    return YES;
}

// 自定义方法时才显示对就选项菜单，即屏蔽系统选项菜单
- (BOOL)canPerformAction:(SEL)action withSender:(id)sender {
    if (action == @selector(customCopy:)){
        return YES;
    }
    return NO;
}

- (void)customCopy:(id)sender {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = self.text;
}

- (void)longPressAction:(UIGestureRecognizer *)recognizer {
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        [self becomeFirstResponder];
        UIMenuItem *copyItem = [[UIMenuItem alloc] initWithTitle:@"拷贝" action:@selector(customCopy:)];
        UIMenuController *menuController = [UIMenuController sharedMenuController];
        menuController.menuItems = [NSArray arrayWithObjects:copyItem, nil];
        [menuController setTargetRect:self.frame inView:self.superview];
        [menuController setMenuVisible:YES animated:YES];
    }
}

@end

@interface OKQRScanResultController ()
@property (weak, nonatomic) IBOutlet UILabel *resultLabel;
@end

@implementation OKQRScanResultController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Scan" bundle:nil] instantiateViewControllerWithIdentifier:@"OKQRScanResultController"];
}

- (UIColor *)navBarTintColor {
    return UIColor.BG_W02;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"scan.result".localized;
    self.resultLabel.text = self.resultText;
}

@end
