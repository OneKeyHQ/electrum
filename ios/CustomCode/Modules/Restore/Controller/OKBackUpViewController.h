//
//  OKBackUpViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN
typedef enum {
    WordsShowTypeRestore,
    WordsShowTypeExport
}WordsShowType;

@interface OKBackUpViewController : BaseViewController
@property (nonatomic,strong)NSArray *words;
@property (nonatomic,assign)WordsShowType showType;
+ (instancetype)backUpViewController;
@end

NS_ASSUME_NONNULL_END
