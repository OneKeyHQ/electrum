//
//  OKFindFollowingWalletController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"
#import "OKCreateResultModel.h"
#import "OKCreateResultWalletInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKFindFollowingWalletController : BaseViewController
@property (nonatomic,strong)OKCreateResultModel *createResultModel;
@property (nonatomic,copy)NSString *pwd;
+ (instancetype)findFollowingWalletController;

@end

NS_ASSUME_NONNULL_END
