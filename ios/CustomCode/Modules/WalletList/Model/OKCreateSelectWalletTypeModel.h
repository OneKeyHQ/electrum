//
//  OKCreateSelectWalletTypeModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKCreateSelectWalletTypeModel : NSObject
@property (nonatomic,copy) NSString* iconName;
@property (nonatomic,copy) NSString* createWalletType;
@property (nonatomic,copy) NSString* tipsString;
@property (nonatomic,assign)OKAddType addtype;
@end

NS_ASSUME_NONNULL_END
