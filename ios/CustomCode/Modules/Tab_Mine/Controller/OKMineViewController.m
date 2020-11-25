//
//  OKMineViewController.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKMineViewController.h"
#import "OKMineTableViewCell.h"
#import "OKMineTableViewCellModel.h"
#import "OKLanguageViewController.h"
#import "OKMonetaryUnitViewController.h"
#import "OKNetworkViewController.h"
#import "OKTradeSettingViewController.h"
#import "OKAboutViewController.h"
#import "OKAllAssetsViewController.h"
#import "OKHDWalletViewController.h"
#import "OKChangePwdViewController.h"
#import "YZAuthID.h"
#import "OKBluetoothViewController.h"

@interface OKMineViewController ()<UINavigationControllerDelegate,UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *allMenuData;

@property (weak, nonatomic) IBOutlet UILabel *bottomtipsLabel;

@end

@implementation OKMineViewController
+ (instancetype)mineViewController
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:@"OKMineViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"my", nil);
    [self setNavigationBarBackgroundColorWithClearColor];
    NSMutableAttributedString *attri = [[NSMutableAttributedString alloc] initWithString:MyLocalizedString(@"Privacy policies, usage protocols, and open source software", nil)];
    NSTextAttachment *attch = [[NSTextAttachment alloc] init];
    attch.image = [UIImage imageNamed:@"bottom_link"];
    attch.bounds = CGRectMake(0, 0, 16, 14);
    NSAttributedString *string = [NSAttributedString attributedStringWithAttachment:attch];
    [attri appendAttributedString:string];
    self.bottomtipsLabel.attributedText = attri;
    self.bottomtipsLabel.alpha = 0.6;
}

#pragma mark - TableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.allMenuData.count;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.allMenuData[section]count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKMineTableViewCell";
    OKMineTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKMineTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allMenuData[indexPath.section][indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 72;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 65)];
    view.backgroundColor = HexColor(0xF5F6F7);
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(20, 35, 200, 22)];
    switch (section) {
        case 0:
            label.text = MyLocalizedString(@"assets", nil);
            break;
//        case 1:
//            label.text = MyLocalizedString(@"hardware", nil);
//            break;
        case 1:
            label.text = MyLocalizedString(@"security", nil);
            break;
        case 2:
            label.text = MyLocalizedString(@"System Settings", nil);
            break;
        default:
            break;
    }
    label.textColor = HexColor(0x546370);
    label.font = [UIFont systemFontOfSize:14];
    [view addSubview:label];
    return view;
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 65;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.section) {
        case 0: //资产
        {
            switch (indexPath.row) {
                case 0: //全部资产
                {
                    OKAllAssetsViewController *allAssetsVc = [OKAllAssetsViewController allAssetsViewController];
                    [self.navigationController pushViewController:allAssetsVc animated:YES];
                }
                    break;
                case 1: //HD钱包
                {
                    OKHDWalletViewController *hdWalletVc = [OKHDWalletViewController hdWalletViewController];
                    [self.navigationController pushViewController:hdWalletVc animated:YES];
                }
                    break;
                default:
                    break;
            }
        }
            break;
        case 1: //安全
        {
            switch (indexPath.row) {
                case 0: //密码
                {
                    OKChangePwdViewController *changePwd = [OKChangePwdViewController changePwdViewController];
                    [self.navigationController pushViewController:changePwd animated:YES];
                }
                    break;
                case 1: //auth识别
                {
                    
                }
                    break;
                default:
                    break;
            }
        }
            break;
        case 2:
        {
            switch (indexPath.row) {
                case 0:
                {
                    OKLanguageViewController *lanVc = [OKLanguageViewController languageViewController];
                    [self.navigationController pushViewController:lanVc animated:YES];
                }
                    break;
                case 1:
                {
                    OKMonetaryUnitViewController *monVc = [OKMonetaryUnitViewController monetaryUnitViewController];
                    [self.navigationController pushViewController:monVc animated:YES];
                }
                    break;
                case 2:
                {
                    OKNetworkViewController *netVc = [OKNetworkViewController networkViewController];
                    [self.navigationController pushViewController:netVc animated:YES];
                }
                    break;
                case 3:
                {
                    OKTradeSettingViewController *tradeVc = [OKTradeSettingViewController tradeSettingViewController];
                    [self.navigationController pushViewController:tradeVc animated:YES];
                }
                    break;
                case 4:
                {
                    OKAboutViewController *aboutVc = [OKAboutViewController aboutViewController];
                    [self.navigationController pushViewController:aboutVc animated:YES];
                }
                    break;
                case 5:
                {
                    OKBluetoothViewController *bluetoothVc = [OKBluetoothViewController bluetoothViewController];
                    [self.navigationController pushViewController:bluetoothVc animated:YES];
                }
                    break;
                default:
                    break;
            }
        }
            break;
        default:
            break;
    }
}



- (NSArray *)allMenuData
{
    if (!_allMenuData) {
        OKMineTableViewCellModel *model1 = [OKMineTableViewCellModel new];
        model1.menuName = MyLocalizedString(@"All assets", nil);
        model1.imageName = @"money";
        model1.isAuth = NO;
        
        OKMineTableViewCellModel *model2 = [OKMineTableViewCellModel new];
        model2.menuName = MyLocalizedString(@"HD wallet", nil);
        model2.imageName = @"hd_wallet";
        model2.isAuth = NO;
        
        OKMineTableViewCellModel *model3 = [OKMineTableViewCellModel new];
        model3.menuName = MyLocalizedString(@"All the equipment", nil);
        model3.imageName = @"device_link";
        model3.isAuth = NO;
        
        OKMineTableViewCellModel *model4 = [OKMineTableViewCellModel new];
        model4.menuName = MyLocalizedString(@"The connection method", nil);
        model4.imageName = @"link";
        model4.isAuth = NO;
        
        OKMineTableViewCellModel *model5 = [OKMineTableViewCellModel new];
        model5.menuName = MyLocalizedString(@"password", nil);
        model5.imageName = @"lockpwd";
        model5.isAuth = NO;
        
        
        OKMineTableViewCellModel *model6 = [OKMineTableViewCellModel new];
        model6.menuName = MyLocalizedString(@"Facial recognition", nil);
        model6.imageName = @"faceid";
        model6.isAuth = YES;
        
        OKMineTableViewCellModel *model7 = [OKMineTableViewCellModel new];
        model7.menuName = MyLocalizedString(@"Fingerprint identification", nil);
        model7.imageName = @"zhiwen";
        model7.isAuth = YES;
        
        OKMineTableViewCellModel *model8 = [OKMineTableViewCellModel new];
        model8.menuName = MyLocalizedString(@"language", nil);
        model8.imageName = @"translation 2";
        model8.isAuth = NO;
        
        OKMineTableViewCellModel *model9 = [OKMineTableViewCellModel new];
        model9.menuName = MyLocalizedString(@"Monetary unit", nil);
        model9.imageName = @"currency-dollar 2";
        model9.isAuth = NO;
        
        OKMineTableViewCellModel *model10 = [OKMineTableViewCellModel new];
        model10.menuName = MyLocalizedString(@"network", nil);
        model10.imageName = @"hotspot 1";
        model10.isAuth = NO;
        
        OKMineTableViewCellModel *model11 = [OKMineTableViewCellModel new];
        model11.menuName = MyLocalizedString(@"Transaction Settings (Advanced)", nil);
        model11.imageName = @"bold-direction 1";
        model11.isAuth = NO;
        
        OKMineTableViewCellModel *model12 = [OKMineTableViewCellModel new];
        model12.menuName = MyLocalizedString(@"about", nil);
        model12.imageName = @"c-question 4";
        model12.isAuth = NO;
        
        __block NSArray *biologicaArray = [NSArray array];
        [YZAuthID biologicalRecognitionResult:^(YZAuthenticationType type) {
            switch (type) {
                case YZAuthenticationFace:
                {
                    biologicaArray = @[model5,model6];
                }
                        break;
                case YZAuthenticationTouch:
                {
                    biologicaArray = @[model5,model7];
                }
                        break;
                default:
                {
                    biologicaArray = @[model5];
                }
                        break;
            }
        }];
        _allMenuData = @[@[model1,model2],biologicaArray,@[model8,model9,model10,model11,model12,model3]];
    }
    return _allMenuData;
}
#pragma mark - UINavigationControllerDelegate
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowMine = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowMine animated:YES];
}
@end
