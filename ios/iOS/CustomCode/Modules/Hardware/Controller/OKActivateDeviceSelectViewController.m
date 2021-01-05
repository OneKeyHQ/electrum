//
//  OKActivateDeviceSelectViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/10.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKActivateDeviceSelectViewController.h"
#import "OKCreateSelectWalletTypeCell.h"
#import "OKCreateSelectWalletTypeModel.h"
#import "OKPINCodeViewController.h"

@interface OKActivateDeviceSelectViewController ()
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (nonatomic,strong)NSArray *walletTypeListArray;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@end

@implementation OKActivateDeviceSelectViewController
+ (instancetype)activateDeviceSelectViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKActivateDeviceSelectViewController"];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"pairing", nil);
    self.titleLabel.text = MyLocalizedString(@"this is already activated device, you can...", nil);
    self.tableView.tableFooterView = [UIView new];
}

#pragma mark - UITableViewDelegate | UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.walletTypeListArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ID = @"OKCreateSelectWalletTypeCell";
    OKCreateSelectWalletTypeCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKCreateSelectWalletTypeCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.walletTypeListArray[indexPath.row];
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKCreateSelectWalletTypeModel *model = self.walletTypeListArray[indexPath.row];
   
    NSLog(@"didSelectRowAtIndexPath %@",model.tipsString);
    OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController];
    [self.navigationController pushViewController:pinCodeVc animated:YES];
}


- (NSArray *)walletTypeListArray
{
    if (!_walletTypeListArray) {
        
        OKCreateSelectWalletTypeModel *model = [OKCreateSelectWalletTypeModel new];
        model.createWalletType = MyLocalizedString(@"Add a new wallet (recommended)", nil);
        model.iconName = @"new_device";
        model.tipsString = MyLocalizedString(@"Up to 20 derived wallets can be created, as needed", nil);
        //model.addtype = OKAddTypeCreateHDDerived;
        
        OKCreateSelectWalletTypeModel *model1 = [OKCreateSelectWalletTypeModel new];
        model1.createWalletType = MyLocalizedString(@"Restore the wallet that was created from this hardware device", nil);
        model1.iconName = @"recover_device";
        model1.tipsString = MyLocalizedString(@"If you have ever deleted a wallet created by the device on your phone, you can recover it this way.", nil);
        //model1.addtype = OKAddTypeCreateSolo;
        
        OKCreateSelectWalletTypeModel *model2 = [OKCreateSelectWalletTypeModel new];
        model2.createWalletType = MyLocalizedString(@"Create a Condominium wallet (Advanced)", nil);
        model2.iconName = @"multi-sig";
        model2.tipsString = MyLocalizedString(@"At least 2 devices are required to cooperate. Before adding, make sure that all hardware devices that want to participate in the public pipe are activated", nil);
        
        _walletTypeListArray = @[model,model1,model2];
    }
    return _walletTypeListArray;
}

@end
