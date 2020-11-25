//
//  OKFindFollowingWalletController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKFindFollowingWalletController.h"
#import "OKFindWalletTableViewCell.h"
#import "OKFindWalletTableViewCellModel.h"

@interface OKFindFollowingWalletController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIView *tableViewBgView;
@property (weak, nonatomic) IBOutlet UITableView *tbaleView;
@property (weak, nonatomic) IBOutlet UIView *headerView;
@property (weak, nonatomic) IBOutlet UIButton *restoreBtn;
- (IBAction)restoreBtnClick:(UIButton *)sender;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;

@property (nonatomic,strong)NSArray *walletList;

@end

@implementation OKFindFollowingWalletController

+ (instancetype)findFollowingWalletController
{
    return [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKFindFollowingWalletController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    //        [OKStorageManager saveToUserDefaults:@"BTC-1" key:kCurrentWalletName];
    //        //创建HD成功刷新首页的UI
    //        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletFirstCreateComplete object:@{@"pwd":pwd}];
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 10;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKFindWalletTableViewCell";
    OKFindWalletTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKFindWalletTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.walletList[indexPath.row];
    return  cell;
}



- (IBAction)restoreBtnClick:(UIButton *)sender {
    NSLog(@"restoreBtnClick");
}
@end
