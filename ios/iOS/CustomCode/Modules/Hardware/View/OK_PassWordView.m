//
//  OK_PassWordView.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/11.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OK_PassWordView.h"
#pragma mark -
#pragma mark - 密码框
#define kDotSize CGSizeMake (10, 10) //密码点的大小
#define K_Field_Height self.frame.size.height  //每一个输入框的高度等于当前view的高度
@interface OK_PassWordView()<UITextFieldDelegate>

@property(nonatomic,strong)NSMutableArray *dotArr;//黑点
@property(nonatomic,strong)NSMutableArray *bottomLineArr;//下划线
@property(nonatomic,strong)NSMutableArray *codeLArr;//Lable
@end

@implementation OK_PassWordView

-(instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}
- (instancetype)initWithCoder:(NSCoder *)coder
{
    self = [super initWithCoder:coder];
    if (self) {
    
    }
    return self;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
      
    }
    return self;
}
-(void)configUI{
    self.backgroundColor = [UIColor whiteColor];
    self.bottomLineArr= @[].mutableCopy;
    self.dotArr = @[].mutableCopy;
    self.codeLArr = @[].mutableCopy;
 
    
    [self.bottomLineArr removeAllObjects];
    [self.dotArr removeAllObjects];
    [self.codeLArr removeAllObjects];

    
//    passShow1,//黑点,框,没间隔
//    passShow2,//显示数字,框,没间隔
//    passShow3,//黑点,框,有间隔
//    passShow4,//显示数字,框,有间隔
//    passShow5,//显示数字,下划线,一般用做验证码
    
    if (self.showType == passShow1) {
        [self initShow1];
    }else if(self.showType == passShow2){
        [self initShow2];
        
    }else if(self.showType == passShow3){
        [self initShow3];
        
    }else if(self.showType == passShow4){
        [self initShow4];
        
    }else if(self.showType == passShow5){
        [self initShow5];
        
    }
    
}

//    passShow1,//黑点,框,没间隔
-(void)initShow1{
    CGFloat width = self.frame.size.width/self.num;
    
    for (int i = 0; i< self.num -1; i++) {
            UIView *lineV = [[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame)+(i+1)*width, 0, 1,K_Field_Height)];
            lineV.backgroundColor = self.tintColor;
            [self addSubview:lineV];
    }
    
    self.dotArr = @[].mutableCopy;
    for (int i=0; i<self.num; i++) {
        UIView *dotView =[[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (width - self.num) / 2 + i * width, CGRectGetMinY(self.textF.frame) + (K_Field_Height - kDotSize.height) / 2, kDotSize.width, kDotSize.height)];
        dotView.backgroundColor = [UIColor blackColor];
        dotView.layer.cornerRadius = kDotSize.width/2.0f;
        dotView.clipsToBounds = YES;
        dotView.hidden = YES;
        [self addSubview:dotView];
        [self.dotArr addObject:dotView];
    }
    _textF.layer.borderColor = self.tintColor.CGColor;
    _textF.layer.borderWidth = 1;
    _textF.layer.masksToBounds = YES;
    
}
//    passShow2,//显示数字,框,没间隔
-(void)initShow2{
    CGFloat width = self.frame.size.width/self.num;
    
    for (int i = 0; i< self.num -1; i++) {
        UIView *lineV = [[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame)+(i+1)*width, 0, 1,K_Field_Height)];
        lineV.backgroundColor = self.tintColor;
        [self addSubview:lineV];
    }
    
    for (int i=0; i<self.num; i++) {
        UILabel *codeL =[[UILabel alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (i)*width+1, 1, width-2, self.frame.size.height-2)];
        codeL.backgroundColor = [UIColor clearColor];
        codeL.layer.borderColor = [UIColor whiteColor].CGColor;
        codeL.layer.borderWidth = 1.f;
        codeL.textColor = self.textColor;
        codeL.hidden = YES;
        codeL.textAlignment = NSTextAlignmentCenter;
        [self addSubview:codeL];
        [self.codeLArr addObject:codeL];
    }
    
    _textF.layer.borderColor = self.tintColor.CGColor;
    _textF.layer.borderWidth = 1;
    _textF.layer.masksToBounds = YES;
}

//    passShow3,//黑点,框,有间隔
-(void)initShow3{
    CGFloat f = 10; //设置格子间隔
    
    CGFloat width = ((self.frame.size.width-(self.num-1)*f)/self.num);
    
    for (int i=0; i<self.num; i++) {
        UILabel *codeL =[[UILabel alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (i)*(width+f), 0, width, self.frame.size.height)];
        codeL.backgroundColor = HexColor(0xF2F2F2);
        [codeL setLayerRadius:15];
        codeL.layer.borderColor= self.tintColor.CGColor;
        codeL.textColor = self.textColor;
        codeL.layer.borderWidth = 1.;
        codeL.textAlignment = NSTextAlignmentCenter;
        [self addSubview:codeL];
        [self.codeLArr addObject:codeL];
    }
    
    for (int i=0; i<self.num; i++) {
        UIView *dotView =[[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + i * (width+f) +(width- kDotSize.width)/2, CGRectGetMinY(self.textF.frame) + (K_Field_Height - kDotSize.height) / 2, kDotSize.width, kDotSize.height)];
        dotView.backgroundColor = [UIColor blackColor];
        dotView.layer.cornerRadius = kDotSize.width/2.0f;
        dotView.clipsToBounds = YES;
        dotView.hidden = YES;
        [self addSubview:dotView];
        [self.dotArr addObject:dotView];
    }
}
////    passShow4,//显示数字,框,有间隔
-(void)initShow4{
    CGFloat f = 10; //设置格子间隔
    CGFloat width = ((self.frame.size.width-(self.num-1)*f)/self.num);
    for (int i=0; i<self.num; i++) {
        UILabel *codeL =[[UILabel alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (i)*(width+f), 0, width, self.frame.size.height)];
        codeL.backgroundColor = [UIColor whiteColor];
        codeL.layer.borderColor= self.tintColor.CGColor;
        codeL.textColor = self.textColor;
        codeL.layer.borderWidth = 1.;
        codeL.textAlignment = NSTextAlignmentCenter;
        [self addSubview:codeL];
        [self.codeLArr addObject:codeL];
    }
    
}

//    passShow5,//显示数字,下划线,一般用做验证码
-(void)initShow5{
        CGFloat f = 10; //设置间隔
     CGFloat width = ((self.frame.size.width-(self.num-1)*f)/self.num);
    
    for (int i =0; i<self.num; i++) {
        UIView *lineV = [[UIView alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (i)*(width+f), K_Field_Height-1,width,1)];
        lineV.backgroundColor = [UIColor grayColor];//原始线的颜色
        [self addSubview:lineV];
        [self.bottomLineArr addObject:lineV];
        
    }
    
    for (int i=0; i<self.num; i++) {
        UILabel *codeL =[[UILabel alloc]initWithFrame:CGRectMake(CGRectGetMinX(self.textF.frame) + (i)*(width+f), 0, width, K_Field_Height-1)];
        codeL.backgroundColor = [UIColor whiteColor];
        codeL.textColor = self.textColor;
        codeL.hidden = YES;
        codeL.textAlignment = NSTextAlignmentCenter;
        [self addSubview:codeL];
        [self.codeLArr addObject:codeL];
    }
    
}
-(BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    
    
    if ([string isEqualToString:@"\n"]) {
        [textField resignFirstResponder];
        return NO;
    }else if(string.length == 0){
        return YES;
    }else if(textField.text.length >= self.num){
        return NO;
    }else{
        return YES;
    }
}
-(void)cleanPassword{
    self.textF.text = @"";

    [self textFieldDidChange:self.textF];


}
-(void)textFieldChanged:(NSString *)newValue{
    self.textF.text = newValue;
    [self textFieldDidChange:self.textF];
}
- (void)textFieldDidChange:(UITextField *)textField
{
    switch (_showType) {
        case 1:
        {
            for (UIView *dotView in self.dotArr) {
                dotView.hidden = YES;
            }
            
            for (int i = 0; i < textField.text.length; i++) {
                ((UIView *)[self.dotArr objectAtIndex:i]).hidden = NO;
            }
        }
            break;
        case 2:{
            for (UILabel *codeL in self.codeLArr) {
                codeL.hidden = YES;
            }
            for (int i = 0; i < textField.text.length; i++) {
                ((UILabel *)[self.codeLArr objectAtIndex:i]).hidden = NO;
                ((UILabel *)[self.codeLArr objectAtIndex:i]).text =[textField.text substringWithRange:NSMakeRange(i, 1)];
            }
        }
            break;
        case 3:{
            for (UIView *dotView in self.dotArr) {
                dotView.hidden = YES;
            }
            
            for (int i = 0; i < textField.text.length; i++) {
                ((UIView *)[self.dotArr objectAtIndex:i]).hidden = NO;
            }
            
        }
            break;
        case 4:{
            
            for (UILabel *codeL in self.codeLArr) {
                codeL.text = @"";
                
            }
          for (int i = 0; i < textField.text.length; i++) {
                ((UILabel *)[self.codeLArr objectAtIndex:i]).hidden = NO;
                ((UILabel *)[self.codeLArr objectAtIndex:i]).text =[textField.text substringWithRange:NSMakeRange(i, 1)];
            }
        }
            break;
        case 5:{
            for (UILabel *codeL in self.codeLArr) {
                codeL.hidden = YES;
            }
            for (int i = 0; i < textField.text.length; i++) {
                ((UILabel *)[self.codeLArr objectAtIndex:i]).hidden = NO;
                ((UILabel *)[self.codeLArr objectAtIndex:i]).text =[textField.text substringWithRange:NSMakeRange(i, 1)];
            }
            
            for (UIView *lineV in self.bottomLineArr) {
                lineV.backgroundColor = [UIColor grayColor];
            }
            for (int i = 0; i < textField.text.length; i++) {
                ((UIView *)[self.bottomLineArr objectAtIndex:i]).backgroundColor = self.tintColor;
            }
        }
            break;
        default:
            break;
    }

    if (textField.text.length == self.num) {
        if (_textBlock) {
            self.textBlock(textField.text);
        }
        
    }
    

}

-(UITextField *)textF{
    if (!_textF) {
        _textF = [[UITextField alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, K_Field_Height)];
        _textF.userInteractionEnabled = NO;
        [_textF setLayerRadius:72 * 0.5];
        //输入的文字颜色为白色
        _textF.textColor = [UIColor whiteColor];
        //输入框光标的颜色为白色
        _textF.tintColor = [UIColor whiteColor];
        _textF.delegate = self;
        _textF.autocapitalizationType = UITextAutocapitalizationTypeNone;
        _textF.keyboardType = UIKeyboardTypeNumberPad;
        [_textF addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [self addSubview:_textF];
    }
     return _textF;

}


- (void)setNum:(NSInteger)num{
    _num = num;
}

-(void)setTextColor:(UIColor *)textColor{
    _textColor = textColor;
}
-(void)setShowType:(passShowType)showType{
    _showType = showType;
}
-(void)setTintColor:(UIColor *)tintColor{
    _tintColor = tintColor;
}
-(void)show{
    if(_textColor == nil){
        _textColor = [UIColor blackColor];
    }
    if (!_showType) {
        _showType = passShow1;
    }
    if(_tintColor == nil){
        _tintColor = [UIColor blackColor];
    }
    if(!_num ){
        _num =6;//默认
    }
    [self configUI];
    
}
@end
