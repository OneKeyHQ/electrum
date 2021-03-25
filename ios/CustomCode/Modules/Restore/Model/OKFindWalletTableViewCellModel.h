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
@property (nonatomic,copy) NSString* blance;
@property (nonatomic,copy) NSString* name;
@property (nonatomic,copy) NSString* label;
@property (nonatomic,copy) NSString* coin;
@property (nonatomic,copy) NSString* coin_type;
@property (nonatomic,assign)BOOL isSelected;
@property (nonatomic,assign)BOOL exist;
@end

NS_ASSUME_NONNULL_END
