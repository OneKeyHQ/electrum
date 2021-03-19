//
//  OKTokenSelectController.m
//  OneKey
//
//  Created by zj on 2021/3/9.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKTokenSelectController.h"
#import "OKAllAssetsTableViewCell.h"
#import "UITableView+OKRoundSection.h"

@interface OKTokenSelectController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (nonatomic, strong)NSMutableArray <OKAllAssetsCellModel *>*tokens;
@end

@implementation OKTokenSelectController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKTokenSelectController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"token.select".localized;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.mj_insetT = 8;
    NSArray *tokensArray = [self.data objectForKey:@"tokens"];
    OKAllAssetsCellModel *nativeToken = [OKAllAssetsCellModel mj_objectWithKeyValues:self.data];
    nativeToken.isNativeToken = YES;
    self.tokens = [@[nativeToken] mutableCopy];
    [self.tokens addObjectsFromArray:[OKAllAssetsCellModel mj_objectArrayWithKeyValuesArray:tokensArray]];
}

#pragma mark - UITableViewDataSource, UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.tokens.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 64;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.selectCallback) {
        self.selectCallback(self.tokens[indexPath.row]);
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *ID  = @"OKAllAssetsTableViewCell";
    OKAllAssetsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAllAssetsTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.separator.hidden = indexPath.row == [tableView numberOfRowsInSection:indexPath.section] - 1;
    cell.model = self.tokens[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
    [UITableView roundSectiontableView:tableView
                       willDisplayCell:cell
                     forRowAtIndexPath:indexPath
                          cornerRadius:12];
}

#pragma mark - Override BaseVC methods
- (UIColor *)navBarTintColor {
    return UIColor.BG_W02;
}

- (UIScrollView *)scrollViewForNavbar {
    return self.tableView;
}
@end
