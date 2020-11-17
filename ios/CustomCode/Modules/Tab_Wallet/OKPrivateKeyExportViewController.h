//
//  OKPrivateKeyExportViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/17.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKPrivateKeyExportViewController : BaseViewController
@property (nonatomic,copy)NSString *privateKey;
+ (instancetype)privateKeyExportViewController;
@end

NS_ASSUME_NONNULL_END
