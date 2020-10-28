//
//  OKSelectCoinTypeViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum {
    OKAddTypeCreateHD,
    OKAddTypeCreateSolo,
    OKAddTypeImport
    
}OKAddType;

NS_ASSUME_NONNULL_BEGIN

@interface OKSelectCoinTypeViewController : BaseViewController

+ (instancetype)selectCoinTypeViewController;

//添加类型
@property (nonatomic,assign)OKAddType addType;

@end

NS_ASSUME_NONNULL_END
