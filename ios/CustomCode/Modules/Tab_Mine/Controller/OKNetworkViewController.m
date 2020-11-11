//
//  OKNetworkViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKNetworkViewController.h"

@interface OKNetworkViewController ()

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
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.row) {
        case 1:
            [kTools tipMessage:@"点击了同步服务器"];
            break;
        case 3:
            [kTools tipMessage:@"点击了价格行情"];
            break;
        case 5:
            [kTools tipMessage:@"点击了区块浏览器BTC"];
            break;
        case 7:
            [kTools tipMessage:@"点击了区块浏览器ETH"];
            break;
        case 9:
            [kTools tipMessage:@"点击了ETH节点选择"];
            break;
        case 11:
            [kTools tipMessage:@"点击了代理服务器"];
            break;
        case 13:
            
            break;
        default:
            break;
    }
}



@end
