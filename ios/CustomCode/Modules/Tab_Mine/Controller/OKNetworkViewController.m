//
//  OKNetworkViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKNetworkViewController.h"
#import "OKNetSetingViewController.h"
#import "OKTheMarketViewController.h"
#import "OKBrowserBTCTableViewController.h"
#import "OKElectrumNodeViewController.h"
#import "OKProxyServerTableViewController.h"

@interface OKNetworkViewController ()
@property (weak, nonatomic) IBOutlet UILabel *sysServerLabel;
@property (weak, nonatomic) IBOutlet UILabel *marketLabel;
@property (weak, nonatomic) IBOutlet UILabel *btcBLabel;
@property (weak, nonatomic) IBOutlet UILabel *electrumNodeLabel;


@end

@implementation OKNetworkViewController
+ (instancetype)networkViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKNetworkViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"network", nil);
    

    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userSetingSysServerComplete) name:kUserSetingSysServerComplete object:nil];
    [self userSetingSysServerComplete];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userSetingBtcBComplete) name:kUserSetingBtcBComplete object:nil];
    [self userSetingBtcBComplete];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userSetingMarketSource) name:kUserSetingMarketSource object:nil];
    [self userSetingMarketSource];
    
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userSetingElectrumServer) name:kUserSetingElectrumServer object:nil];
    [self userSetingElectrumServer];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.row) {
        case 1:
        {
            OKNetSetingViewController *netSettingVc = [OKNetSetingViewController netSetingViewController];
            [self.navigationController pushViewController:netSettingVc animated:YES];
        }
            break;
        case 3:
        {
            OKTheMarketViewController *theMarketVc = [OKTheMarketViewController theMarketViewController];
            [self.navigationController pushViewController:theMarketVc animated:YES];
        }
            break;
        case 5:
        {
            OKBrowserBTCTableViewController *browserBTC = [OKBrowserBTCTableViewController browserBTCTableViewController];
            [self.navigationController pushViewController:browserBTC animated:YES];
        }
            break;
        case 7:
        {
            OKElectrumNodeViewController *electrumNodeVc = [OKElectrumNodeViewController electrumNodeViewController];
            [self.navigationController pushViewController:electrumNodeVc animated:YES];
        }
            break;
        case 9:
        {
            OKProxyServerTableViewController *proxyServerVc = [OKProxyServerTableViewController proxyServerTableViewController];
            [self.navigationController pushViewController:proxyServerVc animated:YES];
        }
            break;
        default:
            break;
    }
}

- (void)userSetingMarketSource
{
    self.marketLabel.text = kUserSettingManager.currentMarketSource;
}

- (void)userSetingSysServerComplete
{
    self.sysServerLabel.text = kUserSettingManager.currentSynchronousServer;
}
- (void)userSetingBtcBComplete
{
    self.btcBLabel.text = kUserSettingManager.currentBtcBrowser;
}

- (void)userSetingElectrumServer
{
    self.electrumNodeLabel.text = kUserSettingManager.electrum_server;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
@end
