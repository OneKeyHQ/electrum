//
//  OKSpecialEquipmentViewController.m
//  OneKey
//
//  Created by xiaoliang on 2021/1/21.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKSpecialEquipmentViewController.h"
#import "OKCreateSelectWalletTypeCell.h"
#import "OKCreateSelectWalletTypeModel.h"
#import "OKPINCodeViewController.h"
#import "OKPwdViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"
#import "OKBiologicalViewController.h"
#import "OKFindFollowingWalletController.h"
#import "OKDeviceSettingsViewController.h"

@interface OKSpecialEquipmentViewController ()<OKHwNotiManagerDelegate>
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (nonatomic,strong)NSArray *walletTypeListArray;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *toSetingLabel;

@end

@implementation OKSpecialEquipmentViewController
+ (instancetype)specialEquipmentViewController
{
    return [[UIStoryboard storyboardWithName:@"Hardware" bundle:nil]instantiateViewControllerWithIdentifier:@"OKSpecialEquipmentViewController"];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = MyLocalizedString(@"pairing", nil);
    self.titleLabel.text = MyLocalizedString(@"This is a special device that stores HD wallet mnemonic words. You can...", nil);
    self.toSetingLabel.userInteractionEnabled = YES;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapClick)];
    [self.toSetingLabel addGestureRecognizer:tap];
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
    OKWeakSelf(self)
    kHwNotiManager.delegate = self;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
       id result = [kPyCommandsManager callInterface:kInterfacebixin_backup_device parameter:@{}];
        if (result != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakself restoreMnemonicWord:result];
            });
        }
    });
}
- (void)restoreMnemonicWord:(NSString *)word
{
    OKWeakSelf(self)
    NSString *mnemonicStr = word;
    id result =  [kPyCommandsManager callInterface:kInterfaceverify_legality parameter:@{@"data":mnemonicStr,@"flag":@"seed"}];
    if (result != nil) {
        if ([kWalletManager checkIsHavePwd]) {
            if (kWalletManager.isOpenAuthBiological) {
               [[YZAuthID sharedInstance]yz_showAuthIDWithDescribe:MyLocalizedString(@"OenKey request enabled", nil) BlockState:^(YZAuthIDState state, NSError *error) {
                   if (state == YZAuthIDStateNotSupport
                       || state == YZAuthIDStatePasswordNotSet || state == YZAuthIDStateTouchIDNotSet) { // 不支持TouchID/FaceID
                       [OKValidationPwdController showValidationPwdPageOn:self isDis:YES complete:^(NSString * _Nonnull pwd) {
                           [weakself createWallet:pwd mnemonicStr:mnemonicStr isInit:NO];
                       }];
                   } else if (state == YZAuthIDStateSuccess) {
                       NSString *pwd = [kOneKeyPwdManager getOneKeyPassWord];
                       [weakself createWallet:pwd mnemonicStr:mnemonicStr isInit:NO];
                   }
               }];
           }else{
               [OKValidationPwdController showValidationPwdPageOn:self isDis:NO complete:^(NSString * _Nonnull pwd) {
                    [weakself createWallet:pwd mnemonicStr:mnemonicStr isInit:NO];
               }];
           }
        }else{
            OKPwdViewController *pwdVc = [OKPwdViewController setPwdViewControllerPwdUseType:OKPwdUseTypeInitPassword setPwd:^(NSString * _Nonnull pwd) {
                [weakself createWallet:pwd mnemonicStr:mnemonicStr isInit:YES];
            }];
            [self.navigationController pushViewController:pwdVc animated:YES];
        }
    }
}

- (void)createWallet:(NSString *)pwd mnemonicStr:(NSString *)mnemonicStr isInit:(BOOL)isInit
{
    NSString *seed = mnemonicStr;
    [kTools showIndicatorView];
    OKWeakSelf(self)
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSDictionary *create =  [kPyCommandsManager callInterface:kInterfaceCreate_hd_wallet parameter:@{@"password":pwd,@"seed":seed}];
        OKCreateResultModel *createResultModel = [OKCreateResultModel mj_objectWithKeyValues:create];
        if (createResultModel != nil) {
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (createResultModel.derived_info.count == 0) {
                OKCreateResultWalletInfoModel *model = [createResultModel.wallet_info firstObject];
                [kPyCommandsManager callInterface:kInterfacerecovery_confirmed parameter:@{@"name_list":@[model.name]}];
                OKWalletInfoModel *walletInfoModel = [kWalletManager getCurrentWalletAddress:model.name];
                [kWalletManager setCurrentWalletInfo:walletInfoModel];
                if (kUserSettingManager.currentSelectPwdType.length > 0 && kUserSettingManager.currentSelectPwdType !=  nil) {
                    [kUserSettingManager setIsLongPwd:[kUserSettingManager.currentSelectPwdType boolValue]];
                }
                if (!kWalletManager.isOpenAuthBiological && isInit) {
                    OKBiologicalViewController *biologicalVc = [OKBiologicalViewController biologicalViewController:@"OKWalletViewController" pwd:pwd biologicalViewBlock:^{
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"0",@"takecareshow":@"1"}];
                    }];
                    [kTools hideIndicatorView];
                    [weakself.OK_TopViewController.navigationController pushViewController:biologicalVc animated:YES];
                }else{
                    [self.OK_TopViewController dismissToViewControllerWithClassName:@"OKWalletViewController" animated:YES complete:^{
                        [[NSNotificationCenter defaultCenter]postNotificationName:kNotiWalletCreateComplete object:@{@"pwd":pwd,@"backupshow":@"0",@"takecareshow":@"1"}];
                    }];
                }
            }else{
                OKFindFollowingWalletController *findFollowingWalletVc = [OKFindFollowingWalletController findFollowingWalletController];
                findFollowingWalletVc.pwd = pwd;
                findFollowingWalletVc.isInit = isInit;
                findFollowingWalletVc.createResultModel = createResultModel;
                [kTools hideIndicatorView];
                [weakself.OK_TopViewController.navigationController pushViewController:findFollowingWalletVc animated:YES];
            }
        });
    }else{
        [kTools hideIndicatorView];
    }
});
}


- (NSArray *)walletTypeListArray
{
    if (!_walletTypeListArray) {
        OKCreateSelectWalletTypeModel *model = [OKCreateSelectWalletTypeModel new];
        model.createWalletType = MyLocalizedString(@"Restore the phone HD wallet", nil);
        model.iconName = @"hd_wallet-1";
        model.tipsString = MyLocalizedString(@"Restore your HD wallet completely. Any wallet derived from the original HD mnemonic will be restored", nil);
        model.addtype = OKAddTypeSpecialEquipmentHw;
        _walletTypeListArray = @[model];
    }
    return _walletTypeListArray;
}

- (void)tapClick
{
    OKDeviceSettingsViewController *vc = [OKDeviceSettingsViewController deviceSettingsViewController];
    vc.deviceModel = [[OKDevicesManager sharedInstance]getDeviceModelWithID:kOKBlueManager.currentDeviceID];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - OKHwNotiManagerDelegate
- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type
{
    OKWeakSelf(self)
    if (type == OKHWNotiTypePin_Current) {
        dispatch_async(dispatch_get_main_queue(), ^{
            OKPINCodeViewController *pinCodeVc = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                });
            }];
            [weakself.navigationController pushViewController:pinCodeVc animated:YES];
        });
    }
}
@end
