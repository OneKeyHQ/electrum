//
//  OKTxViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKAssetTableViewCellModel;
@interface OKTxViewController : UIViewController
@property (nonatomic,copy)NSString *searchType;
@property (nonatomic,strong)OKAssetTableViewCellModel *assetTableViewCellModel;
@property (nonatomic,copy)NSString *coinType;
+ (instancetype)txViewController;
@end

NS_ASSUME_NONNULL_END
