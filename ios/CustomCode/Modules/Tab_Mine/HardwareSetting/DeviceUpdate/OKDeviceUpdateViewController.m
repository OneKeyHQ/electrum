//
//  OKDeviceUpdateViewController.m
//  OneKey
//
//  Created by liuzj on 09/01/2021.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKDeviceUpdateViewController.h"
#import "NSDictionary+OKKeyPath.h"
#import "OKDeviceUpdateInstallController.h"
#import "OKDeviceUpdateModel.h"

#define kLocalizedString(key) \
MyLocalizedString([@"hardwareWallet.update." stringByAppendingString:(key)], nil)

@interface OKDeviceUpdateCell ()
@property (weak, nonatomic) IBOutlet UIView *updateButtonView;
@property (weak, nonatomic) IBOutlet UIView *versionLabelView;
@property (weak, nonatomic) IBOutlet UILabel *versionLabel;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UILabel *updateLabel;
@end

@implementation OKDeviceUpdateCell

- (void)awakeFromNib {
    [super awakeFromNib];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(updateBtnClick)];
    [self.updateButtonView addGestureRecognizer:tap];
    self.updateLabel.text = kLocalizedString(@"update");
}

- (void) updateBtnClick {
    if (!self.updateClickCallback) {
        return;
    }
    self.updateClickCallback(self.updateType, self.updateUrl);
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.updateButtonView.layer.masksToBounds = YES;
    self.updateButtonView.layer.cornerRadius = self.updateButtonView.height * 0.5;
    self.versionLabelView.layer.masksToBounds = YES;
    self.versionLabelView.layer.cornerRadius = self.versionLabelView.height * 0.5;
}

- (void)setVersionDesc:(NSString *)versionDesc {
    _versionDesc = versionDesc;
    self.versionLabel.text = versionDesc;
}

- (void)setUpdateDesc:(NSString *)updateDesc {
    _updateDesc = updateDesc;
    self.descLabel.text = updateDesc;
}

@end

@interface OKDeviceAlreadyUpToDateCell()
@property (weak, nonatomic) IBOutlet UILabel *tipLabel;
@end
@implementation OKDeviceAlreadyUpToDateCell : UITableViewCell
- (void)awakeFromNib {
    [super awakeFromNib];
    self.tipLabel.text = MyLocalizedString(@"hardwareWallet.update.uptodate", nil);
}
@end

@interface OKDeviceUpdateViewController () <UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UILabel *currentVersionLabel;
@property (strong, nonatomic) OKDeviceUpdateModel *updateModel;
@property (strong, nonatomic) NSArray <NSDictionary *> *cellsData;
@end

@implementation OKDeviceUpdateViewController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceUpdateViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [self checkAvailableUpdate];
}

- (void)checkAvailableUpdate {
    NSURLSession *session = [NSURLSession sharedSession];
    NSURL *url = [NSURL URLWithString:@"https://data.onekey.so/version.json"];
    NSURLSessionTask *task = [session dataTaskWithURL:url completionHandler:^(NSData *data, NSURLResponse *response, NSError* error) {
        if (error) {
            return;
        }
        id json = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
        if ([json isKindOfClass:[NSDictionary class]]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                self.updateModel = [[OKDeviceUpdateModel alloc] initWithDict:json];
                [self updateCellModel];
            });
        }
    }];
    [task resume];
}

- (void)setupUI {
    self.title = MyLocalizedString(@"hardwareWallet.update", nil);
    self.tableView.backgroundColor = HexColor(0xf5f6f7);
    self.tableView.allowsSelection = NO;

    NSString *versionText = kLocalizedString(@"currentDesc");
    
    // TODO: get from device.
    NSString *frameworkVersionStr = @"9.9.9";
    NSString *bluetoothVersionStr = @"1.1.1";
    versionText = [versionText stringByReplacingOccurrencesOfString:@"<frameworkVersion>" withString:frameworkVersionStr];
    versionText = [versionText stringByReplacingOccurrencesOfString:@"<bluetoothVersion>" withString:bluetoothVersionStr];
    self.currentVersionLabel.text = versionText;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSDictionary *cellData = self.cellsData[indexPath.row];
    OKDeviceUpdateType cellType = [cellData[@"updateType"] integerValue];
    
    if (cellType == OKDeviceUpdateTypeAlreadyUpToDate) {
        OKDeviceAlreadyUpToDateCell *uptodateCell = [tableView dequeueReusableCellWithIdentifier:@"OKDeviceAlreadyUpToDateCell"];
        if(!uptodateCell) {
            uptodateCell = [[OKDeviceAlreadyUpToDateCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"OKDeviceAlreadyUpToDateCell"];
        }
        return uptodateCell;
    }
    
    static NSString *cellID = @"OKDeviceUpdateCell";
    OKDeviceUpdateCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID];
    if (cell == nil) {
        cell = [[OKDeviceUpdateCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellID];
    }
    cell.updateType = [cellData[@"updateType"] integerValue];
    cell.versionDesc = cellData[@"versionDesc"];
    cell.updateDesc = cellData[@"updateDesc"];
    cell.updateUrl = cellData[@"url"];
    cell.updateClickCallback = ^(OKDeviceUpdateType type, NSString *url) {
        OKDeviceUpdateInstallController *vc = [OKDeviceUpdateInstallController controllerWithStoryboard];
        vc.framewareDownloadURL = url;
        vc.type = type;
        BaseNavigationController *nav = [[BaseNavigationController alloc]initWithRootViewController:vc];
        [self presentViewController:nav animated:YES completion:nil];
    };
    return cell;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.cellsData.count;
}

- (void)updateCellModel {
    
    NSMutableArray *cellsData = [[NSMutableArray alloc] initWithCapacity:2];
    if ([self.updateModel systemFirmwareNeedUpdate:@"0.0.0"]) {
        NSString *version = self.updateModel.systemFirmwareVersion;
        NSString *versionDesc = [kLocalizedString(@"newSysAvailable") stringByAppendingString:version];
        NSString *updateDesc = ([OKLocalizableManager getCurrentLanguageType] == AppLanguageTypeZh_Hans) ? self.updateModel.systemFirmwareChangeLogCN : self.updateModel.systemFirmwareChangeLogEN;
        NSString *url = self.updateModel.systemFirmwareUrl;
        [cellsData addObject:@{
            @"updateType": @(OKDeviceUpdateTypeFramework),
            @"versionDesc": versionDesc,
            @"updateDesc": updateDesc,
            @"url": url
        }];
    }
    
    if ([self.updateModel bluetoothFirmwareNeedUpdate:@"3.0.0"]) {
        NSString *version = self.updateModel.bluetoothFirmwareVersion;
        NSString *versionDesc = [kLocalizedString(@"newBluetoothAvailable") stringByAppendingString:version];
        NSString *updateDesc = ([OKLocalizableManager getCurrentLanguageType] == AppLanguageTypeZh_Hans) ? self.updateModel.bluetoothFirmwareChangeLogCN : self.updateModel.bluetoothFirmwareChangeLogEN;
        NSString *url = self.updateModel.bluetoothFirmwareUrl;
        [cellsData addObject:@{
            @"updateType": @(OKDeviceUpdateTypeBluetooth),
            @"versionDesc": versionDesc,
            @"updateDesc": updateDesc,
            @"url": url
        }];
    }
    
    self.cellsData = cellsData;
    [self.tableView reloadData];
}


@end