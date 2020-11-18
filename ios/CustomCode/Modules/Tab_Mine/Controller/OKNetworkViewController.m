//
//  OKNetworkViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
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
            
            break;
        case 3:
            
            break;
        case 5:
            
            break;
        case 7:
            
            break;
        case 9:
            
            break;
        case 11:
            
            break;
        case 13:
            
            break;
        default:
            break;
    }
}



@end
