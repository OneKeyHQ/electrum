//
//  OK_PassWordView.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>
#pragma mark - 密码框
typedef enum:NSUInteger {
    passShow1 = 1,//黑点,框,没间隔
    passShow2,//显示数字,框,没间隔
    passShow3,//黑点,框,有间隔
    passShow4,//显示数字,框,有间隔
    passShow5,//显示数字,下划线,一般用做验证码
} passShowType;

typedef void(^textBlock) (NSString * _Nullable str);
NS_ASSUME_NONNULL_BEGIN

@interface OK_PassWordView : UIView

@property(nonatomic,strong)UITextField *textF;

@property(nonatomic,strong)UIColor *tintColor;//主题色

@property(nonatomic,strong)UIColor *textColor;//字体颜色

@property(nonatomic, copy)textBlock textBlock;

@property(nonatomic,assign)passShowType showType;

@property(nonatomic,assign)NSInteger num;//格子数

-(void)show;
-(void)cleanPassword;
-(void)textFieldChanged:(NSString *)newValue;

@end

NS_ASSUME_NONNULL_END
