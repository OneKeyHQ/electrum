//
//  OKProxyServerTableViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKProxyServerTableViewController.h"
#import "OKSocketTypeSelectController.h"
#import "OKProxyServerModel.h"

@interface OKProxyServerTableViewController ()

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *serverLLabel;
@property (weak, nonatomic) IBOutlet UISwitch *serverSwitch;
- (IBAction)serverSwitchClick:(UISwitch *)sender;
@property (weak, nonatomic) IBOutlet UILabel *nodeTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *ipAddressLabel;
@property (weak, nonatomic) IBOutlet UITextField *ipaddressTextField;
@property (weak, nonatomic) IBOutlet UILabel *portLLabel;
@property (weak, nonatomic) IBOutlet UITextField *portRTextField;
@property (weak, nonatomic) IBOutlet UILabel *userNameLLabel;
@property (weak, nonatomic) IBOutlet UITextField *userNameTextField;
@property (weak, nonatomic) IBOutlet UILabel *pwdLLabel;
@property (weak, nonatomic) IBOutlet UITextField *pwdTextField;
@property (weak, nonatomic) IBOutlet UILabel *NodeTypeRLabel;
@property (weak, nonatomic) IBOutlet UIButton *confirmBtn;

@property (nonatomic,strong)OKProxyServerModel *currentModel;
@property (weak, nonatomic) IBOutlet UIButton *nodeTypeButton;
@end

@implementation OKProxyServerTableViewController

+ (instancetype)proxyServerTableViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKProxyServerTableViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
 
    self.title = MyLocalizedString(@"Proxy server", nil);
    self.serverLLabel.text = MyLocalizedString(@"Using a proxy server", nil);
    self.nodeTypeLabel.text = MyLocalizedString(@"The node type", nil);
    self.ipAddressLabel.text = MyLocalizedString(@"The IP address", nil);
    self.userNameLLabel.text = MyLocalizedString(@"The user name", nil);
    self.pwdLLabel.text = MyLocalizedString(@"password", nil);
    self.NodeTypeRLabel.text = @"";
    [self.confirmBtn setLayerRadius:20];
    
    OKProxyServerModel *model = [OKProxyServerModel mj_objectWithKeyValues:[kUserSettingManager.currentProxyDict mj_JSONObject]];
    self.currentModel = model;
    self.serverSwitch.on = self.currentModel.proxyOn;
    [self changetextFieldData];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(textFieldDidChange) name:UITextFieldTextDidChangeNotification object:nil];
    [self checkBtn];
}

- (void)changetextFieldData
{
    if (self.serverSwitch.isOn) {
        self.NodeTypeRLabel.userInteractionEnabled = YES;
        self.nodeTypeButton.userInteractionEnabled = YES;
        self.ipaddressTextField.userInteractionEnabled = YES;
        self.portRTextField.userInteractionEnabled = YES;
        self.userNameTextField.userInteractionEnabled = YES;
        self.pwdTextField.userInteractionEnabled = YES;
        
        self.NodeTypeRLabel.text = self.currentModel.type;
        self.ipaddressTextField.text = self.currentModel.ipAddress;
        self.portRTextField.text = self.currentModel.port;
        self.userNameTextField.text = self.currentModel.userName;
        self.pwdTextField.text = self.currentModel.pwd;
    }else{
        self.NodeTypeRLabel.userInteractionEnabled = NO;
        self.nodeTypeButton.userInteractionEnabled = NO;
        self.ipaddressTextField.userInteractionEnabled = NO;
        self.portRTextField.userInteractionEnabled = NO;
        self.userNameTextField.userInteractionEnabled = NO;
        self.pwdTextField.userInteractionEnabled = NO;
        
        self.NodeTypeRLabel.text = @"";
        self.ipaddressTextField.text = @"";
        self.portRTextField.text = @"";
        self.userNameTextField.text = @"";
        self.pwdTextField.text = @"";
    }
}
- (IBAction)serverSwitchClick:(UISwitch *)sender {
    [self changetextFieldData];
    [self checkBtn];
}
- (IBAction)confirmBtnClick:(UIButton *)sender {
    OKProxyServerModel *model = [OKProxyServerModel new];
    if (self.serverSwitch.isOn) {
        model.proxyOn = self.serverSwitch.isOn;
        model.type = self.NodeTypeRLabel.text;
        model.ipAddress = self.ipaddressTextField.text;
        model.port = self.portRTextField.text;
        model.userName = self.userNameTextField.text;
        model.pwd = self.pwdTextField.text;
    }else{
        model.proxyOn = self.serverSwitch.isOn;
        model.type = @"";
        model.ipAddress = @"";
        model.port = @"";
        model.userName = @"";
        model.pwd = @"";
    }
    id result = [kPyCommandsManager callInterface:kInterfaceset_proxy parameter:@{@"proxy_mode":model.type,@"proxy_host":model.ipAddress,@"proxy_port":model.port,@"proxy_user":model.userName,@"proxy_password":model.pwd}];
    if (result != nil) {
        [kTools tipMessage:MyLocalizedString(@"Set up the success", nil)];
        [kUserSettingManager setCurrentProxyDict:[model mj_JSONString]];
        [self.navigationController popViewControllerAnimated:YES];
    }
}
#pragma mark - TextField
- (void)textFieldDidChange
{
    [self checkBtn];
}
- (void)checkBtn
{
    if (self.serverSwitch.isOn == NO) {
        [self.confirmBtn checkBtnStatus:YES];
        return;
    }
    if (self.NodeTypeRLabel.text.length > 0 && self.ipaddressTextField.text.length > 0 && self.portRTextField.text.length > 0 && self.userNameTextField.text.length > 0) {
        [self.confirmBtn checkBtnStatus:YES];
    }else{
        [self.confirmBtn checkBtnStatus:NO];
    }
}
- (IBAction)rightNodeTypeBtnClick:(UIButton *)sender {
    OKWeakSelf(self)
    OKSocketTypeSelectController *socketVc = [OKSocketTypeSelectController socketTypeSelectController:^(NSString *type) {
        weakself.NodeTypeRLabel.text = type;
        [weakself checkBtn];
    }];
    socketVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self.OK_TopViewController presentViewController:socketVc animated:NO completion:nil];

}
@end
