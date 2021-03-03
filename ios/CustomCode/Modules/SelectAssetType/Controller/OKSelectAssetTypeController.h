//
//  OKSelectAssetTypeController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"


NS_ASSUME_NONNULL_BEGIN

@interface OKSelectAssetTypeController : BaseViewController
@property (nonatomic,copy)NSString *pwd;
@property (nonatomic,assign)BOOL isInit;
+ (instancetype)selectAssetTypeController;

@end

NS_ASSUME_NONNULL_END
