//
//  OKMineTableViewCellModel.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/20.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKMineTableViewCellModel : NSObject
@property (nonatomic,copy) NSString* imageName;
@property (nonatomic,copy) NSString* menuName;
@property (nonatomic,assign) BOOL isSwitch;
@property (nonatomic,assign) BOOL isAuth;
@end

NS_ASSUME_NONNULL_END
