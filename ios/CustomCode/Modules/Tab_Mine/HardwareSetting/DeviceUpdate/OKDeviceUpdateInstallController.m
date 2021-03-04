//
//  OKDeviceUpdateDownloadController.m
//  OneKey
//
//  Created by liuzj on 2021/1/12.
//  Copyright © 2021 Onekey. All rights reserved.
//


#define kLocalizedString(key) \
MyLocalizedString([@"hardwareWallet.update." stringByAppendingString:(key)], nil)

#import "OKDeviceUpdateInstallController.h"
#import "OKHwNotiManager.h"
#import "OKPINCodeViewController.h"
#import "OKDeviceConfirmController.h"
@import iOSDFULibrary;

@interface OKDeviceUpdateInstallController () <NSURLSessionDelegate, OKHwNotiManagerDelegate,
DFUServiceDelegate, LoggerDelegate, DFUProgressDelegate>

@property (nonatomic, strong) NSURLSessionConfiguration* sessionConfiguration;
@property (nonatomic, strong) NSURLSessionDownloadTask* downloadTask;
@property (nonatomic, strong) NSURLSession* session;
@property (weak, nonatomic) IBOutlet UIProgressView *progressView;
@property (weak, nonatomic) IBOutlet UILabel *descLabel;
@property (weak, nonatomic) IBOutlet UIButton *finshedButton;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation OKDeviceUpdateInstallController

+ (instancetype)controllerWithStoryboard {
    return [[UIStoryboard storyboardWithName:@"HardwareSetting" bundle:nil] instantiateViewControllerWithIdentifier:@"OKDeviceUpdateInstallController"];
}


- (void)awakeFromNib {
    [super awakeFromNib];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(installProcess:) name:@"process_notification" object:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [OKHwNotiManager sharedInstance].delegate = self;
    self.phase = OKDeviceUpdateInstallPhaseBegin;
    [self.finshedButton addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    if (self.framewareDownloadURL) {
        [self startDownload];
    } else {
        [self back];
    }
}

- (void)setupUI {
    self.title = kLocalizedString(@"title");
    [self setNavigationBarBackgroundColorWithClearColor];
    self.navigationItem.leftBarButtonItem = [UIBarButtonItem backBarButtonItemWithTarget:self selector:@selector(back)];
    self.progressView.progress = 0;

    if (self.type == OKDeviceUpdateTypeFramework) {
        self.titleLabel.text = kLocalizedString(@"sysUpdating");
    } else if (self.type == OKDeviceUpdateTypeBluetooth) {
        self.titleLabel.text = kLocalizedString(@"bluetoothUpdating");
    }

    [self.finshedButton setLayerRadius: 20];
    self.finshedButton.titleLabel.text = @"done".localized;
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [UIApplication sharedApplication].idleTimerDisabled = YES;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [UIApplication sharedApplication].idleTimerDisabled = NO;
}

- (void)startDownload {
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    self.sessionConfiguration = sessionConfiguration;

    NSURLSession *downloadSession = [NSURLSession sessionWithConfiguration:self.sessionConfiguration delegate:self delegateQueue:[NSOperationQueue mainQueue]];
    self.session = downloadSession;

    NSURL *url = [NSURL URLWithString:self.framewareDownloadURL];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    NSURLSessionDownloadTask *downloadTask =  [downloadSession downloadTaskWithRequest:request];
    self.downloadTask = downloadTask;

    self.phase = OKDeviceUpdateInstallPhaseDownloading;

    NSString *filePath = [self filePathForUrl];
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        self.progressView.progress = 1.0;
        if (self.type == OKDeviceUpdateTypeBluetooth) {
            [self installBluetoothFirmware:filePath];
        } else if (self.type == OKDeviceUpdateTypeFramework) {
            [self installFrameware:filePath];
        }
    } else {
        [downloadTask resume];
    }
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didWriteData:(int64_t)bytesWritten totalBytesWritten:(int64_t)totalBytesWritten totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite {
    NSLog(@"bytesWritten=%@,totalBytesWritten=%@,totalBytesExpectedToWrite=%@",@(bytesWritten),@(totalBytesWritten),@(totalBytesExpectedToWrite));
    float progress = (float)totalBytesWritten/totalBytesExpectedToWrite;
    self.progressView.progress = progress;
}


- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didFinishDownloadingToURL:(NSURL *)location {
    self.progressView.progress = 1.0;

    NSString *filePath = [self filePathForUrl];

    NSError *error = nil;
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
    }
    [[NSFileManager defaultManager] copyItemAtURL:location toURL:[NSURL fileURLWithPath:filePath] error:&error];

    if (error) {
        NSLog(@"错误信息为:%@",[error localizedDescription]);
    } else {
        NSLog(@"拷贝文件成功，文件的路径为:%@",filePath);
        if (self.type == OKDeviceUpdateTypeBluetooth) {
            [self installBluetoothFirmware:filePath];
        } else if (self.type == OKDeviceUpdateTypeFramework) {
            [self installFrameware:filePath];
        }
    }
}

- (void)installFrameware:(NSString *)path {
    self.phase = OKDeviceUpdateInstallPhaseInstalling;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [kPyCommandsManager callInterface:kInterfacefirmware_update parameter:@{@"filename": path}];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (self.phase != OKDeviceUpdateInstallPhaseDone) {
                [self back];
            }
        });

    });
}

- (void)installBluetoothFirmware:(NSString *)path {
    NSURL *zipUrl = [NSURL fileURLWithPath:path];

    CBPeripheral *peripheral = kOKBlueManager.currentPeripheral;
    DFUFirmware *selectedFirmware = [[DFUFirmware alloc] initWithUrlToZipFile:zipUrl];
    DFUServiceInitiator *initiator = [[DFUServiceInitiator alloc] initWithCentralManager:[kOKBlueManager centralManager] target:peripheral];
    id _ = [initiator withFirmware:selectedFirmware];
    initiator.delegate = self; // - to be informed about current state and errors
    initiator.logger = self;
    initiator.progressDelegate = self;
    [initiator start];
}


- (void)installProcess:(NSNotification *)noti {
    NSInteger process = [noti.object integerValue];
    dispatch_async(dispatch_get_main_queue(), ^{

        // TODO: 硬件输入 pin 码没有通知临时解决方案
        static NSUInteger received;
        if (++received < 5) {
            [self.navigationController popToRootViewControllerAnimated:YES];
        }

        self.progressView.progress = process / 100.0;
        if (process >= 100) {
            self.phase = OKDeviceUpdateInstallPhaseDone;
        }
    });

}

- (void)setPhase:(OKDeviceUpdateInstallPhase)phase {
    _phase = phase;

    self.finshedButton.enabled = (phase == OKDeviceUpdateInstallPhaseDone);
    self.finshedButton.alpha = (phase == OKDeviceUpdateInstallPhaseDone ? 1 : 0.3);
    if (phase == OKDeviceUpdateInstallPhaseDownloading) {
        self.descLabel.text = kLocalizedString(@"downloading");
    } else if (phase == OKDeviceUpdateInstallPhaseInstalling) {
        self.descLabel.text = kLocalizedString(@"installing");
    } else if (phase == OKDeviceUpdateInstallPhaseDone) {
        if (self.type == OKDeviceUpdateTypeBluetooth) {
            self.descLabel.text = @"固件升级成功。请前往系统蓝牙设置页忽略此设备，然后重启“OneKey” App。";
        } else {
            self.descLabel.text = kLocalizedString(@"installed");
        }
    }
}

- (NSString *)filePathForUrl {
    NSString *namePatten = @"%lu.bin";
    if (self.type == OKDeviceUpdateTypeBluetooth) {
        namePatten = @"%lu.zip";
    }
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *docDir = [paths objectAtIndex:0];
    NSString *filePath = [docDir stringByAppendingPathComponent:[NSString stringWithFormat:namePatten, self.framewareDownloadURL.hash]];
    return filePath;
}

- (void)hwNotiManagerDekegate:(OKHwNotiManager *)hwNoti type:(OKHWNotiType)type {
    OKWeakSelf(self)
    dispatch_async(dispatch_get_main_queue(), ^{
        if (type == OKHWNotiTypePin_Current) {
            OKPINCodeViewController *pinCode = [OKPINCodeViewController PINCodeViewController:^(NSString * _Nonnull pin) {
                NSLog(@"pinCode = %@",pin);
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    [kPyCommandsManager callInterface:kInterfaceset_pin parameter:@{@"pin":pin}];
                });
            }];
            pinCode.forbidInteractivePopGestureRecognizer = YES;
            pinCode.backToPreviousCallback = ^{
                [weakself cancel];
            };
            [weakself.navigationController pushViewController:pinCode animated:YES];

        } else if (type == OKHWNotiTypeKeyConfirm) {
            OKDeviceConfirmController *confirmVC = [OKDeviceConfirmController controllerWithStoryboard];
            confirmVC.titleText = @"Verify on the equipment".localized;
            confirmVC.btnText = @"cancel".localized;
            confirmVC.btnCallback = ^{
                [kPyCommandsManager cancel];
            };
            confirmVC.backToPreviousCallback = ^{
                [kPyCommandsManager cancel];
            };
            confirmVC.forbidInteractivePopGestureRecognizer = YES;
            [weakself.navigationController pushViewController:confirmVC animated:YES];
        } else {
//            NSAssert(0, @"OKDeviceUpdateInstallController hwNotiManagerDekegate unimplemented");
        }
    });
}


- (void)settingAndReboot {
    OKDeviceModel *device = [[OKDevicesManager sharedInstance] getDeviceModelWithID:[OKDevicesManager sharedInstance].recentDeviceId];
    NSString *ble_name = device.deviceInfo.ble_name;
    NSString *msg = [NSString stringWithFormat:@"请前往系统蓝牙设置页找到设备 %@ 并点击“忽略此设备”，然后重新连接。", ble_name ?: @""];
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"蓝牙升级成功，请重新进行蓝牙配对"
                                                                   message:msg
                                                            preferredStyle:UIAlertControllerStyleAlert];

    UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"去设置" style:UIAlertActionStyleDefault
                                                          handler:^(UIAlertAction * action) {
        NSURL *url = [NSURL URLWithString: @"App-Prefs:root=Bluetooth"];
        [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:^(BOOL success) {
            exit(0);
        }];                                                              }];
    [alert addAction:defaultAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)cancel {
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        [kPyCommandsManager cancelPIN];
    });
    [self back];
}

- (void)back {
    if (self.doneCallback) {
        self.doneCallback(self.phase == OKDeviceUpdateInstallPhaseDone);
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - LoggerDelegate
- (void)logWith:(enum LogLevel)level message:(NSString *)message{
    NSLog(@"dfu logWith---------level = %ld,-------message,%@", (long)level, message);
}

#pragma mark - DFUServiceDelegate & DFUProgressDelegate
- (void)dfuProgressDidChangeFor:(NSInteger)part outOf:(NSInteger)totalParts to:(NSInteger)progress currentSpeedBytesPerSecond:(double)currentSpeedBytesPerSecond avgSpeedBytesPerSecond:(double)avgSpeedBytesPerSecond {
    self.progressView.progress = progress * 0.01;
}

- (void)dfuStateDidChangeTo:(enum DFUState)state {
    if (state == 0) {
        self.phase = OKDeviceUpdateInstallPhaseInstalling;
    }
    if (state == 6) { // completed
        self.phase = OKDeviceUpdateInstallPhaseDone;
        [self settingAndReboot];
    }
    NSLog(@"dfu state: %ld", (long)state);
}

- (void)dfuError:(enum DFUError)error didOccurWithMessage:(NSString *)message {
    NSLog(@"dfu Error-----------error = %ld,-------------message = %@", (long)error, message);
}


@end
