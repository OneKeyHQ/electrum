//
//  OKScanView.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import <Foundation/Foundation.h>
@class OKQRCodeScanManager;

@protocol OKQRCodeScanManagerDelegate <NSObject>

@optional
/** 二维码扫描获取数据的回调方法 (metadataObjects: 扫描二维码数据信息) */
- (void)OKQRCodeScanManager:(OKQRCodeScanManager *)scanManager didOutputMetadataObjects:(NSArray *)metadataObjects;
// 相册获取回调
- (void)pickerControllerDidCancel:(UIImagePickerController *)picker;
- (void)pickerController:(UIImagePickerController *)picker didFinishPickingMediaWithMessage:(NSString *)message;
// 亮度值回调(-5 ~ 9 ？)
- (void)captureDidOutput:(CGFloat)brightnessValue;
@end

@interface OKQRCodeScanManager : NSObject

@property (nonatomic, weak) id<OKQRCodeScanManagerDelegate> delegate;

@property (nonatomic, readonly) BOOL torchHadOn; // 已经打开了背光灯
- (void)setupSessionOnController:(UIViewController *)currentController;
/** 延迟3秒重新开始开启会话对象扫描 */
- (void)sessionRestartRunning;
/** 开启会话对象扫描 */
- (void)sessionStartRunning;
/** 停止会话对象扫描 */
- (void)sessionStopRunning;
/** 移除 videoPreviewLayer 对象 */
- (void)videoPreviewLayerRemoveFromSuperlayer;
/** 播放音效文件 */
- (void)playSound;
// 从相册读取二维码
- (void)readQRCodeFromPhotoLibraryTo:(UIViewController *)currentController;
// 打开背光灯
- (void)torchOn;
// 关闭背光灯
- (void)torchOff;
// 是否需要打开背光灯
- (BOOL)needTorchOn:(CGFloat)brightnessValue;
@end
