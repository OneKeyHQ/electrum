//
//  OKAboutViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKAboutViewController.h"
#import "OKAboutTableViewCellModel.h"
#import "OKAboutTableViewCell.h"

@interface OKAboutViewController ()<UITableViewDelegate,UITableViewDataSource>
@property (nonatomic,strong)NSArray *allData;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@end

@implementation OKAboutViewController
+ (instancetype)aboutViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Mine" bundle:nil] instantiateViewControllerWithIdentifier:@"OKAboutViewController"];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"about", nil);
    self.tableView.tableFooterView = [UIView new];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.allData count];
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKAboutTableViewCell";
    OKAboutTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKAboutTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.row];
    return  cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.row) {
        case 0:
        {
            [kTools tipMessage:@"点击了版本更新"];
        }
            break;
        case 1:
        {
            WebViewVC *vc = [WebViewVC loadWebViewControllerWithTitle:MyLocalizedString(@"用户协议", nil) url:kTheServiceAgreement];
            [self.navigationController pushViewController:vc animated:YES];
        }
            break;
        default:
            break;
    }
}


- (NSArray *)allData
{
    if (!_allData) {
        
        OKAboutTableViewCellModel *model1 = [[OKAboutTableViewCellModel alloc]init];
        model1.titleStr = MyLocalizedString(@"Version update", nil);
        model1.descStr = @"v1.0.0";
        
        OKAboutTableViewCellModel *model2 = [[OKAboutTableViewCellModel alloc]init];
        model2.titleStr = MyLocalizedString(@"User agreement", nil);
        model2.descStr = @"";
        
        _allData = @[model1,model2];
    }
    return _allData;
}
@end
