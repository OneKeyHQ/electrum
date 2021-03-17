//
//  OKTxListViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/14.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKTxListViewController.h"
#import "MLMSegmentManager.h"
#import "OKTxViewController.h"
#import "OKSendCoinViewController.h"
#import "OKReceiveCoinViewController.h"
#import "OKAssetTableViewCellModel.h"
#import "OKMatchingInCirclesViewController.h"

#define kBottomBgViewH 100.0

@interface OKTxListViewController ()

@property (nonatomic, strong) NSArray *list;
@property (nonatomic, strong) MLMSegmentHead *segHead;
@property (nonatomic, strong) MLMSegmentScroll *segScroll;
@property (weak, nonatomic) IBOutlet UIView *bottomBgView;
@property (weak, nonatomic) IBOutlet UIButton *sendCoinBtn;
- (IBAction)sendCoinBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UIButton *reciveCoinBtn;
- (IBAction)reciveCoinBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *textBalanceLabel;
@property (weak, nonatomic) IBOutlet UILabel *textMoneyLabel;
@property (weak, nonatomic) IBOutlet UIView *topBgView;
@property (nonatomic,assign)NSInteger count;
@property (nonatomic,strong)OKButton *rightBtn;
@end

@implementation OKTxListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _count = 0;
    [self stupUI];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshUI:) name:kNotiUpdate_status object:nil];
}

- (void)stupUI
{
    [self setNavigationBarBackgroundColorWithClearColor];
    self.title = [self.model.coinType uppercaseString];

    _rightBtn = [OKButton buttonWithType:UIButtonTypeCustom];
    _rightBtn.frame = CGRectMake(0, 0, 44, 44);
    _rightBtn.layer.cornerRadius = 44 * 0.5;
    _rightBtn.layer.masksToBounds = YES;
    [_rightBtn addTarget:self action:@selector(handleRightBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [_rightBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.height.equalTo(@44);
    }];

    UIBarButtonItem *rightItem = [[UIBarButtonItem alloc] initWithCustomView:self.rightBtn];
    self.navigationItem.rightBarButtonItem = rightItem;
    NSArray *tokens = [[OKTokenManager sharedInstance]tokensFilterWith:(self.model.contract_addr ?:@"")];
    NSString *imageName = [NSString stringWithFormat:@"token_%@",[self.coinType lowercaseString]];
    if (tokens.count == 0) {
        [self.rightBtn setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    }else{
        OKToken *t = [tokens firstObject];
        NSString *iconName = t.logoURI.length ?t.logoURI:imageName;
        [self.rightBtn sd_setImageWithURL:[NSURL URLWithString:iconName]
                               forState:UIControlStateNormal
                       placeholderImage:[UIImage imageNamed:imageName]];;
    }

    [self.sendCoinBtn setLayerRadius:15];
    [self.reciveCoinBtn setLayerRadius:15];
}

- (void)handleRightBtnClicked:(OKButton*)btn
{
    NSLog(@"handleRightBtnClicked");
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    if (_count == 0) {
        [self segmentStyle];
        _count ++;
    }
}

- (void)refreshUI:(NSNotification *)noti
{
    NSDictionary *dict = noti.object;
    [self Setupthedata:dict];
}

- (void)Setupthedata:(NSDictionary *)dict
{
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        NSString *coinType = [weakself.coinType uppercaseString];
        if (weakself.tokenType.length > 0) {
            coinType = [weakself.coinType uppercaseString];
        }
        self.textBalanceLabel.text =  [NSString stringWithFormat:@"%@ %@",[dict safeStringForKey:@"balance"],coinType];
        self.textMoneyLabel.text = [NSString stringWithFormat:@"≈%@",[dict safeStringForKey:@"fiat"]];
    });
}

- (void)setModel:(OKAssetTableViewCellModel *)model
{
    _model = model;
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:model.balance forKey:@"balance"];
    [dict setValue:model.money forKey:@"fiat"];
    [self Setupthedata:dict];
}

#pragma mark - 右侧按钮
- (void)rightBarButtonItemClick
{
    NSLog(@"rightBarButtonItemClick");
}
#pragma mark - 数据源
- (NSArray *)vcArr:(NSInteger)count {
    NSMutableArray *arr = [NSMutableArray array];
    OKTxViewController *txAllVc = [OKTxViewController txViewController];
    txAllVc.searchType = @"";
    txAllVc.assetTableViewCellModel = self.model;
    txAllVc.coinType = self.coinType;
    [arr addObject:txAllVc];

    OKTxViewController *txInVc = [OKTxViewController txViewController];
    txInVc.searchType = @"receive";
    txInVc.assetTableViewCellModel = self.model;
    txInVc.coinType = self.coinType;
    [arr addObject:txInVc];

    OKTxViewController *txOutVc = [OKTxViewController txViewController];
    txOutVc.searchType = @"send";
    txOutVc.assetTableViewCellModel = self.model;
    txOutVc.coinType = self.coinType;
    [arr addObject:txOutVc];

    return arr;
}
#pragma mark - 均分下划线
- (void)segmentStyle{
    self.list = @[MyLocalizedString(@"Tx All", nil),
                  MyLocalizedString(@"Tx In", nil),
                   MyLocalizedString(@"Tx Out", nil)
                  ];
    CGFloat headerH = 36;
    _segHead = [[MLMSegmentHead alloc] initWithFrame:CGRectMake(0,0, self.topBgView.width, (headerH)) titles:self.list headStyle:SegmentHeadStyleSlide layoutStyle:MLMSegmentLayoutDefault];
    _segHead.slideCorner = 7;

//    _segHead.fontScale = 1.05;
    _segHead.fontSize = (15);
    /**
     *  导航条的背景颜色
     */
    _segHead.headColor = RGBA(118, 118, 118, 0.12);

    /*------------滑块风格------------*/
    /**
     *  滑块的颜色
     */
    _segHead.slideColor = [UIColor whiteColor];

    /*------------下划线风格------------*/
    /**
     *  下划线的颜色
     */
//    _segHead.lineColor = [UIColor redColor];
    /**
     *  选中颜色
     */
    _segHead.selectColor = [UIColor blackColor];
    /**
     *  未选中颜色
     */
    _segHead.deSelectColor = [UIColor blackColor];
    /**
     *  下划线高度
     */
//    _segHead.lineHeight = 2;
    /**
     *  下划线相对于正常状态下的百分比，默认为1
     */
//    _segHead.lineScale = 0.8;

    /**
     *  顶部导航栏下方的边线
     */
    _segHead.bottomLineHeight = 0;
    _segHead.bottomLineColor = [UIColor lightGrayColor];

    _segHead.slideScale = 0.98;
    /**
     *  设置当前屏幕最多显示的按钮数,只有在默认布局样式 - MLMSegmentLayoutDefault 下使用
     */
    _segScroll = [[MLMSegmentScroll alloc] initWithFrame:CGRectMake(0, headerH, self.topBgView.width,self.topBgView.height - headerH) vcOrViews:[self vcArr:self.list.count]];
    _segScroll.loadAll = YES;
    _segScroll.showIndex = 0;
    @weakify(self)
    [MLMSegmentManager associateHead:_segHead withScroll:_segScroll completion:^{
        @strongify(self)
        [self.topBgView addSubview:self.segHead];
        [self.topBgView addSubview:self.segScroll];
    } selectEnd:^(NSInteger index) {
        if (index == 2) {

        }else{

        }
    }];
}
- (IBAction)sendCoinBtnClick:(UIButton *)sender {
    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
        matchingVc.type = OKMatchingTypeTransfer;
        matchingVc.where = OKMatchingFromWhereNav;
        matchingVc.tokenCoinType = self.tokenType?:@"";
        [self.navigationController pushViewController:matchingVc animated:YES];
    }else{
        OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
        sendCoinVc.coinType = kWalletManager.currentWalletInfo.coinType;
        sendCoinVc.tokenCoinType = self.tokenType?:@"";
        [self.navigationController pushViewController:sendCoinVc animated:YES];
    }
}
- (IBAction)reciveCoinBtnClick:(UIButton *)sender {
    if ([kWalletManager getWalletDetailType] == OKWalletTypeHardware) {
        OKMatchingInCirclesViewController *matchingVc = [OKMatchingInCirclesViewController matchingInCirclesViewController];
        matchingVc.type = OKMatchingTypeReceiveCoin;
        matchingVc.where = OKMatchingFromWhereNav;
        matchingVc.tokenCoinType = self.tokenType?:@"";
        [self.navigationController pushViewController:matchingVc animated:YES];
    }else{
        OKReceiveCoinViewController *receiveCoinVc = [OKReceiveCoinViewController receiveCoinViewController];
        receiveCoinVc.coinType = kWalletManager.currentWalletInfo.coinType;
        receiveCoinVc.walletType = [kWalletManager getWalletDetailType];
        receiveCoinVc.tokenCoinType = self.tokenType?:@"";
        [self.navigationController pushViewController:receiveCoinVc animated:YES];
    }
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
    _count = 0;
}
@end
