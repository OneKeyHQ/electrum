//
//  OKMineViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKMineViewController.h"
#import "OKMineTableViewCell.h"
#import "OKMineTableViewCellModel.h"

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
        case 1:
            label.text = MyLocalizedString(@"hardware", nil);
            break;
        case 2:
            label.text = MyLocalizedString(@"security", nil);
            break;
        case 3:
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

- (NSArray *)allMenuData
{
    if (!_allMenuData) {
        OKMineTableViewCellModel *model1 = [OKMineTableViewCellModel new];
        model1.menuName = MyLocalizedString(@"All assets", nil);
        model1.imageName = @"money";
        
        OKMineTableViewCellModel *model2 = [OKMineTableViewCellModel new];
        model2.menuName = MyLocalizedString(@"HD wallet", nil);
        model2.imageName = @"hd_wallet";
        
        OKMineTableViewCellModel *model3 = [OKMineTableViewCellModel new];
        model3.menuName = MyLocalizedString(@"All the equipment", nil);
        model3.imageName = @"device_link";
        
        OKMineTableViewCellModel *model4 = [OKMineTableViewCellModel new];
        model4.menuName = MyLocalizedString(@"The connection method", nil);
        model4.imageName = @"link";
        
        OKMineTableViewCellModel *model5 = [OKMineTableViewCellModel new];
        model5.menuName = MyLocalizedString(@"password", nil);
        model5.imageName = @"lockpwd";
        
        
        OKMineTableViewCellModel *model6 = [OKMineTableViewCellModel new];
        model6.menuName = MyLocalizedString(@"Facial recognition", nil);
        model6.imageName = @"faceid";
        
        OKMineTableViewCellModel *model7 = [OKMineTableViewCellModel new];
        model7.menuName = MyLocalizedString(@"Fingerprint identification", nil);
        model7.imageName = @"zhiwen";
        
        
        OKMineTableViewCellModel *model8 = [OKMineTableViewCellModel new];
        model8.menuName = MyLocalizedString(@"language", nil);
        model8.imageName = @"translation 2";
        
        OKMineTableViewCellModel *model9 = [OKMineTableViewCellModel new];
        model9.menuName = MyLocalizedString(@"Monetary unit", nil);
        model9.imageName = @"currency-dollar 2";
        
        OKMineTableViewCellModel *model10 = [OKMineTableViewCellModel new];
        model10.menuName = MyLocalizedString(@"network", nil);
        model10.imageName = @"hotspot 1";
        
        
        OKMineTableViewCellModel *model11 = [OKMineTableViewCellModel new];
        model11.menuName = MyLocalizedString(@"Transaction Settings (Advanced)", nil);
        model11.imageName = @"bold-direction 1";
        
        OKMineTableViewCellModel *model12 = [OKMineTableViewCellModel new];
        model12.menuName = MyLocalizedString(@"about", nil);
        model12.imageName = @"c-question 4";
        
        
        _allMenuData = @[@[model1,model2],@[model3,model4],@[model5,model6,model7],@[model8,model9,model10,model11,model12]];
    }
    return _allMenuData;
}
#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowMine = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowMine animated:YES];
}
@end
