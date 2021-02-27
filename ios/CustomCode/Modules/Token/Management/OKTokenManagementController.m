//
//  TokenManagementController.m
//  OneKey
//
//  Created by zj on 2021/2/23.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenManagementController.h"
#import "OKToken.h"
#import "OKTokenCell.h"

@interface OKTokenManagementController () <UITableViewDataSource , UITableViewDelegate, UISearchResultsUpdating>
@property (nonatomic, strong) NSArray<OKTokenModel *> *data;
@property (nonatomic, strong) NSArray<OKTokenModel *> *filteredData;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) UISearchController *searchController;

@end

@implementation OKTokenManagementController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tokens" bundle:nil] instantiateViewControllerWithIdentifier:@"OKTokenManagementController"];
}



- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"代币管理";
//    self.navigationController.navigationBar.backgroundColor = [UIColor yellowColor];
    self.filteredData = self.data;
    self.searchController = [[UISearchController alloc] initWithSearchResultsController:nil];
    self.searchController.searchBar.searchBarStyle = UISearchBarStyleMinimal;
    self.searchController.searchResultsUpdater = self;
    self.searchController.dimsBackgroundDuringPresentation = NO;
    self.extendedLayoutIncludesOpaqueBars = YES;
    [self.searchController.searchBar sizeToFit];
    self.searchController.searchBar.width *= 0.8;

    self.tableView.tableHeaderView = self.searchController.searchBar;
    self.definesPresentationContext = YES;
}

- (NSArray<OKTokenModel *> *)data {
    static int c = 0;
    if (!_data) {
        NSMutableArray *mutData = [[NSMutableArray alloc] init];
        for (int i = 0; i < 30; i++) {
            OKTokenModel *token = [[OKTokenModel alloc] init];
            token.name = [NSString stringWithFormat:@"BTC%i", c++];
            token.address = @"fwefnqerkjnviueanviqcu";
            token.blance = @(222222);
            token.isOn = NO;
            [mutData addObject:token];
        }
        _data = mutData;
    }
    return _data;
}

- (nonnull UITableViewCell *)tableView:(nonnull UITableView *)tableView cellForRowAtIndexPath:(nonnull NSIndexPath *)indexPath {
    static NSString *ID = @"OKTokenCell";
    OKTokenCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKTokenCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.isTop = indexPath.row == 0;
    cell.isBottom = indexPath.row == self.filteredData.count - 1;
    cell.model = self.filteredData[indexPath.row];
    return cell;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.filteredData.count;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 64;
}

- (void)updateSearchResultsForSearchController:(UISearchController *)searchController {

    NSString *searchText = searchController.searchBar.text;
    if (searchText) {
        if (searchText.length != 0) {
            self.filteredData = [self.data ok_filter:^BOOL(id obj) {
                return [((OKTokenModel *)obj).name containsString:searchText];
            }];
        } else {
            self.filteredData = self.data;
        }
        [self.tableView reloadData];
    }
}


@end
