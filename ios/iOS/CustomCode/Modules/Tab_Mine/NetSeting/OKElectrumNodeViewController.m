//
//  OKElectrumNodeViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/24.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKElectrumNodeViewController.h"
#import "OKElectrumNodeTableViewCell.h"
#import "OKElectrumNodeModel.h"

@interface OKElectrumNodeViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *ipLLabel;
@property (weak, nonatomic) IBOutlet UILabel *portLLabel;
@property (weak, nonatomic) IBOutlet UITextField *ipRTextField;
@property (weak, nonatomic) IBOutlet UITextField *portRTextField;
@property (weak, nonatomic) IBOutlet UILabel *alternativeNodesLabel;
@property (strong, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *allData;
@end


@implementation OKElectrumNodeViewController

+ (instancetype)electrumNodeViewController
{
    return [[UIStoryboard storyboardWithName:@"NetSeting" bundle:nil]instantiateViewControllerWithIdentifier:@"OKElectrumNodeViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = MyLocalizedString(@"Electrum node", nil);
    self.titleLabel.text = MyLocalizedString(@"Electrum node selection college node traded using the open source distributed radio and access to information on the chain", nil);
    self.alternativeNodesLabel.text = MyLocalizedString(@"Alternative nodes", nil);
    self.ipRTextField.userInteractionEnabled = NO;
    self.portRTextField.userInteractionEnabled = NO;
    self.tableView.tableFooterView = [UIView new];
    
    NSDictionary *dict =  [kPyCommandsManager callInterface:kInterfaceget_server_list parameter:@{}];
    NSArray *keys = [dict allKeys];
    NSMutableArray *arrayM = [NSMutableArray array];
    for (int i = 0; i < keys.count; i++) {
        OKElectrumNodeModel *model = [OKElectrumNodeModel new];
        model.ip = keys[i];
        NSDictionary *subDict = dict[model.ip];
        model.pruning = [subDict safeStringForKey:@"pruning"];
        model.s = [subDict safeStringForKey:@"s"];
        model.t = [subDict safeStringForKey:@"t"];
        model.version = [subDict safeStringForKey:@"version"];
        [arrayM addObject:model];
    }
    self.allData = arrayM;
    [self.tableView reloadData];
    [self refreshUI];
}

- (void)refreshUI
{
    NSArray *array = [kUserSettingManager.electrum_server componentsSeparatedByString:@":"];
    NSString *ip = [array firstObject];
    NSString *port = [array lastObject];
    self.ipRTextField.text = ip;
    self.portRTextField.text = port;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.allData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKElectrumNodeTableViewCell";
    OKElectrumNodeTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKElectrumNodeTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKElectrumNodeModel *model = self.allData[indexPath.row];
    NSString *server = [NSString stringWithFormat:@"%@:%@",model.ip,model.s];
    if (![server isEqualToString:kUserSettingManager.electrum_server]) {
        [kUserSettingManager setElectrum_server:server];
        [kPyCommandsManager callInterface:kInterfaceset_server parameter:@{@"host":model.ip,@"port":model.s}];
        [self refreshUI];
    }
}

@end
