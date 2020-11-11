//
//  OKKeystoreImportViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/16.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKKeystoreImportViewController : BaseViewController
@property (nonatomic,assign)OKAddType importType;
+ (instancetype)keystoreImportViewController;
@end

NS_ASSUME_NONNULL_END
