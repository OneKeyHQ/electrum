//
//  OKPrivateKeyExportViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

//导出类型
typedef enum {
    OKExportTypePrivate,            //导出私钥
    OKExportTypeKeyStore           //导出keyStore
}OKExportType;

NS_ASSUME_NONNULL_BEGIN

@interface OKPrivateKeyExportViewController : BaseViewController
@property (nonatomic,copy)NSString *keyStr;
@property (nonatomic,assign)OKExportType exportType;
+ (instancetype)privateKeyExportViewController;
@end

NS_ASSUME_NONNULL_END
