//
//  OKScanView.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//
#import <UIKit/UIKit.h>

typedef void(^MyAddressBtnEventBlocks)();
typedef void(^LightBtnEventBlocks)(BOOL selected);

@interface OKScanView : UIView

@property (nonatomic, copy) MyAddressBtnEventBlocks myAddressBtnEventBlocks;
@property (nonatomic, copy) LightBtnEventBlocks lightBtnEventBlocks;
@property (nonatomic) BOOL lightBtnIsShowing; // 背光灯开关显示中

- (void)createTimer;
- (void)showTorch;
- (void)hideTorch;

@end
