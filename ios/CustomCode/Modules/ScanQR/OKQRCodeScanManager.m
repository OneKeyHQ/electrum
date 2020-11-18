//
//  OKScanView.m
//  OneKey
//
//  Created by xiaoliang on 2020/9/28.
//
#import "OKQRCodeScanManager.h"
#import <AVFoundation/AVFoundation.h>
#import <Photos/Photos.h>
#import <ImageIO/ImageIO.h>

@interface OKQRCodeScanManager () <AVCaptureMetadataOutputObjectsDelegate, OKQRCodeScanManagerDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate, AVCaptureVideoDataOutputSampleBufferDelegate>
@property (nonatomic, strong) AVCaptureSession *session;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *videoPreviewLayer;
@property (nonatomic, weak) UIViewController *currentVC;
/** 判断相册访问权限是否授权 */
@property (nonatomic, assign) BOOL isPHAuthorization;
@end

@implementation OKQRCodeScanManager

- (void)setupSessionOnController:(UIViewController *)currentController {
    // 1、获取摄像设备
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    
    // 2、创建设备输入流
    AVCaptureDeviceInput *deviceInput = [AVCaptureDeviceInput deviceInputWithDevice:device error:nil];
    
    // 3、创建数据输出流
    AVCaptureMetadataOutput *metaDataOutput = [[AVCaptureMetadataOutput alloc] init];
    
    // 4、设置代理：在主线程里刷新
    [metaDataOutput setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
    // 光源输出
    AVCaptureVideoDataOutput *viewDataOutput = [[AVCaptureVideoDataOutput alloc] init];
    [viewDataOutput setSampleBufferDelegate:self queue:dispatch_get_main_queue()];

    // 设置扫描范围（每一个取值0～1，以屏幕右上角为坐标原点）
    // 注：微信二维码的扫描范围是整个屏幕，这里并没有做处理（可不用设置）
//    metadataOutput.rectOfInterest = CGRectMake(0.05, 0.2, 0.7, 0.6);
    
    // 5、创建会话对象
    _session = [[AVCaptureSession alloc] init];
    // 会话采集率: AVCaptureSessionPresetHigh
    _session.sessionPreset = AVCaptureSessionPreset1920x1080; // 推荐使用AVCaptureSessionPreset1920x1080，对于小型的二维码读取率较高
    
    // 6、添加设备输入流到会话对象
    if ([_session canAddInput:deviceInput]) {
        [_session addInput:deviceInput];
    }
    
    // 7、添加设备输入流到会话对象
    if ([_session canAddOutput:metaDataOutput]) {
        [_session addOutput:metaDataOutput];
    }
    // 光源
    if ([_session canAddOutput:viewDataOutput]) {
        [_session addOutput:viewDataOutput];
    }
    
    // 8、设置数据输出类型，需要将数据输出添加到会话后，才能指定元数据类型，否则会报错
    // 设置扫码支持的编码格式(如下设置条形码和二维码兼容)
    // @[AVMetadataObjectTypeQRCode, AVMetadataObjectTypeEAN13Code,  AVMetadataObjectTypeEAN8Code, AVMetadataObjectTypeCode128Code]
    metaDataOutput.metadataObjectTypes = @[AVMetadataObjectTypeQRCode, AVMetadataObjectTypeEAN13Code, AVMetadataObjectTypeEAN8Code, AVMetadataObjectTypeCode128Code];
    
    // 9、实例化预览图层, 传递_session是为了告诉图层将来显示什么内容
    _videoPreviewLayer = [AVCaptureVideoPreviewLayer layerWithSession:_session];
    // 保持纵横比；填充层边界
    _videoPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
    _videoPreviewLayer.frame = currentController.view.layer.bounds;
    [currentController.view.layer insertSublayer:_videoPreviewLayer atIndex:0];
}

- (void)sessionRestartRunning {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [_session startRunning];
    });
}

- (void)sessionStartRunning {
    [_session startRunning];
}

- (void)sessionStopRunning {
    [_session stopRunning];
}

- (void)videoPreviewLayerRemoveFromSuperlayer {
    _session = nil;
    [_videoPreviewLayer removeFromSuperlayer];
}

- (void)playSound {
    NSString *audioFile = [[NSBundle mainBundle] pathForResource:@"QRCodeScanSound" ofType:@"caf"];
    NSURL *fileUrl = [NSURL fileURLWithPath:audioFile];
    SystemSoundID soundID = 0;
    AudioServicesCreateSystemSoundID((__bridge CFURLRef)(fileUrl), &soundID);
    AudioServicesAddSystemSoundCompletion(soundID, NULL, NULL, soundCompleteCallback, NULL);
    AudioServicesPlaySystemSound(soundID); // 播放音效
}

void soundCompleteCallback(SystemSoundID soundID, void *clientData){
    
}

#pragma mark - - - AVCaptureMetadataOutputObjectsDelegate
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection {
    [self playSound];
    [self sessionStopRunning];
    if (self.delegate && [self.delegate respondsToSelector:@selector(OKQRCodeScanManager:didOutputMetadataObjects:)]) {
        [self.delegate OKQRCodeScanManager:self didOutputMetadataObjects:metadataObjects];
    }
}

#pragma mark - 从相册读取
- (void)readQRCodeFromPhotoLibraryTo:(UIViewController *)currentController {
    self.currentVC = currentController;
    // 1、 获取摄像设备
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if (device) {
        // 判断授权状态
        PHAuthorizationStatus status = [PHPhotoLibrary authorizationStatus];
        if (status == PHAuthorizationStatusNotDetermined) { // 用户还没有做出选择
            // 弹框请求用户授权
            [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
                if (status == PHAuthorizationStatusAuthorized) { // 用户第一次同意了访问相册权限
                    self.isPHAuthorization = YES;
                    dispatch_async(dispatch_get_main_queue(), ^{
                        UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
                        imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary; //（选择类型）表示仅仅从相册中选取照片
                        imagePicker.delegate = self;
                        [self.currentVC presentViewController:imagePicker animated:YES completion:nil];
                    });
                } else { // 用户第一次拒绝了访问相机权限

                }
            }];
            
        } else if (status == PHAuthorizationStatusAuthorized) { // 用户允许当前应用访问相册
            self.isPHAuthorization = YES;
            UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
            imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary; //（选择类型）表示仅仅从相册中选取照片
            imagePicker.delegate = self;
            [self.currentVC presentViewController:imagePicker animated:YES completion:nil];
        } else if (status == PHAuthorizationStatusDenied) { // 用户拒绝当前应用访问相册
            NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
            NSString *appName = [infoDict objectForKey:@"CFBundleName"];
            NSString *message = [NSString stringWithFormat:@"请前往 -> [设置 - 隐私 - 照片 - %@ 打开访问开关", appName];
            UIAlertController *alertC = [UIAlertController alertControllerWithTitle:@"温馨提示" message:message preferredStyle:(UIAlertControllerStyleAlert)];
            UIAlertAction *alertA = [UIAlertAction actionWithTitle:@"确定" style:(UIAlertActionStyleDefault) handler:^(UIAlertAction * _Nonnull action) {
                
            }];
            [alertC addAction:alertA];
            [self.currentVC presentViewController:alertC animated:YES completion:nil];
        } else if (status == PHAuthorizationStatusRestricted) {
            UIAlertController *alertC = [UIAlertController alertControllerWithTitle:@"温馨提示" message:@"由于系统原因, 无法访问相册" preferredStyle:(UIAlertControllerStyleAlert)];
            UIAlertAction *alertA = [UIAlertAction actionWithTitle:@"确定" style:(UIAlertActionStyleDefault) handler:^(UIAlertAction * _Nonnull action) {
                
            }];
            [alertC addAction:alertA];
            [self.currentVC presentViewController:alertC animated:YES completion:nil];
        }
    }
}

#pragma mark - - - UIImagePickerControllerDelegate
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self.currentVC dismissViewControllerAnimated:YES completion:nil];
    if (self.delegate && [self.delegate respondsToSelector:@selector(pickerControllerDidCancel:)]) {
        [self.delegate pickerControllerDidCancel:picker];
    }
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {
    // 对选取照片的处理，如果选取的图片尺寸过大，则压缩选取图片，否则不作处理
    UIImage *image = [UIImage imageSizeWithScreenImage:info[UIImagePickerControllerOriginalImage]];
    // CIDetector(CIDetector可用于人脸识别)进行图片解析，从而使我们可以便捷的从相册中获取到二维码
    // 声明一个 CIDetector，并设定识别类型 CIDetectorTypeQRCode
    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:nil options:@{CIDetectorAccuracy: CIDetectorAccuracyHigh}];
    // 取得识别结果
    NSArray *features = [detector featuresInImage:[CIImage imageWithCGImage:image.CGImage]];
    if (features.count == 0) {
        [self.currentVC dismissViewControllerAnimated:YES completion:nil];
        NSString *message = @"暂未识别出扫描的二维码";
        UIAlertController *alertC = [UIAlertController alertControllerWithTitle:@"很抱歉" message:message preferredStyle:(UIAlertControllerStyleAlert)];
        UIAlertAction *alertA = [UIAlertAction actionWithTitle:@"确定" style:(UIAlertActionStyleDefault) handler:^(UIAlertAction * _Nonnull action) {
            
        }];
        [alertC addAction:alertA];
        [self.currentVC presentViewController:alertC animated:YES completion:nil];
        return;
    } else {
        NSString *resultStr = @"";
        for (int index = 0; index < [features count]; index ++) {
            CIQRCodeFeature *feature = [features objectAtIndex:index];
            resultStr = feature.messageString;
        }
        [self.currentVC dismissViewControllerAnimated:YES completion:^{
            if (self.delegate && [self.delegate respondsToSelector:@selector(pickerController:didFinishPickingMediaWithMessage:)]) {
                [self.delegate pickerController:picker didFinishPickingMediaWithMessage:resultStr];
            }
        }];
    }
}

#pragma mark - AVCaptureVideoDataOutputSampleBufferDelegate的方法
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection {
    CFDictionaryRef metadataDict = CMCopyDictionaryOfAttachments(NULL,sampleBuffer, kCMAttachmentMode_ShouldPropagate);
    NSDictionary *metadata = [[NSMutableDictionary alloc] initWithDictionary:(__bridge NSDictionary*)metadataDict];
    CFRelease(metadataDict);
    NSDictionary *exifMetadata = [[metadata objectForKey:(NSString *)kCGImagePropertyExifDictionary] mutableCopy];
    float brightnessValue = [[exifMetadata objectForKey:(NSString *)kCGImagePropertyExifBrightnessValue] floatValue];
    if ([self.delegate respondsToSelector:@selector(captureDidOutput:)]) {
        [self.delegate captureDidOutput:brightnessValue];
    }
}

- (void)torchOn {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if ([device hasTorch]) {
        [device lockForConfiguration:nil];
        [device setTorchMode: AVCaptureTorchModeOn];//开
        [device unlockForConfiguration];
    }
}

- (void)torchOff {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if ([device hasTorch]) {
        [device lockForConfiguration:nil];
        [device setTorchMode: AVCaptureTorchModeOff];//关
        [device unlockForConfiguration];
    }
}

- (BOOL)needTorchOn:(CGFloat)brightnessValue {
    if (brightnessValue <= -4) {
        return YES;
    } else {
        return NO;
    }
}

- (BOOL)torchHadOn {
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    if ([device hasTorch] && device.torchMode == AVCaptureTorchModeOn) {
        return YES;
    } else {
        return NO;
    }
}
@end
