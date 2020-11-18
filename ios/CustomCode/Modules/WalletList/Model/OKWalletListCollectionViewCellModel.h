//
//  OKWalletListCollectionViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/15.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWalletListCollectionViewCellModel : NSObject
@property (nonatomic,copy) NSString* iconName;
@property (nonatomic,copy) NSString* coinType;
@property (nonatomic,assign)BOOL isSelected;
@property (nonatomic,copy) NSString *headerWaletType;
@end

NS_ASSUME_NONNULL_END
