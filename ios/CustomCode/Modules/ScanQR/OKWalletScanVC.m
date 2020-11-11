//
//  ViewController.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import "OKWalletScanVC.h"
#import "OKScanView.h"
#import "OKQRCodeScanManager.h"
#import "UIBarButtonItem+CustomBarButtonItem.h"
#import "OKAppAuthorityManager.h"
#import <AVFoundation/AVFoundation.h>

@interface OKWalletScanVC () <OKQRCodeScanManagerDelegate>
{
    UIImage *_navigationBarBackgroundImage;
    NSDictionary<NSAttributedStringKey, id> *_navigationBarTitleTextAttributes;
    BOOL _navigationBarTranslucent;
    UIColor *_barTintColor;
}

@property (nonatomic, strong) OKQRCodeScanManager *scanManager;
@property (weak, nonatomic) IBOutlet OKScanView *scanView;
@property (weak, nonatomic) IBOutlet UILabel *scanDescriptionLabel;

@end

@implementation OKWalletScanVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = MyLocalizedString(@"Scan QR Code", nil);
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem barButtonItemWithTitle:MyLocalizedString(@"Photo Album", nil) bounds:CGSizeMake(100, 30) size:15 titleColor:[UIColor whiteColor] backgroundColor:[UIColor clearColor] isSetLayer:NO target:self selector:@selector(toPhotoLibrary)];
    self.scanDescriptionLabel.text = MyLocalizedString(@"Put QR code in the frame. Scan it.", nil);
    [self backButtonWhiteColor];

    _navigationBarBackgroundImage = [self.navigationController.navigationBar backgroundImageForBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
    _navigationBarTranslucent = self.navigationController.navigationBar.isTranslucent;
    _navigationBarTitleTextAttributes = self.navigationController.navigationBar.titleTextAttributes;
    _barTintColor = self.navigationController.navigationBar.barTintColor;
    // 扫描二维码
    __weak typeof(self) weakSelf = self;
    self.scanView.lightBtnEventBlocks = ^(BOOL selected){
        if (selected) {
            [weakSelf.scanManager torchOn];
        } else {
            [weakSelf.scanManager torchOff];
        }
    };
    [self.scanView createTimer];
    [self.scanManager setupSessionOnController:self];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.scanManager sessionStartRunning];
    if (@available(iOS 13.0, *) && self.scanningType == ScanningTypeImportMninomic) {
        self.navigationController.navigationBar.barTintColor = HexColor(0x000000);
    }else{
        [self.navigationController.navigationBar setTranslucent:YES];
        [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    }

    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor whiteColor]};
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (@available(iOS 13.0, *) && self.scanningType == ScanningTypeImportMninomic) {
        self.navigationController.navigationBar.barTintColor = HexColor(0x000000);
    }else{
        [self.navigationController.navigationBar setTranslucent:YES];
        [self.navigationController.navigationBar setBackgroundImage:[UIImage new] forBarMetrics:UIBarMetricsDefault];
    }

    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName:[UIColor whiteColor]};
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.scanManager sessionStopRunning];
    [self resetNavigationBar];
}

- (void)resetNavigationBar {
    UINavigationController *naVC = self.navigationController ?:self.OK_NavigationController;
    [naVC.navigationBar setTranslucent:_navigationBarTranslucent];
    [naVC.navigationBar setBackgroundImage:_navigationBarBackgroundImage forBarMetrics:UIBarMetricsDefault];
    naVC.navigationBar.titleTextAttributes = _navigationBarTitleTextAttributes;
    naVC.navigationBar.barTintColor = _barTintColor;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    return UIStatusBarStyleLightContent;
}

- (OKQRCodeScanManager *)scanManager {
    if (_scanManager == nil) {
        _scanManager = [[OKQRCodeScanManager alloc] init];
        _scanManager.delegate = self;
    }
    return _scanManager;
}

- (void)toPhotoLibrary {
    if ([OKAppAuthorityManager canReadPhotos]) {
        [self.scanManager readQRCodeFromPhotoLibraryTo:self];
    } else {
        [self presentViewController:[self alertWithMessage:[NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"Please open the photos permissions: Settings->Privacy->Photos->", nil),[kTools getAppDisplayName],MyLocalizedString(@"(Open)", nil)]] animated:YES completion:nil];;
    }
}
#pragma mark - OKQRCodeScanManagerDelegate
// 扫描二维码成功回调
- (void)OKQRCodeScanManager:(OKQRCodeScanManager *)scanManager didOutputMetadataObjects:(NSArray *)metadataObjects {
    AVMetadataMachineReadableCodeObject *obj = metadataObjects[0];
    [self processResult:obj.stringValue];
}

// 读取相册二维码成功回调
- (void)pickerController:(UIImagePickerController *)picker didFinishPickingMediaWithMessage:(NSString *)message {
    [self processResult:message];
}

- (void)processResult:(NSString *)result {
    switch (self.scanningType) {
        case ScanningTypeAddress: {
            [self.navigationController popViewControllerAnimated:YES];
            if (self.scanningCompleteBlock) {
                self.scanningCompleteBlock(result);
            }
        }
            break;
        default:
            [self scanFinish:result];
            break;
    }
}

- (void)scanFinish:(id)message {
    [self.navigationController popViewControllerAnimated:NO];
    if (self.scanningCompleteBlock) {
        self.scanningCompleteBlock(message);
    }
}
- (void)captureDidOutput:(CGFloat)brightnessValue {
    if (self.scanView.lightBtnIsShowing && self.scanManager.torchHadOn) {
        return;
    }
    if ([self.scanManager needTorchOn:brightnessValue]) {
        [self.scanView showTorch];
    } else {
        [self.scanView hideTorch];
    }
}

- (void)authorizePushOn:(UIViewController *)viewController {
    if ([OKAppAuthorityManager canUseCamera]) {
        [viewController.navigationController pushViewController:self animated:YES];
    } else {
        [viewController presentViewController:[self alertWithMessage:[NSString stringWithFormat:@"%@%@%@",MyLocalizedString(@"Please open the camera permissions: Settings->Privacy->Camera->", nil),[kTools getAppDisplayName],MyLocalizedString(@"(Open)", nil)]] animated:YES completion:nil];
    }
}

- (UIAlertController *)alertWithMessage:(NSString *)message {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:MyLocalizedString(@"Gentle Hint", nil) message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:MyLocalizedString(@"Set Later", nil) style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alert addAction:cancelAction];
    UIAlertAction *updateAction = [UIAlertAction actionWithTitle:MyLocalizedString(@"Set Now", nil) style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [kTools okJumpOpenURL:UIApplicationOpenSettingsURLString];
    }];
    [alert addAction:updateAction];
    return alert;
}
@end
