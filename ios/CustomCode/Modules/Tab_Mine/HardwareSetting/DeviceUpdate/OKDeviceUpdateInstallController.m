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

@interface OKDeviceUpdateInstallController () <NSURLSessionDelegate>
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

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];

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
    self.finshedButton.titleLabel.text = MyLocalizedString(@"done", nil);
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
    
    [downloadTask resume];
    self.phase = OKDeviceUpdateInstallPhaseDownloading;
}

- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didWriteData:(int64_t)bytesWritten totalBytesWritten:(int64_t)totalBytesWritten totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite {
    NSLog(@"bytesWritten=%@,totalBytesWritten=%@,totalBytesExpectedToWrite=%@",@(bytesWritten),@(totalBytesWritten),@(totalBytesExpectedToWrite));
    float progress = (float)totalBytesWritten/totalBytesExpectedToWrite;
    self.progressView.progress = progress;
}


- (void)URLSession:(NSURLSession *)session downloadTask:(NSURLSessionDownloadTask *)downloadTask didFinishDownloadingToURL:(NSURL *)location {
    self.progressView.progress = 1.0;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *docDir = [paths objectAtIndex:0];
    
    NSString *filePath = [docDir stringByAppendingPathComponent:@"framework.bin"];
    
    NSError *error = nil;
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
    }
    [[NSFileManager defaultManager] copyItemAtURL:location toURL:[NSURL fileURLWithPath:filePath] error:&error];
    
    if (error) {
        NSLog(@"错误信息为:%@",[error localizedDescription]);
    } else {
        NSLog(@"拷贝文件成功，文件的路径为:%@",filePath);
        [self installFrameware:filePath];
    }
}

- (void)installFrameware:(NSString *)path {
    self.phase = OKDeviceUpdateInstallPhaseInstalling;
}


- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    if (error) {

    } else {
        self.phase = OKDeviceUpdateInstallPhaseDone;
       //下载完成处理
    }
}

- (void)setPhase:(OKDeviceUpdateInstallPhase)phase {
    _phase = phase;
    
    self.finshedButton.enabled = (phase == OKDeviceUpdateInstallPhaseDone);
    self.finshedButton.alpha = (phase == OKDeviceUpdateInstallPhaseDone ? 1 : 0.5);
    if (phase == OKDeviceUpdateInstallPhaseDownloading) {
        self.descLabel.text = kLocalizedString(@"downloading");
    } else if (phase == OKDeviceUpdateInstallPhaseInstalling) {
        self.descLabel.text = kLocalizedString(@"installing");
    } else if (phase == OKDeviceUpdateInstallPhaseDone) {
        self.descLabel.text = kLocalizedString(@"installed");
    }

}

- (void)back {
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
