//
//  OKManagerHDViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKManagerHDViewController.h"

@interface OKManagerHDViewController () <UITableViewDelegate,UITableViewDataSource>

@end

@implementation OKManagerHDViewController

+ (instancetype)managerHDViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil]instantiateViewControllerWithIdentifier:@"OKManagerHDViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = MyLocalizedString(@"management", nil);
    self.tableView.tableFooterView = [UIView new];
}

#pragma mark - Table view data source

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"didSelectRowAtIndexPath");
}
@end
