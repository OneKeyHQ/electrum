//
//  OKSignatureViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/8.
//  Copyright © 2021 Onekey. All rights reserved.
//
typedef enum {
    OKSegmentTypeMsg,
    OKSegmentTypeTrad
}OKSegmentType;


#import "OKSignatureViewController.h"
#import "MLMSegmentManager.h"
#import "OKDocument.h"
#import "OKiCloudManager.h"
#import "OKVerifySignatureViewController.h"

#define kBottomBgViewH 100.0

@interface OKSignatureViewController ()<UITextViewDelegate,UIDocumentPickerDelegate>
- (IBAction)confirmBtnClick:(UIButton *)sender;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *confirmBtn;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *topBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UITextView *textView;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *btnBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *textViewBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *placeLabel;

@property (nonatomic, strong) MLMSegmentHead *segHead;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong)NSArray *list;
@property (nonatomic, assign)OKSegmentType type;
@end

@implementation OKSignatureViewController

+ (instancetype)signatureViewController
{
    return [[UIStoryboard storyboardWithName:@"Signature" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSignatureViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _count = 0;
    _type = OKSegmentTypeMsg;
    [self stupUI];
    [self refreshUI];
}

- (void)stupUI
{
    [self setNavigationBarBackgroundColorWithClearColor];
    self.title = MyLocalizedString(@"The signature", nil);
    CGFloat btnH = 30;
    NSString *str = MyLocalizedString(@"Verify the signature", nil);
    CGFloat btnW =  [str getWidthWithHeight:btnH font:14];
    UIView *rightBtn = [[UIView alloc]initWithFrame:CGRectMake(0, 0, btnW + 20 ,btnH)];
    rightBtn.backgroundColor = HexColorA(0x26CF02, 0.1);
    [rightBtn setLayerRadius:btnH * 0.5];
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(10, 0, btnW, btnH)];
    label.text = str;
    label.font = [UIFont systemFontOfSize:14];
    label.textColor = HexColor(0x00B812);
    label.textAlignment = NSTextAlignmentCenter;
    [rightBtn addSubview:label];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapRightClick)];
    [rightBtn addGestureRecognizer:tap];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:rightBtn];
    [self.confirmBtn setLayerRadius:20];
    [self.confirmBtn setTitle:MyLocalizedString(@"determine", nil) forState:UIControlStateNormal];
    [self.textViewBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (_count == 0) {
        [self segmentStyle];
        _count ++;
    }
}

#pragma mark - 右侧按钮
- (void)tapRightClick
{
    OKVerifySignatureViewController *verifySignatureVc = [OKVerifySignatureViewController initWithStoryboardName:@"Signature" identifier:@"OKVerifySignatureViewController"];
    [self.navigationController pushViewController:verifySignatureVc animated:YES];
}
#pragma mark - 均分下划线
- (void)segmentStyle{
    self.list = @[MyLocalizedString(@"trading", nil),
                  MyLocalizedString(@"The message", nil),
                  ];
    CGFloat headerH = 36;
    _segHead = [[MLMSegmentHead alloc] initWithFrame:CGRectMake(0,0, self.topBgView.width, (headerH)) titles:self.list headStyle:SegmentHeadStyleSlide layoutStyle:MLMSegmentLayoutDefault];
    _segHead.slideCorner = 7;
    _segHead.fontSize = (15);
    _segHead.headColor = RGBA(118, 118, 118, 0.12);
    _segHead.slideColor = [UIColor whiteColor];
    _segHead.selectColor = [UIColor blackColor];
    _segHead.deSelectColor = [UIColor blackColor];
    _segHead.bottomLineHeight = 0;
    _segHead.bottomLineColor = [UIColor lightGrayColor];
    
    _segHead.slideScale = 0.98;
    @weakify(self)
    [MLMSegmentManager associateHead:_segHead withScroll:nil completion:^{
        @strongify(self)
        [self.topBgView addSubview:self.segHead];
    } selectEnd:^(NSInteger index) {
        weak_self.type = (OKSegmentType)index;
        [weak_self refreshUI];
    }];
}

- (void)refreshUI
{
    switch (_type) {
        case OKSegmentTypeTrad:
        {
            self.placeLabel.text = MyLocalizedString(@"Enter the transaction message", nil);
        }
            break;
        case OKSegmentTypeMsg:
        {
            self.placeLabel.text = MyLocalizedString(@"Enter the message to be signed", nil);
        }
            break;
        default:
            break;
    }
}

- (IBAction)theImportBtnClick:(UIButton *)sender {
        NSArray *documentTypes = @[@"public.text",
                                   @"public.content",
                                   @"public.source-code",
                                   @"public.audiovisual-content",
                                   @"com.adobe.pdf",
                                   @"com.apple.keynote.key",
                                   @"com.microsoft.word.doc",
                                   @"com.microsoft.excel.xls",
                                   @"com.microsoft.powerpoint.ppt"];
        UIDocumentPickerViewController *documentPickerViewController = [[UIDocumentPickerViewController alloc] initWithDocumentTypes:documentTypes inMode:UIDocumentPickerModeImport];
        documentPickerViewController.delegate = self;
        [self presentViewController:documentPickerViewController animated:YES completion:nil];
}
#pragma mark - UIDocumentPickerDelegate
- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentAtURL:(NSURL *)url {
    NSArray *array = [[url absoluteString] componentsSeparatedByString:@"/"];
    NSString *fileName = [array lastObject];
    fileName = [fileName stringByRemovingPercentEncoding];
    if ([OKiCloudManager iCloudEnable]) {
        [OKiCloudManager downloadWithDocumentURL:url callBack:^(id obj) {
            NSData *data = obj;
            NSString *path = [NSHomeDirectory() stringByAppendingString:[NSString stringWithFormat:@"/Documents/%@",fileName]];
            [data writeToFile:path atomically:YES];
        }];
    }
}

- (IBAction)scanBtnClick:(UIButton *)sender {
    NSLog(@"点击了扫码");
}
- (IBAction)pasteBtnClick:(OKButton *)sender {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    if (pasteboard.string.length > 0) {
        self.textView.text = pasteboard.string;
        self.placeLabel.hidden = YES;
    }
}
- (IBAction)confirmBtnClick:(UIButton *)sender {
    NSLog(@"点击了确定按钮");
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(nonnull NSString *)text {
    if (textView == self.textView) {
        if (text.length == 0) {
            if (textView.text.length == 1) {
                self.placeLabel.hidden = NO;
            }
        } else {
            if (self.placeLabel.hidden == NO) {
                self.placeLabel.hidden = YES;
            }
        }
    }
    return YES;
}
- (void)dealloc
{
    _count = 0;
}
@end
