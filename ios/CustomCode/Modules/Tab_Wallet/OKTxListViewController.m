//
//  OKTxListViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/14.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKTxListViewController.h"
#import "MLMSegmentManager.h"
#import "OKTxViewController.h"
#import "OKSendCoinViewController.h"
#import "OKReceiveCoinViewController.h"
#import "OKAssetTableViewCellModel.h"

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

@end

@implementation OKTxListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(refreshUI:) name:kNotiUpdate_status object:nil];
}

- (void)stupUI
{
    [self setNavigationBarBackgroundColorWithClearColor];
    self.title = @"BTC";
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemWithImage:[UIImage imageNamed:@"token_btc"] frame:CGRectMake(0, 0, 30, 30) target:self selector:@selector(rightBarButtonItemClick)];
    // Do any additional setup after loading the view.
    [self segmentStyle];
    [self.sendCoinBtn setLayerRadius:15];
    [self.reciveCoinBtn setLayerRadius:15];
}

- (void)refreshUI:(NSNotification *)noti
{
    NSDictionary *dict = noti.object;
    [self Setupthedata:dict];
}

- (void)Setupthedata:(NSDictionary *)dict
{
    dispatch_async(dispatch_get_main_queue(), ^{
       // UI更新代码
        self.textBalanceLabel.text =  [NSString stringWithFormat:@"%@ %@",[dict safeStringForKey:@"balance"],kWalletManager.currentBitcoinUnit];
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
    [arr addObject:txAllVc];
    
    OKTxViewController *txInVc = [OKTxViewController txViewController];
    txInVc.searchType = @"receive";
    [arr addObject:txInVc];
    
    OKTxViewController *txOutVc = [OKTxViewController txViewController];
    txOutVc.searchType = @"send";
    [arr addObject:txOutVc];
    
    return arr;
}
#pragma mark - 均分下划线
- (void)segmentStyle{
    self.list = @[MyLocalizedString(@"Tx All", nil),
                  MyLocalizedString(@"Tx In", nil),
                   MyLocalizedString(@"Tx Out", nil)
                  ];
    CGFloat margin = 3;
    _segHead = [[MLMSegmentHead alloc] initWithFrame:CGRectMake(margin, 250, SCREEN_WIDTH - margin * 2, (36)) titles:self.list headStyle:SegmentHeadStyleSlide layoutStyle:MLMSegmentLayoutDefault];
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
    //_segHead.maxTitles = 5;
    CGFloat marginTableTop = 10;
    _segScroll = [[MLMSegmentScroll alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(_segHead.frame) + marginTableTop, SCREEN_WIDTH,CGRectGetMinY(self.bottomBgView.frame)- CGRectGetMaxY(_segHead.frame) - marginTableTop) vcOrViews:[self vcArr:self.list.count]];
    _segScroll.loadAll = YES;
    _segScroll.showIndex = 0;
    
    @weakify(self)
    [MLMSegmentManager associateHead:_segHead withScroll:_segScroll completion:^{
        @strongify(self)
        [self.view addSubview:self.segHead];
        [self.view addSubview:self.segScroll];
    } selectEnd:^(NSInteger index) {
        if (index == 2) {
            
        }else{
            
        }
    }];
}
- (IBAction)sendCoinBtnClick:(UIButton *)sender {
    
    OKSendCoinViewController *sendCoinVc = [OKSendCoinViewController sendCoinViewController];
    [self.navigationController pushViewController:sendCoinVc animated:YES];
}
- (IBAction)reciveCoinBtnClick:(UIButton *)sender {
    
    OKReceiveCoinViewController *receiveCoinVc = [OKReceiveCoinViewController receiveCoinViewController];
    [self.navigationController pushViewController:receiveCoinVc animated:YES];
}


- (void)dealloc
{
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
@end
