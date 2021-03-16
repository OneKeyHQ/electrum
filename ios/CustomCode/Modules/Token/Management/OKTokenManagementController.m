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
#import "OKTokenSectionCell.h"
#import "OKTokenManModel.h"
#import "OKIndexView.h"
#import "OKTokenNoResultView.h"
#import "OKAddTokenController.h"
#import "OKTokenManager.h"


@interface OKTokenManagementController () <UITableViewDataSource , UITableViewDelegate, UISearchResultsUpdating>
@property (nonatomic, strong) OKTokenManModel *model;
@property (nonatomic, strong) NSArray *filteredData;
@property (nonatomic, weak) IBOutlet UITableView *tableView;
@property (nonatomic, strong) UISearchController *searchController;
@property (nonatomic, strong) OKIndexView *indexView;
@property (nonatomic, assign) BOOL searchMode;
@property (nonatomic, strong) OKTokenNoResultView *noResultView;
@end

@implementation OKTokenManagementController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tokens" bundle:nil] instantiateViewControllerWithIdentifier:@"OKTokenManagementController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"token.management".localized;
    self.titleColor = UIColor.blackColor;
    self.model = [OKTokenManModel new];
    self.filteredData = self.model.data;

    self.searchController = [[UISearchController alloc] initWithSearchResultsController:nil];
    self.searchController.searchBar.searchBarStyle = UISearchBarStyleMinimal;
    self.searchController.searchResultsUpdater = self;
    self.searchController.dimsBackgroundDuringPresentation = NO;
    self.extendedLayoutIncludesOpaqueBars = YES;
    [self.searchController.searchBar sizeToFit];
    self.searchController.searchBar.tintColor = UIColor.FG_B01;
    self.searchController.searchBar.translucent = NO;

    self.tableView.tableHeaderView = self.searchController.searchBar;
    self.tableView.contentOffset = CGPointMake(0, self.searchController.searchBar.height);
    self.tableView.showsVerticalScrollIndicator = NO;
    self.definesPresentationContext = YES;

    self.indexView = [[OKIndexView alloc] initWithFrame:CGRectZero];
    self.indexView.titles = [self.model sectionIndexTitles];
    OKWeakSelf(self)
    self.indexView.callback = ^(NSString * _Nonnull title, NSInteger index) {
        NSInteger row = [weakself.model indexOfTableViewIndexTitle:title];
        NSIndexPath *path = [NSIndexPath indexPathForRow:row inSection:0] ;
        [weakself.tableView scrollToRowAtIndexPath:path atScrollPosition:UITableViewScrollPositionTop animated:NO];
    };
    [self.view addSubview:self.indexView];

    self.noResultView = [OKTokenNoResultView getView];
    self.noResultView.hidden = YES;
    self.noResultView.addTokenCallback = ^{
        OKAddTokenController *vc = [OKAddTokenController controllerWithStoryboard];
        [weakself.navigationController pushViewController:vc animated:YES];
    };
    [self.view addSubview:self.noResultView];
    [OKTokenManager sharedInstance];
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    self.indexView.right = self.view.width;
    self.indexView.centerY = self.view.height / 2;
    self.noResultView.top = 120;
    self.noResultView.centerX = self.view.width / 2;
}

- (UIColor *)navBarTintColor {
    return UIColor.BG_W02;
}

- (void)setSearchMode:(BOOL)searchMode {
    _searchMode = searchMode;
    self.indexView.hidden = searchMode;
}

- (void)setFilteredData:(NSArray *)filteredData {
    _filteredData = filteredData;
    self.noResultView.hidden = filteredData.count;
}

#pragma mark - UITableViewDataSource & UITableViewDelegate
- (nonnull UITableViewCell *)tableView:(nonnull UITableView *)tableView cellForRowAtIndexPath:(nonnull NSIndexPath *)indexPath {
    id model = self.filteredData[indexPath.row];
    if ([model isKindOfClass:[NSString class]]) {
        static NSString *ID = @"OKTokenSectionCell";
        OKTokenSectionCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        if (cell == nil) {
            cell = [[OKTokenSectionCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        }
        cell.title = model;
        return cell;
    }

    static NSString *ID = @"OKTokenCell";
    OKTokenCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKTokenCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }

    if (self.searchMode) {
        cell.isTop = indexPath.row == 0;
        cell.isBottom = indexPath.row == self.filteredData.count - 1;
    } else {
        cell.isTop = indexPath.row == 1 || indexPath.row == self.model.hot.count + 2;
        cell.isBottom = indexPath.row == self.filteredData.count - 1 || indexPath.row == self.model.hot.count;
    }
    OKWeakSelf(self)
    cell.tokenSwitched = ^(BOOL isOn, OKToken * _Nonnull model) {
        [weakself token:model switchedTo:isOn];
    };
    cell.model = model;
    return cell;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.filteredData.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 64;
}

#pragma mark - UISearchResultsUpdating
- (void)updateSearchResultsForSearchController:(UISearchController *)searchController {

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        searchController.view.subviews.firstObject.backgroundColor = UIColor.BG_W02;
    });

    NSString *searchText = searchController.searchBar.text;
    if (searchText) {
        if (searchText.length != 0) {
            self.searchMode = YES;
            self.filteredData = [[OKTokenManager sharedInstance] tokensFilterWith:searchText];
        } else {
            self.searchMode = NO;
            self.filteredData = self.model.data;
        }
        [self.tableView reloadData];
    }
}

- (void)token:(OKToken *)model switchedTo:(BOOL)isOn {
    NSArray *cells = [self.tableView visibleCells];
    for (id cell in cells) {
        if (![cell isKindOfClass:[OKTokenCell class]]) {
            continue;
        }
        OKTokenCell *tokenCell = (OKTokenCell *)cell;
        if ([tokenCell.model.address isEqualToString:model.address]) {
            tokenCell.isOn = isOn;
        }
    }
}
@end
