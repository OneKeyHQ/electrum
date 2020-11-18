//
//  OKFindWalletTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKFindWalletTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* iconName;
@property (nonatomic,copy) NSString* walletName;
@property (nonatomic,copy) NSString* balanceName;
@end

NS_ASSUME_NONNULL_END
