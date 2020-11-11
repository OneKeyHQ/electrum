//
//  OKUnitTableViewCellModel.h
//  OneKey
//
//  Created by bixin on 2020/10/30.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum{
    GroupTypeFait,
    GroupTypeBitcoinUnit,
    GroupTypeBitcoinETH
}GroupType;

NS_ASSUME_NONNULL_BEGIN

@interface OKUnitTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* titleStr;
@property (nonatomic,assign) BOOL isSelected;
@property (nonatomic,copy) NSString* descStr;
@property (nonatomic,copy) NSString* typeString;
@property (nonatomic,assign)GroupType type;
@end

NS_ASSUME_NONNULL_END
