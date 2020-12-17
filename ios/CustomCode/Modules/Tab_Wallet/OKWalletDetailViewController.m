//
//  OKWalletDetailViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKWalletDetailViewController.h"
#import "OKWalletDetailTableViewCell.h"
#import "OKWalletDetailModel.h"
#import "OKEditWalletNameViewController.h"
#import "OKDeleteWalletConfirmController.h"
#import "OKExportTipsViewController.h"
#import "OKDontScreenshotTipsViewController.h"
#import "OKBackUpViewController.h"
#import "OKPrivateKeyExportViewController.h"
#import "OKDeleteBackUpTipsController.h"
#import "OKDeleteWalletTipsViewController.h"

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
    [self loadUI];
    BOOL is_watch_only =  [[kPyCommandsManager callInterface:kInterfaceis_watch_only parameter:@{}] boolValue];
    if (is_watch_only) {
        _walletType = OKWalletTypeObserve;
    }else{
        _walletType = [kWalletManager getWalletDetailType];
    }
    self.tableView.tableFooterView = [UIView new];
}

- (void)loadUI
{
    self.title = MyLocalizedString(@"Wallet Detail", nil);
    self.nameLabel.text = kWalletManager.currentWalletInfo.label;
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
    OKWeakSelf(self)
    switch (model.clickType) {
        case OKClickTypeExportMnemonic:
        {
            OKExportTipsViewController *exportTipsVc = [OKExportTipsViewController exportTipsViewController:^{
                if (kWalletManager.isOpenAuthBiological) {
                    [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                        if (state == YZAuthIDStateNotSupport
                            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                            [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                [weakself importSeedPwd:pwd];
                            }];
                        } else if (state == YZAuthIDStateSuccess) {
                            NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                            [weakself importSeedPwd:pwd];;
                        }
                    }];
                }else{
                    [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                        [weakself importSeedPwd:pwd];
                    }];
                }
            }];
            exportTipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [self.OK_TopViewController presentViewController:exportTipsVc animated:NO completion:nil];
        }
            break;
        case OKClickTypeExportPrivatekey:
        {
            OKExportTipsViewController *exportTipsVc = [OKExportTipsViewController exportTipsViewController:^{
                if (kWalletManager.isOpenAuthBiological) {
                    [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                        if (state == YZAuthIDStateNotSupport
                            || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                            [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                [weakself exportPrivatePwd:pwd];
                            }];
                        } else if (state == YZAuthIDStateSuccess) {
                            NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                            [weakself exportPrivatePwd:pwd];
                        }
                    }];
                }else{
                    [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                        [weakself exportPrivatePwd:pwd];
                    }];
                }
            }];
            exportTipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [self.OK_TopViewController presentViewController:exportTipsVc animated:NO completion:nil];
        }
            break;
        case OKClickTypeExportKeystore:
        {
            OKExportTipsViewController *exportTipsVc = [OKExportTipsViewController exportTipsViewController:^{
                
            }];
            exportTipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
            [self.OK_TopViewController presentViewController:exportTipsVc animated:NO completion:nil];
        }
            break;
        case OKClickTypeDeleteWallet:
        {
            OKWeakSelf(self)
            BOOL isBackUp = [[kPyCommandsManager callInterface:kInterfaceget_backup_info parameter:@{@"name":kWalletManager.currentWalletInfo.name}] boolValue];
            if (isBackUp) {
                if (self.walletType == OKWalletTypeIndependent) {
                    OKDeleteWalletTipsViewController *deleteVc = [OKDeleteWalletTipsViewController deleteWalletTipsViewController];
                    deleteVc.deleteType = OKWhereToDeleteTypeDetail;
                    deleteVc.walletName = kWalletManager.currentWalletInfo.name;
                    [weakself.navigationController pushViewController:deleteVc animated:YES];
                    
                }else{
                    OKDeleteWalletConfirmController *deleteVc = [OKDeleteWalletConfirmController deleteWalletConfirmController:^{
                        if (weakself.walletType == OKWalletTypeObserve) {
                            [weakself deleteWalletPwd:@""];
                        }else{
                            if (kWalletManager.isOpenAuthBiological) {
                                [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                                    if (state == YZAuthIDStateNotSupport
                                        || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                                        [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                            [weakself deleteWalletPwd:pwd];
                                        }];
                                    } else if (state == YZAuthIDStateSuccess) {
                                        NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                                        [weakself deleteWalletPwd:pwd];;
                                    }
                                }];
                            }else{
                                [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                                    [weakself deleteWalletPwd:pwd];
                                }];
                            }
                        }
                    } type:OKDeleteTipsTypeWallet];
                    deleteVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
                    [self.OK_TopViewController presentViewController:deleteVc animated:NO completion:nil];
                }
            }else{
                OKDeleteBackUpTipsController *tipsVc = [OKDeleteBackUpTipsController deleteBackUpTipsController:^{
                    
                }];
                tipsVc.modalPresentationStyle = UIModalPresentationOverFullScreen;
                [self presentViewController:tipsVc animated:NO completion:nil];
            }
        }
            break;
        default:
            break;
    }
}

- (void)importSeedPwd:(NSString *)pwd
{
    OKWeakSelf(self)
    NSString *result = [kPyCommandsManager callInterface:kInterfaceexport_seed parameter:@{@"password":pwd,@"name":kWalletManager.currentWalletInfo.name}];
    if (result != nil) {
        OKBackUpViewController *backUpVc = [OKBackUpViewController backUpViewController];
        backUpVc.words = [result componentsSeparatedByString:@" "];
        backUpVc.showType = WordsShowTypeExport;
        [weakself.OK_TopViewController.navigationController pushViewController:backUpVc animated:YES];
    }
}

- (void)exportPrivatePwd:(NSString *)pwd
{
    OKWeakSelf(self)
    NSString *result =  [kPyCommandsManager callInterface:kInterfaceexport_privkey parameter:@{@"password":pwd}];
     if (result != nil) {
         OKPrivateKeyExportViewController *privateKeyExportVc = [OKPrivateKeyExportViewController privateKeyExportViewController];
         privateKeyExportVc.privateKey = result;
         [weakself.OK_TopViewController.navigationController pushViewController:privateKeyExportVc animated:YES];
    }
}

- (void)deleteWalletPwd:(NSString *)pwd
{
    [kPyCommandsManager callInterface:kInterfaceDelete_wallet parameter:@{@"name":kWalletManager.currentWalletInfo.name,@"password":pwd}];
    [kWalletManager clearCurrentWalletInfo];
    [kTools tipMessage:MyLocalizedString(@"Wallet deleted successfully", nil)];
    [[NSNotificationCenter defaultCenter]postNotificationName:kNotiDeleteWalletComplete object:nil];
    [self.navigationController popViewControllerAnimated:YES];
}
- (NSArray *)allData
{
    if (!_allData) {
        NSMutableArray *allDataM = [NSMutableArray array];
        OKWalletDetailModel *model1 = [OKWalletDetailModel new];
        model1.titleStr = MyLocalizedString(@"address", nil);
        model1.rightLabelStr = kWalletManager.currentWalletInfo.addr;
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
            model3.rightLabelStr = MyLocalizedString(@"All subwallets derived from the ROOT mnemonic of HD wallet can be recovered with the root mnemonic, so there is no need to export mnemonic words for a single wallet. If you want to get the HD purse the root word mnemonic, please go to my assets HD wallet", nil);
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
            if ([kWalletManager.currentWalletInfo.coinType isEqualToString:COIN_ETH]) {
                [securityGroup addObject:modelS3];
            }
            [allDataM addObject:securityGroup];
        }
        
        if (_walletType == OKWalletTypeIndependent || _walletType == OKWalletTypeHardware || _walletType == OKWalletTypeMultipleSignature  || _walletType == OKWalletTypeObserve) {
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
            return MyLocalizedString(@"HD derived wallet", nil);
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
        case OKWalletTypeObserve:
            return MyLocalizedString(@"Observe the purse", nil);
            break;
        default:
            break;
    }
}
- (IBAction)editWalletNameClick:(UIButton *)sender {
    OKWeakSelf(self)
    OKEditWalletNameViewController *vc = [OKEditWalletNameViewController editWalletNameViewController:^{
        [weakself loadUI];
    }];
    vc.modalPresentationStyle = UIModalPresentationOverFullScreen;
    [self.OK_TopViewController presentViewController:vc animated:NO completion:nil];
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
