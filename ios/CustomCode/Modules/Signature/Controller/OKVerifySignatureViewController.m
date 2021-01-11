//
//  OKVerifySignatureViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKVerifySignatureViewController.h"
#import "OKVerifySignatureResultController.h"

@interface OKVerifySignatureViewController ()<UITextViewDelegate>
@property (weak, nonatomic) IBOutlet UILabel *topLabel;
@property (weak, nonatomic) IBOutlet UIView *topBgView;
@property (weak, nonatomic) IBOutlet UITextView *topTextView;
@property (weak, nonatomic) IBOutlet UILabel *topTextPlaceLabel;

@property (weak, nonatomic) IBOutlet UILabel *midLabel;
@property (weak, nonatomic) IBOutlet UIView *midBgView;
@property (weak, nonatomic) IBOutlet UITextView *midTextView;
@property (weak, nonatomic) IBOutlet UILabel *midTextPlaceLabel;

@property (weak, nonatomic) IBOutlet UILabel *bottomLabel;
@property (weak, nonatomic) IBOutlet UIView *bottomBgView;
@property (weak, nonatomic) IBOutlet UILabel *bottomTextPlaceLabel;
@property (weak, nonatomic) IBOutlet UITextView *bottomTextView;

@property (weak, nonatomic) IBOutlet UIButton *confirmBtn;
- (IBAction)confirmBtnClick:(UIButton *)sender;

@end

@implementation OKVerifySignatureViewController
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Verify the signature", nil);
    [self stupUI];
}
- (void)stupUI
{
    [self.topBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.midBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.bottomBgView setLayerBoarderColor:HexColor(0xDBDEE7) width:1 radius:20];
    [self.confirmBtn setLayerRadius:20];
    self.topLabel.text = MyLocalizedString(@"The original message", nil);
    self.midLabel.text = MyLocalizedString(@"The public key", nil);
    self.bottomLabel.text = MyLocalizedString(@"Post signed message", nil);
    self.topTextPlaceLabel.text = MyLocalizedString(@"Enter the original message", nil);
    self.midTextPlaceLabel.text = MyLocalizedString(@"Enter the public key", nil);
    self.bottomTextPlaceLabel.text = MyLocalizedString(@"Enter the signed post message", nil);
    [self.confirmBtn setTitle:MyLocalizedString(@"validation", nil) forState:UIControlStateNormal];
    self.topTextView.delegate = self;
    self.midTextView.delegate = self;
    self.bottomTextView.delegate = self;
}
- (IBAction)confirmBtnClick:(UIButton *)sender {
    OKVerifySignatureResultController *verifySignatureVc = [OKVerifySignatureResultController initWithStoryboardName:@"Signature" identifier:@"OKVerifySignatureResultController"];
    verifySignatureVc.type = OKVerifySignatureTypeFailure;
    [self.navigationController pushViewController:verifySignatureVc animated:YES];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(nonnull NSString *)text {
    if (textView == self.topTextView) {
        if (text.length == 0) {
            if (textView.text.length == 1) {
                self.topTextPlaceLabel.hidden = NO;
            }
        } else {
            if (self.topTextPlaceLabel.hidden == NO) {
                self.topTextPlaceLabel.hidden = YES;
            }
        }
    }else if (textView == self.midTextView){
        if (text.length == 0) {
            if (textView.text.length == 1) {
                self.midTextPlaceLabel.hidden = NO;
            }
        } else {
            if (self.midTextPlaceLabel.hidden == NO) {
                self.midTextPlaceLabel.hidden = YES;
            }
        }
    }else if (textView == self.bottomTextView){
        if (text.length == 0) {
            if (textView.text.length == 1) {
                self.bottomTextPlaceLabel.hidden = NO;
            }
        } else {
            if (self.bottomTextPlaceLabel.hidden == NO) {
                self.bottomTextPlaceLabel.hidden = YES;
            }
        }
    }
    return YES;
}
@end
