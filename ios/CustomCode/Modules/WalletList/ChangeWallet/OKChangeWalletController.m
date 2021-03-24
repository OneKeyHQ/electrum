//
//  OKChangeWalletController.m
//  OneKey
//
//  Created by zj on 2021/3/10.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKChangeWalletController.h"
#import "OKChangeWalletCell.h"
#import "OKAllAssetsCellModel.h"

static const CGFloat ANIMATION_DURATION = 0.3;
static const CGFloat MASK_ALPHA = 0.4;

@interface OKChangeWalletController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UITableView *subTableView;
@property (weak, nonatomic) IBOutlet UIView *handleView;
@property (weak, nonatomic) IBOutlet UIView *panel;
@property (weak, nonatomic) IBOutlet UIView *mask;
@property (weak, nonatomic) IBOutlet UIView *handle;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *panelBottom;

@property (nonatomic, strong)NSArray <OKWalletInfoModel *>*wallets;
@property (nonatomic, strong)NSMutableArray <NSArray <OKWalletInfoModel *>*>*walletsList;
@property (nonatomic, strong)NSMutableArray <NSNumber *>*walletCoinTypes;
@property (nonatomic, assign)NSUInteger currentWalletIndex;
@property (nonatomic, assign)BOOL walletSelected;

@end

@implementation OKChangeWalletController
+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil] instantiateViewControllerWithIdentifier:@"OKChangeWalletController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.walletCoinTypes = [[NSMutableArray alloc] init];

    self.panelBottom.constant = - self.panel.height - 30;
    [self.handle setLayerRadius:2];

    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.subTableView.delegate = self;
    self.subTableView.dataSource = self;

    [self.handleView setLayerRadius:20];
    self.mask.alpha = 0;

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismiss)];
    [self.mask addGestureRecognizer:tap];
    UIPanGestureRecognizer *panGes = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
    UIPanGestureRecognizer *panGes2 = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
    [self.view addGestureRecognizer:panGes];
    [self.panel addGestureRecognizer:panGes2];

    self.wallets = self.walletsList.firstObject;
    self.currentWalletIndex = 0;

    [kPyCommandsManager asyncCall:kInterface_get_all_wallet_balance parameter:nil callback:^(id  _Nonnull result) {
        NSDictionary *dict = result;
        NSArray *wallet_info = dict[@"wallet_info"];
        NSArray *array = [OKAllAssetsSectionModel mj_objectArrayWithKeyValuesArray:wallet_info];
        for (OKAllAssetsSectionModel *assetModel in array) {
            for (OKWalletInfoModel *wallet in [self getWalletsFlatList]) {
                if ([wallet.name isEqualToString:assetModel.label]) {
                    NSString *balance = assetModel.wallets.firstObject.balance;
                    wallet.additionalData = @{@"balance": balance ?: @"?"};
                    break;
                }
            }
        }
        [self.tableView reloadData];
    }];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    self.panel.top = self.view.height;
    [self show];
}

- (NSMutableArray <NSArray <OKWalletInfoModel *>*>*)walletsList {
    if (!_walletsList) {

        _walletsList = [[NSMutableArray alloc] init];
        NSArray<OKWalletInfoModel *> *listWallets = [kWalletManager listWallets];
        OKChangeWalletChainType type = self.chianType;

        #define WALLET_ADD_TYPE(ARG_ChainType, ARG_WalletCoinType) \
        if (type & (ARG_ChainType)) {\
            NSArray *wallets = [listWallets ok_filter:^BOOL(id obj) {\
                return ((OKWalletInfoModel *)obj).walletCoinType == (ARG_WalletCoinType);\
            }];\
            [_walletsList addObject:wallets];\
            [self.walletCoinTypes addObject:@(ARG_WalletCoinType)];\
        }
        WALLET_ADD_TYPE(OKChangeWalletChainTypeBTC, OKWalletCoinTypeBTC)
        WALLET_ADD_TYPE(OKChangeWalletChainTypeETH, OKWalletCoinTypeETH)
        WALLET_ADD_TYPE(OKChangeWalletChainTypeBSC, OKWalletCoinTypeBSC)
        WALLET_ADD_TYPE(OKChangeWalletChainTypeHECO, OKWalletCoinTypeHECO)
    }
    return _walletsList;
}

- (NSArray <OKWalletInfoModel *>*)getWalletsFlatList {
    NSMutableArray *walletsFlatList = [[NSMutableArray alloc] init];
    for (NSArray *wallets in self.walletsList) {
        for (OKWalletInfoModel *wallet in wallets) {
            [walletsFlatList addObject:wallet];
        }
    }
    return walletsFlatList;
}

- (void)setCurrentWalletIndex:(NSUInteger)currentWalletIndex {
    _currentWalletIndex = currentWalletIndex;
    OKWalletCoinType coinType = [self.walletCoinTypes[currentWalletIndex] integerValue];
    NSString *walletName = @"BTC wallet";

    switch (coinType) {
        case OKWalletCoinTypeETH:  walletName = @"ETH wallet"; break;
        case OKWalletCoinTypeBSC:  walletName = @"BSC wallet"; break;
        case OKWalletCoinTypeHECO: walletName = @"HECO wallet"; break;
        default: break;
    }
    self.headerLabel.text = walletName.localized;
}

#pragma mark - UITableViewDelegate, UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return tableView == self.tableView ? self.wallets.count : self.walletsList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.tableView) {
        static NSString *ID = @"OKChangeWalletCell";
        OKChangeWalletCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        cell = cell ?: [[OKChangeWalletCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        cell.model = self.wallets[indexPath.row];
        return cell;
    } else {
        static NSString *ID = @"OKChangeWalletSubCell";
        OKChangeWalletSubCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        cell = cell ?: [[OKChangeWalletSubCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        cell.chosen = indexPath.row == self.currentWalletIndex;
        cell.type = [self.walletCoinTypes[indexPath.row] integerValue];
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (tableView == self.tableView) {
        OKWalletInfoModel *model = self.wallets[indexPath.row];
        [self changeWalletTo:model];
    } else {
        if (indexPath.row == self.currentWalletIndex) {
            return;
        }
        self.currentWalletIndex = indexPath.row;
        self.wallets = self.walletsList[indexPath.row];
        [self.tableView reloadData];
        [self.subTableView reloadData];
    }
}

- (void)changeWalletTo:(OKWalletInfoModel *)wallet {
    self.loadingIndicator.hidden = NO;
    [kWalletManager setCurrentWalletInfo:wallet];
    [kPyCommandsManager asyncCall:kInterface_switch_wallet
                        parameter:@{@"name":kWalletManager.currentWalletInfo.name}
                         callback:^(id  _Nonnull result) {
        if (!result) {
            return;
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:kNotiSelectWalletComplete object:nil];
        self.walletSelected = YES;
        self.loadingIndicator.hidden = YES;
        if (self.walletChangedCallback) {
            self.walletChangedCallback(wallet);
        }
        [self dismiss];
    }];
}

#pragma mark - Animation
- (void)show {
    [UIView animateWithDuration:ANIMATION_DURATION animations:^{
        self.mask.alpha = MASK_ALPHA;
        self.panel.top = self.view.height - self.panel.height;
    } completion:^(BOOL finished) {
        self.panelBottom.constant = 0;
    }];
}

- (void)move:(UIPanGestureRecognizer *)sender {
    CGFloat originY = self.view.height - self.panel.height * 0.5;
    CGPoint offset = [sender translationInView:self.panel];
    CGFloat deltaY = MAX(self.panel.centerY + offset.y, originY);

    self.panel.centerY = deltaY;
    [sender setTranslation:CGPointMake(0, 0) inView:self.view];

    self.mask.alpha = MASK_ALPHA * MAX(1 - ABS(deltaY - originY) / originY, 0);

    BOOL reachDismiss = self.panel.centerY > originY * 1.2;
    if (sender.state == UIGestureRecognizerStateEnded) {
        if (reachDismiss) {
            [self dismiss];
        } else {
            [UIView animateWithDuration:0.2 animations:^{
                self.panel.centerY = originY;
                self.mask.alpha = MASK_ALPHA;
            } completion:nil];
        }
    }
}

- (void)dismiss {
    [UIView animateWithDuration:ANIMATION_DURATION animations:^{
        self.mask.alpha = 0;
        self.panel.centerY = self.view.height + self.panel.height * 0.5 + 20;
    } completion:^(BOOL finished) {
        self.panelBottom.constant = - self.panel.height - 30;
        if (self.cancelCallback) {
            self.cancelCallback(self.walletSelected);
        }
        [self dismissViewControllerAnimated:NO completion:nil];
    }];
}
@end
