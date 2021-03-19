//
//  OKAllAssetsViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKAllAssetsViewController.h"
#import "OKAllAssetsTableViewCell.h"
#import "OKAllAssetsCellModel.h"
#import "UITableView+OKRoundSection.h"

@interface OKAllAssetsViewController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *balanceLabel;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;

@property (nonatomic, strong)NSArray <OKAllAssetsSectionModel *> *data;
@property (nonatomic, strong)NSArray <OKAllAssetsSectionModel *> *showList;
@end

@implementation OKAllAssetsViewController

+ (instancetype)allAssetsViewController {
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKAllAssetsViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.searchBar.placeholder = @"assets.search.placeholder".localized;
    self.title = MyLocalizedString(@"All assets", nil);
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = [UIView new];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeText) name:UITextFieldTextDidChangeNotification object:nil];
    [self loadListData];
}

- (UIColor *)navBarTintColor {
    return UIColor.BG_W02;
}

- (UIScrollView *)scrollViewForNavbar {
    return self.tableView;
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    [self.searchBar endEditing:YES];
}

- (void)loadListData {
    self.loadingIndicator.hidden = NO;
    [kPyCommandsManager asyncCall:kInterface_get_all_wallet_balance parameter:@{} callback:^(id  _Nonnull result) {
        NSDictionary *dict = result;
        NSString *all_balance = [dict safeStringForKey:@"all_balance"];
        NSArray *balanceArray = [all_balance componentsSeparatedByString:@" "];
        NSString *b = [balanceArray firstObject];
        NSDecimalNumber *dn = [NSDecimalNumber decimalNumberWithString:b];
        NSDecimalNumber *resultN = [kTools decimalNumberHandlerWithValue:dn roundingMode:NSRoundUp scale:2];
        self.balanceLabel.text = [NSString stringWithFormat:@"%@ %@", kWalletManager.currentFiatSymbol, resultN];
        NSArray *wallet_info = dict[@"wallet_info"];
        self.data = [OKAllAssetsSectionModel mj_objectArrayWithKeyValuesArray:wallet_info];
        self.showList = [self.data copy];
        [self refreshFooterViewByCount:self.showList.count];
        [self.tableView reloadData];
        self.loadingIndicator.hidden = YES;
    }];
}

- (void)refreshFooterViewByCount:(NSUInteger)count {
    if (count > 0) {
        self.tableView.tableFooterView = [UIView new];
        return;
    }

    UILabel *lab = [UILabel new];
    lab.font = [UIFont systemFontOfSize:21];
    lab.textColor = HexColor(0x9FA6AD);
    lab.text = MyLocalizedString(@"暂无资产", nil);
    lab.textAlignment = NSTextAlignmentCenter;
    [lab sizeToFit];
    self.tableView.tableFooterView = lab;
}

#pragma mark - UITableViewDataSource, UITableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.showList.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.showList[section].wallets.count;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    CGFloat height = [self tableView:tableView heightForHeaderInSection:section];
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.width, height)];
    UILabel *headerLabel = [[UILabel alloc] initWithFrame:CGRectMake(28, 16, self.view.width - 50, 18)];
    headerView.backgroundColor = UIColor.BG_W02;
    headerLabel.text = self.showList[section].name;
    headerLabel.font = [UIFont systemFontOfSize:13];
    headerLabel.textColor = UIColor.FG_B02;
    [headerView addSubview:headerLabel];
    return headerView;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *ID  = @"OKAllAssetsTableViewCell";
    OKAllAssetsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAllAssetsTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.separator.hidden = indexPath.row == [tableView numberOfRowsInSection:indexPath.section] - 1;
    cell.model = self.showList[indexPath.section].wallets[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    [UITableView roundSectiontableView:tableView
                       willDisplayCell:cell
                     forRowAtIndexPath:indexPath
                          cornerRadius:12];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 38;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 64;
}

#pragma mark - changeText
- (void)changeText {
    NSString *text = self.searchBar.text.lowercaseString;

    if (text.length) {
        NSMutableArray <OKAllAssetsSectionModel *>* mutSectionModels = [@[] mutableCopy];
        for (OKAllAssetsSectionModel *sectionModel in self.data) {
            if ([sectionModel.name.lowercaseString containsString:text]) {
                [mutSectionModels addObject:sectionModel];
                continue;
            }
            NSMutableArray <OKAllAssetsCellModel *>* mutCellModels = [@[] mutableCopy];
            for (OKAllAssetsCellModel *cellModel in sectionModel.wallets) {
                if ([cellModel.coin.lowercaseString containsString:text]) {
                    [mutCellModels addObject:cellModel];
                }
            }
            if (mutCellModels.count) {
                OKAllAssetsSectionModel *neoSectionModel = [sectionModel copy];
                neoSectionModel.wallets = mutCellModels;
                [mutSectionModels addObject:neoSectionModel];
            }
        }
        self.showList = mutSectionModels;
    } else {
        self.showList = [self.data copy];
    }
    [self refreshFooterViewByCount:self.showList.count];
    [self.tableView reloadData];
}
@end
