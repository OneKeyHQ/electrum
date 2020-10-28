//
//  OKWordImportView.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import "OKWordImportView.h"
#import "OKDeleteTextField.h"

#define Margin 10
#define CountPerRow  3
#define MaxCountPerColumn 4
#define MinCountPerColumn 4
#define HeightPerCell 35

@interface OKWordImportView()<UITextFieldDelegate, OKDeleteTextFieldDeleteProtocol>
{
    NSInteger _selectedIndex;
    NSMutableArray<OKDeleteTextField *> *_tfs;
}
@property (weak, nonatomic) IBOutlet UIStackView *stackView;

@end

@implementation OKWordImportView

- (instancetype)init {
    if (self = [super init]) {
        [self configSubviews];
    }
    return self;
}

- (void)awakeFromNib {
    [super awakeFromNib];
    [self configSubviews];
}

- (void)configSubviews {
    _tfs = [NSMutableArray arrayWithCapacity:CountPerRow * MinCountPerColumn];

    int column = 0;
    for (UIView *bgView in _stackView.subviews) {
        for (int i = 0; i < CountPerRow; ++i) {
            OKDeleteTextField *textF = [[OKDeleteTextField alloc] init];
            textF.tag = i + column * CountPerRow;
            textF.layer.cornerRadius = 5;
            textF.backgroundColor = [UIColor whiteColor];
            textF.textColor = [UIColor blackColor];
            textF.textAlignment = NSTextAlignmentCenter;
            textF.autocorrectionType = UITextAutocorrectionTypeNo;
            textF.autocapitalizationType = UITextAutocapitalizationTypeNone;
            textF.font = [UIFont fontWithName:kFontPingFangMediumBold size:16];
            textF.hidden = YES;
            textF.delegate = self;
            textF.deleteDelegate = self;
            textF.inputAccessoryView = [UIView new];
            [bgView addSubview:textF];
            [_tfs addObject:textF];
        }
        ++column;
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];

    CGFloat width = (self.frame.size.width - Margin * (CountPerRow + 1)) / CountPerRow;
    int i = 0;
    for (UITextField *textF in _tfs) {

        CGFloat x = Margin + (Margin + width) * (i % CountPerRow);
        textF.frame = CGRectMake(x, 0, width, HeightPerCell);

        ++i;
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    _selectedIndex = textField.tag;
    textField.textColor = UIColorFromRGB(0x333333);
    [textField setLayerBoarderColor:[UIColor whiteColor] width:0];
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    if ([textField.text containsString:@" "]) {  // Fix 12.0以下系统多个空格
        textField.text = [textField.text stringByReplacingOccurrencesOfString:@" " withString:@""];
    }
    [self changeColor:textField];
    [self checkCompleted];
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {

    NSArray *wordsArr = [string componentsSeparatedByString:@" "];

    if (wordsArr.count > 2) { // 粘贴
        [self configureData:wordsArr];
        return NO;
    }

    if ([string isEqualToString:@" "]) { // 后移1个
        if (_tfs.lastObject.hidden == NO) {
            [self.superview endEditing:YES];
            return NO;
        }

        [self autoAdjustHeight:1];

        NSInteger targetIndex = _selectedIndex + 1;

        for (NSInteger i = targetIndex; i < _tfs.count; ++i) {
            UITextField *textF = _tfs[i];
            if (textF.hidden) { // 寻找距离最近的一个
                textF.hidden = NO;
                // 反向赋值
                for (NSInteger j = i; j > targetIndex; --j) {
                    _tfs[j].text = _tfs[j-1].text;
                    [self changeColor:_tfs[j]];
                }
                break;
            }
        }

        _tfs[targetIndex].text = nil;
        [_tfs[targetIndex] becomeFirstResponder];
    }

    return YES;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    for (NSInteger i = _tfs.count - 1; i >= 0; --i) {
        if (_tfs[i].hidden == NO) {
            [_tfs[i] becomeFirstResponder];
            break;
        }
        if (i == 0) {
            UITextField *textF = _tfs[0];
            textF.hidden = NO;
            [textF becomeFirstResponder];
        }
    }
}

- (void)deleteBackward {

    if (!_tfs[_selectedIndex].text.length &&
        !(_selectedIndex == 0 && _tfs[_selectedIndex+1].hidden)) { // 往前删除1个

        for (NSInteger i = _selectedIndex; i < _tfs.count; ++i) {

            if (_selectedIndex == _tfs.count - 1) {
                _tfs[i].hidden = YES;

            }else if (_tfs[i+1].hidden) { // 后一个赋值给前一个
                _tfs[i].hidden = YES;
                _tfs[i].text = nil;
                break;

            }else{
                _tfs[i].text = _tfs[i+1].text;
                [self changeColor:_tfs[i]];
                if (i == _tfs.count - 2) { // 最后一个
                    _tfs[i+1].hidden = YES;
                    _tfs[i+1].text = nil;
                }
            }
        }

        [self autoAdjustHeight:0];

        [_tfs[_selectedIndex - 1] becomeFirstResponder];
    }
}

- (void)AfterDeleteBackward {
    [self checkCompleted];
}

//需要调用助记词列表判断
- (void)changeColor:(UITextField *)textField {
//    if ([kTools containsInAllWords:textField.text] == NO) {
//        textField.textColor = UIColorFromRGB(RGB_TEXT_RED);
//        if (textField.text.length > 0) {
//          [textField setLayerBoarderColor:UIColorFromRGB(RGB_TEXT_RED) width:1];
//        }
//    }else{
//        textField.textColor = [UIColor blackColor];
//        [textField setLayerBoarderColor:[UIColor whiteColor] width:0];
//    }
}

- (void)changBackgroundColor:(UITextField *)textField{
    if (textField.text.length) {
        textField.backgroundColor = [UIColor whiteColor];
    }else{
        textField.backgroundColor = [UIColor clearColor];
    }
}

- (void)configureData:(NSArray *)data {
    if (!data || !data.count) {
        UITextField *textF = _tfs[0];
        textF.hidden = NO;
        [textF becomeFirstResponder];
        return;
    }

    // 找出最近的hide为起始点
    NSInteger start = 0;
    for (NSInteger j = 0; j < _tfs.count; ++j) {
        if (!_tfs[j].text.length || _tfs[j].hidden == YES) {
            start = j;
            break;
        }
    }

    // 顺序赋值
    for (NSInteger i = 0; i < data.count; ++i) {
        if (start+i < _tfs.count) {
            UITextField *textF = _tfs[start+i];
            textF.text = data[i];
            textF.hidden = NO;
            [self changeColor:textF];
            [self autoAdjustHeight:2];
        }
    }

    [self.superview endEditing:YES];
    [self checkCompleted];
}

- (void)checkCompleted {
//    if (_completed) {
//        BOOL completed = NO;
//        if (!_tfs[11].hidden && _tfs[11].text.length) {
//            completed = [ATCreateWalletService checkEveryWordInPlist:self.wordsArr];
//        }else{
//            completed = NO;
//        }
//        _completed(completed);
//    }
    
    if (_completed) {
        _completed(YES);
    }

}

- (void)autoAdjustHeight:(NSUInteger)tag { // tag: 0 删除；1 新增；2 批量导入
    NSInteger lastIndex = _selectedIndex;

    for (OKTextField *textF in _tfs) {
        if (textF.hidden) {
            lastIndex = textF.tag;
            if (tag == 2) {
                --lastIndex;
            }
            break;
        }
    }

    NSUInteger column = lastIndex / CountPerRow;

    if (tag == 0) {
        if (lastIndex % CountPerRow == 0 &&
            column >= MinCountPerColumn) {
            _stackView.subviews[column].hidden = column >= MinCountPerColumn;
        }
    }else{
        if (lastIndex % CountPerRow == 0 &&
            column >= MinCountPerColumn) {
            _stackView.subviews[column].hidden = !(column >= MinCountPerColumn);
        }
    }
}

- (NSArray *)wordsArr {
    NSMutableArray *wordsArr = [NSMutableArray arrayWithCapacity:CountPerRow * MinCountPerColumn];
    for (UITextField *textF in _tfs) {
        if (textF.text.length) {
            [wordsArr addObject:textF.text];
        }
    }
    return [wordsArr copy];
}

@end
