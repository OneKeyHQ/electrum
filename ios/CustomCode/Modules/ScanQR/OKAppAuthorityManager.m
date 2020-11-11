//
//  OKAppAuthorityManager.m
//  OneKey
//
//  CreOKed by bixin on 2020/9/28.
//

#import "OKAppAuthorityManager.h"
#import <AVFoundation/AVFoundation.h>
#import <AssetsLibrary/AssetsLibrary.h>

@implementation OKAppAuthorityManager
static dispatch_once_t once;
+ (OKAppAuthorityManager *)sharedInstance {
    static OKAppAuthorityManager *_sharedInstance = nil;
    dispatch_once(&once, ^{
        _sharedInstance = [[OKAppAuthorityManager alloc] init];
    });
    return _sharedInstance;
}

+ (void)clear {
    once = 0;
}

#pragma mark - 判断相机或照片权限

+ (BOOL)canUseCamera {
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authStatus == AVAuthorizationStatusDenied) {
        return NO;
    } else {
        return YES;
    }
}

+ (BOOL)canReadPhotos {
    ALAuthorizationStatus authStatus = [ALAssetsLibrary authorizationStatus];
    if (authStatus == AVAuthorizationStatusDenied) {
        return NO;
    } else {
        return YES;
    }
}

@end
