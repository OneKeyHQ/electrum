//
//  OKWalletDetailTableViewCell.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/29.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>


NS_ASSUME_NONNULL_BEGIN
@class OKWalletDetailModel;
@interface OKWalletDetailTableViewCell : UITableViewCell
@property (nonatomic,strong) OKWalletDetailModel* model;
@end

NS_ASSUME_NONNULL_END
