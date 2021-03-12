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

@property (nonatomic, strong)NSArray <OKWalletInfoModel *>*wallets;
@property (nonatomic, assign)BOOL walletSelected;

@end

@implementation OKChangeWalletController
+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"WalletList" bundle:nil] instantiateViewControllerWithIdentifier:@"OKChangeWalletController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];

    self.headerLabel.text = self.chianType == OKWalletChainTypeBTC ? @"BTC wallet".localized : @"ETH wallet".localized;
    [self.handle setLayerRadius:2];
    self.subTableView.mj_insetT = 8;

    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    self.subTableView.delegate = self;
    self.subTableView.dataSource = self;

    [self.handleView setLayerRadius:20];
    self.mask.alpha = 0;

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(dismiss)];
    [self.mask addGestureRecognizer:tap];
    UIPanGestureRecognizer *panGes = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
    UIPanGestureRecognizer *panGes2 = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(move:)];
    [self.view addGestureRecognizer:panGes];
    [self.panel addGestureRecognizer:panGes2];

    self.wallets = [kWalletManager listWalletsChainType:self.chianType];

    [kPyCommandsManager asyncCall:kInterface_get_all_wallet_balance parameter:@{} callback:^(id  _Nonnull result) {
        NSDictionary *dict = result;
        NSArray *wallet_info = dict[@"wallet_info"];
        NSArray *array = [OKAllAssetsSectionModel mj_objectArrayWithKeyValuesArray:wallet_info];
        for (OKAllAssetsSectionModel *assetModel in array) {
            for (OKWalletInfoModel *wallet in self.wallets) {
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

#pragma mark - UITableViewDelegate, UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return tableView == self.tableView ? self.wallets.count : 1;
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
        cell.type = self.chianType == OKWalletChainTypeBTC ? OKWalletCoinTypeBTC : OKWalletCoinTypeETH;
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (tableView == self.tableView) {
        OKWalletInfoModel *model = self.wallets[indexPath.row];
        self.walletSelected = YES;
        [self dismiss];
        if (self.walletChangedCallback) {
            self.walletChangedCallback(model);
        }
    }
}

#pragma mark - Animation
- (void)show {
    [UIView animateWithDuration:ANIMATION_DURATION animations:^{
        self.mask.alpha = MASK_ALPHA;
        self.panel.top = self.view.height - self.panel.height;
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
        if (self.cancelCallback) {
            self.cancelCallback(self.walletSelected);
        }
        [self dismissViewControllerAnimated:NO completion:nil];
    }];
}
@end
