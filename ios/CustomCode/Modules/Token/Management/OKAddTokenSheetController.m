//
//  OKAddTokenSheetController.m
//  OneKey
//
//  Created by zj on 2021/3/3.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKAddTokenSheetController.h"
#import "OKAddTokenSheet.h"

@interface OKAddTokenSheetController () <UIGestureRecognizerDelegate>
@property (nonatomic, strong) OKAddTokenSheet *sheet;
@property (nonatomic, strong) UIView *mask;

@end

@implementation OKAddTokenSheetController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.mask = [[UIView alloc] initWithFrame:self.view.bounds];
    self.mask.backgroundColor = UIColor.blackColor;
    self.mask.alpha = 0;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(dismiss)];
    [self.mask addGestureRecognizer:tap];
    [self.view addSubview:self.mask];

    self.sheet = [OKAddTokenSheet getView];
    self.sheet.hidden = YES;
    [self.sheet setLayerRadius:13];
    OKWeakSelf(self)
    self.sheet.cancel = ^{
        [weakself dismiss];
    };
    self.sheet.addTokenCallback = self.addTokenCallback;
    [self.view addSubview:self.sheet];

//    UIPanGestureRecognizer *panGes = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
//    panGes.delegate = self;
//    UIPanGestureRecognizer *panGes2 = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
//    panGes2.delegate = self;
//    [self.view addGestureRecognizer:panGes];
//    [self.sheet addGestureRecognizer:panGes2];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self show];
}

- (void)viewWillLayoutSubviews {
    CGFloat sheetHeight = 347 + self.view.safeAreaInsets.bottom;
    self.sheet.bounds = CGRectMake(0, 0, self.view.width, sheetHeight);
    self.sheet.centerX = self.view.centerX;

}

//- (void)move:(UIPanGestureRecognizer *)sender {
//    CGFloat originY = self.view.height - self.sheet.height * 0.5;
//    CGPoint offset = [sender translationInView:self.sheet];
//    CGFloat deltaY = MAX(self.sheet.centerY + offset.y, originY);
//    self.sheet.centerY = deltaY;
//    [sender setTranslation:CGPointMake(0, 0) inView:self.view];
//    BOOL reachDismiss = self.sheet.centerY > originY * 1.2;
//    if (sender.state == UIGestureRecognizerStateEnded) {
//        if (reachDismiss) {
//            [self dismiss];
//        } else {
//            [UIView animateWithDuration:0.2 animations:^{
//                self.sheet.centerY = originY;
//            } completion:nil];
//        }
//    }
//}

- (void)show {
    self.sheet.top = self.view.height;
    self.sheet.hidden = NO;

    [UIView animateWithDuration:0.3 animations:^{
        self.mask.alpha = 0.4;
        self.sheet.top = self.view.height - self.sheet.height;
    }];
}

- (void)dismiss {
    [UIView animateWithDuration:0.3 animations:^{
        self.mask.alpha = 0;
        self.sheet.top = self.view.height;
    } completion:^(BOOL finished) {
        [self dismissViewControllerAnimated:NO completion:nil];
    }];
}

@end
