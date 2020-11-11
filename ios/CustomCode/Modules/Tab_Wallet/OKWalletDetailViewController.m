//
//  OKWalletDetailViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWalletDetailViewController.h"
#import "OKWalletDetailTableViewCell.h"
#import "OKWalletDetailModel.h"

@interface OKWalletDetailViewController ()<UITableViewDelegate,UITableViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *tableView;

@property (nonatomic,strong)NSArray *allData;

@property (nonatomic,strong)NSArray *groupNameArray;

@property (weak, nonatomic) IBOutlet UIImageView *iconCoinTypeImage;

@property (weak, nonatomic) IBOutlet UILabel *nameLabel;

@property (nonatomic,assign)OKWalletType walletType;

- (IBAction)editWalletNameClick:(UIButton *)sender;


@end

@implementation OKWalletDetailViewController

+ (instancetype)walletDetailViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKWalletDetailViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationBarBackgroundColorWithClearColor];
    [self stupUI];
    _walletType = [kWalletManager getWalletDetailType];
    self.tableView.tableFooterView = [UIView new];
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Wallet Detail", nil);
    self.nameLabel.text = kWalletManager.currentWalletName;
}

#pragma mark - TableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.allData.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return  [self.allData[section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    //TableView
    static NSString *ID = @"OKWalletDetailTableViewCell";
    OKWalletDetailTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[OKWalletDetailTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    cell.model = self.allData[indexPath.section][indexPath.row];
    return cell;
}
- (UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return  nil;
    }
    CGFloat H = 58;
    CGFloat labelH = 19;
    UIView *headerView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, H)];
    headerView.backgroundColor = HexColor(0xF5F6F7);
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(16,H - labelH - 5 ,SCREEN_WIDTH , labelH)];
    label.font = [UIFont systemFontOfSize:14];
    label.textColor = RGBA(84, 99, 112, 0.6);
    label.text = self.groupNameArray[section];
    [headerView addSubview:label];
    return headerView;
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return 0;
    }
    return 58;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKWalletDetailModel *model = self.allData[indexPath.section][indexPath.row];
    switch (model.clickType) {
        case OKClickTypeExportMnemonic:
        {
            [kTools tipMessage:@"点击了导出助记词"];
        }
            break;
        case OKClickTypeExportPrivatekey:
        {
            [kTools tipMessage:@"点击了导出私钥"];
        }
            break;
        case OKClickTypeExportKeystore:
        {
            [kTools tipMessage:@"点击了导出 keystore"];
        }
            break;
        case OKClickTypeDeleteWallet:
        {
            [kPyCommandsManager callInterface:kInterfaceDelete_wallet parameter:@{@"name":kWalletManager.currentWalletName}];
            [kWalletManager clearCurrentWalletInfo];
            [kTools tipMessage:@"删除钱包成功"];
            [[NSNotificationCenter defaultCenter]postNotificationName:kNotiDeleteWalletComplete object:nil];
            [self.navigationController popViewControllerAnimated:YES];
        }
            break;
        default:
            break;
    }
}
- (NSArray *)allData
{
    if (!_allData) {
        NSMutableArray *allDataM = [NSMutableArray array];
        OKWalletDetailModel *model1 = [OKWalletDetailModel new];
        model1.titleStr = MyLocalizedString(@"address", nil);
        model1.rightLabelStr = kWalletManager.currentWalletAddress;
        model1.isShowCopy = YES;
        model1.isShowSerialNumber = NO;
        model1.isShowArrow = NO;
        model1.isShowDesc = NO;
        model1.leftLabelColor = HexColor(0x14293B);
        model1.rightLabelColor = HexColor(0x9FA6AD);
        
        OKWalletDetailModel *model2 = [OKWalletDetailModel new];
        model2.titleStr = MyLocalizedString(@"type", nil);
        model2.rightLabelStr = [self getTypeStr:_walletType];
        model2.isShowCopy = NO;
        model2.isShowSerialNumber = NO;
        model2.isShowArrow = NO;
        model2.isShowDesc = NO;
        model2.leftLabelColor = HexColor(0x14293B);
        model2.rightLabelColor = HexColor(0x00B812);
        
        //第一组
        NSMutableArray *oneGroup = [NSMutableArray array];
        [oneGroup addObject:model1];
        [oneGroup addObject:model2];
        if (_walletType == OKWalletTypeMultipleSignature) {
            OKWalletDetailModel *model4 = [OKWalletDetailModel new];
            model4.titleStr = MyLocalizedString(@"Multiple signature", nil);
            model4.rightLabelStr = MyLocalizedString(@"3-5 (Number of initial signatures - total)", nil);
            model4.isShowCopy = NO;
            model4.isShowSerialNumber = NO;
            model4.isShowArrow = NO;
            model4.isShowDesc = NO;
            model4.leftLabelColor = HexColor(0x14293B);
            model4.rightLabelColor = HexColor(0x9FA6AD);
            [oneGroup addObject:model4];
        }
        
        if (_walletType == OKWalletTypeHardware || _walletType == OKWalletTypeHD){
            OKWalletDetailModel *model3 = [OKWalletDetailModel new];
            model3.titleStr = @"";
            model3.rightLabelStr = MyLocalizedString(@"The private key or mnemonic of the wallet is securely stored in the hardware device. If you need to export a mnemonic for a hardware wallet, go to Myhardware-All Devices to find the device you want to export.", nil);
            model3.isShowCopy = NO;
            model3.isShowSerialNumber = NO;
            model3.isShowArrow = NO;
            model3.isShowDesc = YES;
            model3.leftLabelColor = HexColor(0x14293B);
            model3.rightLabelColor = HexColor(0x00B812);
            [oneGroup addObject:model3];
        }
        
        [allDataM addObject:oneGroup];
        
        if (_walletType == OKWalletTypeIndependent) {
            NSMutableArray *securityGroup = [NSMutableArray array];
            OKWalletDetailModel *modelS1 = [OKWalletDetailModel new];
            modelS1.titleStr = MyLocalizedString(@"Export mnemonic", nil);
            modelS1.rightLabelStr = @"";
            modelS1.isShowCopy = NO;
            modelS1.isShowSerialNumber = NO;
            modelS1.isShowArrow = YES;
            modelS1.isShowDesc = NO;
            modelS1.clickType = OKClickTypeExportMnemonic;
            modelS1.leftLabelColor = HexColor(0x14293B);
            modelS1.rightLabelColor = HexColor(0x9FA6AD);
            
            OKWalletDetailModel *modelS2 = [OKWalletDetailModel new];
            modelS2.titleStr = MyLocalizedString(@"Export the private key", nil);
            modelS2.rightLabelStr = @"";
            modelS2.isShowCopy = NO;
            modelS2.isShowSerialNumber = NO;
            modelS2.isShowArrow = YES;
            modelS2.isShowDesc = NO;
            modelS2.clickType = OKClickTypeExportPrivatekey;
            modelS2.leftLabelColor = HexColor(0x14293B);
            modelS2.rightLabelColor = HexColor(0x00B812);
            
            OKWalletDetailModel *modelS3 = [OKWalletDetailModel new];
            modelS3.titleStr = MyLocalizedString(@"Export the Keystore", nil);
            modelS3.rightLabelStr = @"";
            modelS3.isShowCopy = NO;
            modelS3.isShowSerialNumber = NO;
            modelS3.isShowArrow = YES;
            modelS3.isShowDesc = NO;
            modelS3.clickType = OKClickTypeExportKeystore;
            modelS3.leftLabelColor = HexColor(0x14293B);
            modelS3.rightLabelColor = HexColor(0x00B812);
            [securityGroup addObject:modelS1];
            [securityGroup addObject:modelS2];
            if ([kWalletManager.currentSelectCoinType isEqualToString:COIN_ETH]) {
                [securityGroup addObject:modelS3];
            }
            [allDataM addObject:securityGroup];
        }
        
        if (_walletType == OKWalletTypeIndependent || _walletType == OKWalletTypeHardware || _walletType == OKWalletTypeMultipleSignature) {
            NSMutableArray *twoGroup = [NSMutableArray array];
            OKWalletDetailModel *modelDel = [OKWalletDetailModel new];
            modelDel.titleStr = MyLocalizedString(@"To delete the wallet", nil);
            modelDel.rightLabelStr = @"";
            modelDel.isShowCopy = NO;
            modelDel.isShowSerialNumber = NO;
            modelDel.isShowArrow = YES;
            modelDel.isShowDesc = NO;
            modelDel.clickType = OKClickTypeDeleteWallet;
            modelDel.leftLabelColor = HexColor(0xEB5757);
            modelDel.rightLabelColor = HexColor(0x9FA6AD);
            [twoGroup addObject:modelDel];
            [allDataM addObject:twoGroup];
        }
        
        _allData = allDataM;
    }
    return _allData;
}
- (NSString *)getTypeStr:(OKWalletType)type
{
    switch (type) {
        case OKWalletTypeHD:
            return MyLocalizedString(@"HD wallet", nil);
            break;
        case OKWalletTypeIndependent:
            return MyLocalizedString(@"Independent wallet", nil);
            break;
        case OKWalletTypeHardware:
            return MyLocalizedString(@"Hardware wallet", nil);
            break;
        case OKWalletTypeMultipleSignature:
            return MyLocalizedString(@"Hardware wallet", nil);
            break;
        default:
            break;
    }
}
- (IBAction)editWalletNameClick:(UIButton *)sender {
    NSLog(@"点击了编辑钱包名称");
}

- (NSArray *)groupNameArray
{
    if (!_groupNameArray) {
        switch (_walletType) {
            case OKWalletTypeHD:
                _groupNameArray = @[];
                break;
            case OKWalletTypeIndependent:
                _groupNameArray = @[@"",MyLocalizedString(@"security", nil),MyLocalizedString(@"Dangerous operation", nil)];
                break;
            case OKWalletTypeHardware:
                _groupNameArray = @[@"",MyLocalizedString(@"Dangerous operation", nil)];
                break;
            case OKWalletTypeMultipleSignature:
                _groupNameArray = @[@"",MyLocalizedString(@"Dangerous operation", nil)];
                break;
                
            default:
                break;
        };
    }
    return _groupNameArray;
}
@end
