//
//  OKDeleteWalletTipsViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/12.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN


@interface OKDeleteWalletTipsViewController : BaseViewController
@property (nonatomic,copy)NSString *walletName;
@property (nonatomic,assign)OKWhereToDeleteType deleteType;
+ (instancetype)deleteWalletTipsViewController;
@end

NS_ASSUME_NONNULL_END
