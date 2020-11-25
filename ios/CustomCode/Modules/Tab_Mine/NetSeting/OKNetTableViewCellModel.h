//
//  OKNetTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/25.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
    OKNetTableViewCellModelTypeB,
    OKNetTableViewCellModelTypeM
}OKNetTableViewCellModelType;

NS_ASSUME_NONNULL_BEGIN

@interface OKNetTableViewCellModel : NSObject
@property (nonatomic,copy) NSString * titleStr;
@property (nonatomic,assign) OKNetTableViewCellModelType  type;
@end

NS_ASSUME_NONNULL_END
