//
//  OKSignatureViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/8.
//  Copyright © 2021 Onekey. All rights reserved.
//
typedef enum {
    OKSegmentTypeTrad,
    OKSegmentTypeMsg
}OKSegmentType;


#import "OKSignatureViewController.h"
#import "MLMSegmentManager.h"
#import "OKDocument.h"
#import "OKVerifySignatureViewController.h"
#import "OKSendTxPreInfoViewController.h"
#import "OKSendTxPreModel.h"
#import "OKTransferCompleteController.h"
#import "OKTxDetailViewController.h"

#define kBottomBgViewH 100.0

@interface OKSignatureViewController ()<UITextViewDelegate,UIDocumentPickerDelegate,OKHwNotiManagerDelegate>
- (IBAction)confirmBtnClick:(UIButton *)sender;
@property (unsafe_unretained, nonatomic) IBOutlet OKButton *confirmBtn;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *topBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UITextView *textView;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *btnBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UIView *textViewBgView;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *placeLabel;

@property (nonatomic, strong) MLMSegmentHead *segHead;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong)NSArray *list;
@property (nonatomic, assign)OKSegmentType type;

@property (nonatomic,strong)NSDictionary *hwSignData;

@end

@implementation OKSignatureViewController

+ (instancetype)signatureViewController
{
    return [[UIStoryboard storyboardWithName:@"Signature" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSignatureViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _count = 0;
    _type = OKSegmentTypeTrad;
    [self stupUI];
    [self refreshUI];
    [self textChange];
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
    OKWeakSelf(self)
    [MLMSegmentManager associateHead:_segHead withScroll:nil completion:^{
        [weakself.topBgView addSubview:self.segHead];
    } selectEnd:^(NSInteger index) {
        if (index == 0) {
            weakself.type = OKSegmentTypeTrad;
        }else{
            weakself.type = OKSegmentTypeMsg;
        }
        [weakself refreshUI];
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
- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(nonnull NSArray<NSURL *> *)urls{
        NSString *path = [urls firstObject].absoluteString;
    if ([path hasPrefix:@"file://"]) {
        path = [path substringFromIndex:6];
    }
    NSError *error = nil;
    NSString *str = [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:&error];
    if (error == nil) {
        self.textView.text = str;
        self.placeLabel.hidden = YES;
        [self textChange];
    }else{
        [kTools tipMessage:MyLocalizedString(@"Import failure", nil)];
    }
}
#pragma mark - 扫描
- (IBAction)scanBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    OKWalletScanVC *vc = [OKWalletScanVC initViewControllerWithStoryboardName:@"Scan"];
    vc.scanningType = ScanningTypeAll;
    vc.scanningCompleteBlock = ^(NSString* result) {
        if (result && result.length > 0) {
            weakself.textView.text = result;
            weakself.placeLabel.hidden = YES;
            [weakself textChange];
        }
    };
    [vc authorizePushOn:self];
}

#pragma mark - TextView
- (void)textViewDidChange:(UITextView *)textView
{
    [self textChange];
}

- (void)textChange{
    if (self.textView.text.length > 0) {
        [self.confirmBtn status:OKButtonStatusEnabled];
    }else{
        [self.confirmBtn status:OKButtonStatusDisabled];
    }
}
- (IBAction)pasteBtnClick:(OKButton *)sender {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    if (pasteboard.string.length > 0) {
        self.textView.text = pasteboard.string;
        self.placeLabel.hidden = YES;
        [self textChange];
    }
}

- (IBAction)confirmBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    switch (_type) {
        case OKSegmentTypeMsg:
        {
            NSString *message = self.textView.text;
            [OKHwNotiManager sharedInstance].delegate = self;
            OKWeakSelf(self)
            dispatch_async(dispatch_get_global_queue(0, 0), ^{
               id result = [kPyCommandsManager callInterface:kInterfacesign_message parameter:@{@"address":kWalletManager.currentWalletInfo.addr,@"message":message}];
                if (result != nil) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        NSLog(@"dispatch_async");
                        OKVerifySignatureViewController *verifySignatureVc = [OKVerifySignatureViewController initWithStoryboardName:@"Signature" identifier:@"OKVerifySignatureViewController"];
                        NSDictionary *dict = @{@"message":weakself.textView.text,@"address":kWalletManager.currentWalletInfo.addr,@"signature":result};
                        verifySignatureVc.signMessageInfo = dict;
                        [self.navigationController pushViewController:verifySignatureVc animated:YES];
                    });
                }else{
                    [kTools tipMessage:MyLocalizedString(@"Signature error or cancellation", nil)];
                }
            });
        }
            break;
        case OKSegmentTypeTrad:
        {
            NSString *tx = self.textView.text;
            [OKHwNotiManager sharedInstance].delegate = self;
            dispatch_async(dispatch_get_global_queue(0, 0), ^{
                NSDictionary *signTxDict =  [kPyCommandsManager callInterface:kInterfaceSign_tx parameter:@{@"tx":tx}];
                if (signTxDict != nil) {
                    weakself.hwSignData = signTxDict;
                    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiHwBroadcastiComplete object:nil];
                }
            });
        }
            break;
        default:
            break;
    }
}

- (void)showPreInfoView:(NSDictionary *)dict
{
    OKSendTxPreInfoViewController *sendVc = [OKSendTxPreInfoViewController initViewControllerWithStoryboardName:@"Tab_Wallet"];
    OKSendTxPreModel *model = [OKSendTxPreModel new];
    NSArray *arrayAmount = [[dict safeStringForKey:@"amount"]componentsSeparatedByString:@" "];
    model.amount = [arrayAmount firstObject];
    model.coinType = [arrayAmount objectAtIndex:1];
    model.walletName = kWalletManager.currentWalletInfo.label;
    model.sendAddress = kWalletManager.currentWalletInfo.addr;
    NSArray *arrayAddr = dict[@"output_addr"];
    for (NSDictionary *addrDict in arrayAddr) {
        BOOL is_change = [[addrDict safeStringForKey:@"is_change"] boolValue];
        if (!is_change) {
            model.rAddress = [addrDict safeStringForKey:@"addr"];
            break;
        }
    }
    model.txType = @"";
    model.fee = [NSString stringWithFormat:@"%@",[dict safeStringForKey:@"fee"]];
    sendVc.info = model;
    OKWeakSelf(self)
    [sendVc showOnWindowWithParentViewController:self block:^(NSString * _Nonnull str) {
        if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
            [weakself broadcast_tx:weakself.hwSignData dict:dict];
            return;
        }
    }];
}
- (void)broadcast_tx:(NSDictionary *)signTxDict dict:(NSDictionary *)dict
{
    NSString *signTx = [signTxDict safeStringForKey:@"tx"];
    id result =  [kPyCommandsManager callInterface:kInterfaceBroadcast_tx parameter:@{@"tx":signTx}];
    if (result != nil) {
        OKWeakSelf(self)
        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiSendTxComplete object:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
            OKTransferCompleteController *transferCompleteVc = [OKTransferCompleteController transferCompleteController:dict block:^{
                OKTxDetailViewController *txDetailVc = [OKTxDetailViewController txDetailViewController];
                txDetailVc.tx_hash = [dict safeStringForKey:@"txid"];
                [weakself.navigationController pushViewController:txDetailVc animated:YES];
            }];
            [self.navigationController pushViewController:transferCompleteVc animated:YES];
        });
    }
}
#pragma mark - OKHwNotiManagerDelegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (type == OKHWNotiTypeSendCoinConfirm) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSDictionary* result =  [kPyCommandsManager callInterface:kInterfaceget_tx_info_from_raw parameter:@{@"raw_tx":self.textView.text}];
            if (result != nil) {
                [self showPreInfoView:result];
            }
        });
    }else if(type == OKHWNotiTypePin_Current){
        dispatch_async(dispatch_get_main_queue(), ^{
            OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    id result = [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                    if (result != nil) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [weakself.OK_TopViewController dismissViewControllerWithCount:1 animated:YES complete:^{

                            }];
                        });
                        return;
                    }
                });
            }];
            [weakself.OK_TopViewController presentViewController:pinCodeVc animated:YES completion:nil];
        });
    }
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

- (void)backToPrevious
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    if ([self.navigationController.viewControllers indexOfObject:self]==NSNotFound)
    {
        [self.navigationController popToRootViewControllerAnimated:YES];
    }
}

@end
