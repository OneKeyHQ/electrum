//
//  OKWalletListTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletListTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* walletName;
@property (nonatomic,copy) NSString* walletType;
@property (nonatomic,copy) NSString* walletTypeShowStr;
@property (nonatomic,copy) NSString* address;
@property (nonatomic,copy) NSString* iconName;
@property (nonatomic,copy) UIColor* backColor;
@property (nonatomic,assign)BOOL isCurrent;
+ (UIColor *)getBackColor:(NSString *)type;
+ (NSString *)getBgImageName:(NSString *)type;
@end

NS_ASSUME_NONNULL_END
