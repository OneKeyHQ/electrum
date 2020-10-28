//
//  OKWalletListTableViewCellModel.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletListTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* walletName;
@property (nonatomic,copy) NSString* walletType;
@property (nonatomic,copy) NSString* address;
@property (nonatomic,copy) NSString* iconName;
@property (nonatomic,copy) UIColor* backColor;
@end

NS_ASSUME_NONNULL_END
