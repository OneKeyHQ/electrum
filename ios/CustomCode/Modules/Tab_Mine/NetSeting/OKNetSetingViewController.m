//
//  OKNetSetingViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/23.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKNetSetingViewController.h"

@interface OKNetSetingViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *useServerLabel;
@property (weak, nonatomic) IBOutlet UISwitch *switchR;
- (IBAction)switchRClick:(UISwitch *)sender;
@property (weak, nonatomic) IBOutlet UILabel *ipLLabel;
@property (weak, nonatomic) IBOutlet UITextField *ipRTextField;
@property (weak, nonatomic) IBOutlet UILabel *portLLabel;
@property (weak, nonatomic) IBOutlet UITextField *portRLabel;
@property (weak, nonatomic) IBOutlet UIView *bottomBgView;
@property (weak, nonatomic) IBOutlet UIButton *confirmBtn;
- (IBAction)confirmBtnClick:(UIButton *)sender;
@property (nonatomic,copy)NSString *defaultSeting;
@end

@implementation OKNetSetingViewController

+ (instancetype)netSetingViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKNetSetingViewController"];
}

- (void)viewDidLoad {
    
    [super viewDidLoad];
    
    [self.confirmBtn setLayerRadius:20];
    
    self.title = MyLocalizedString(@"Synchronous server", nil);
    
    self.titleLabel.text = MyLocalizedString(@"The synchronous server USES the synchronous server to back up and restore multi-signature wallet, and synchronizes unsigned transactions among multiple co-signers; The synchronization server stores only public information such as the wallet public key and name, and does not store any private keys and personal information", nil);
    
    NSString *labelText = MyLocalizedString(@"Restore the default", nil);
    CGFloat labelW = [labelText getWidthWithHeight:30 font:14];
    CGFloat labelmargin = 10;
    CGFloat labelH = 30;
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(labelmargin, 0, labelW, labelH)];
    label.text = labelText;
    label.font = [UIFont boldSystemFontOfSize:14];
    label.textColor = HexColor(0x26CF02);
    
    UIView *rightView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, labelW + labelmargin * 2, labelH)];
    rightView.backgroundColor = HexColorA(0x26CF02, 0.1);
    [rightView setLayerRadius:labelH * 0.5];
    [rightView addSubview:label];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:rightView];
    
    UITapGestureRecognizer *tapRightViewClick = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapRightViewClick)];
    [rightView addGestureRecognizer:tapRightViewClick];
    self.defaultSeting =  [kPyCommandsManager callInterface:kInterfaceget_sync_server_host parameter:@{}];
    
    if (kUserSettingManager.sysServerFlag) {
        self.switchR.on = YES;
    }else{
        self.switchR.on = NO;
    }
    [self.confirmBtn checkBtnStatus:self.switchR.isOn];
    
    
    if (kUserSettingManager.currentSynchronousServer == nil || kUserSettingManager.currentSynchronousServer.length == 0) {
        [self refreshUI:self.defaultSeting];
    }else{
        [self refreshUI:kUserSettingManager.currentSynchronousServer];
    }
}

- (void)refreshUI:(NSString *)seting
{
    if (self.switchR.isOn) {
        self.ipRTextField.textColor = HexColor(0x14293B);
        self.portRLabel.textColor = HexColor(0x14293B);
        self.ipRTextField.userInteractionEnabled = YES;
        self.portRLabel.userInteractionEnabled = YES;
    }else{
        self.ipRTextField.textColor = HexColorA(0x14293B, 0.3);
        self.portRLabel.textColor = HexColorA(0x14293B, 0.3);
        self.ipRTextField.userInteractionEnabled = NO;
        self.portRLabel.userInteractionEnabled = NO;
    }
    if (seting.length != 0 &&  seting != nil) {
        NSArray *strArray = [seting componentsSeparatedByString:@":"];
        NSString *ip = [strArray firstObject];
        NSString *port = [strArray lastObject];
        self.ipRTextField.text = ip;
        self.portRLabel.text = port;
    }
}

- (void)tapRightViewClick
{
    [self refreshUI:self.defaultSeting];
}

- (IBAction)switchRClick:(UISwitch *)sender {
    NSString *flag = sender.isOn == YES ? @"1" : @"0";
    [kPyCommandsManager callInterface:kInterfaceset_syn_server parameter:@{@"flag":flag}];
    [kUserSettingManager setSysServerFlag:sender.isOn];
    [self.confirmBtn checkBtnStatus:sender.isOn];
    NSString *ip = self.ipRTextField.text;
    NSString *port = self.portRLabel.text;
    NSString *setingStr = [NSString stringWithFormat:@"%@:%@",ip,port];
    [self refreshUI:setingStr];
}

- (IBAction)confirmBtnClick:(UIButton *)sender {
    
    if (self.ipRTextField.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The IP address cannot be empty", nil)];
        return;
    }
    
    if (self.portRLabel.text.length == 0) {
        [kTools tipMessage:MyLocalizedString(@"The port cannot be empty", nil)];
        return;
    }
    
    
    [kPyCommandsManager callInterface:kInterfaceset_sync_server_host parameter:@{@"ip":self.ipRTextField.text , @"port":self.portRLabel.text}];
    NSString *ipportStr = [NSString stringWithFormat:@"%@:%@",self.ipRTextField.text,self.portRLabel.text];
    [kUserSettingManager setCurrentSynchronousServer:ipportStr];
    [[NSNotificationCenter defaultCenter]postNotificationName:kUserSetingSysServerComplete object:nil];
    [self.navigationController popViewControllerAnimated:YES];
}

@end
