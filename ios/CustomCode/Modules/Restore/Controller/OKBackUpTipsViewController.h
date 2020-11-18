//
//  OKBackUpTipsViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
typedef enum{
    BackUpBtnClickTypeClose,
    BackUpBtnClickTypeBackUp
}BackUpBtnClickType;

typedef void(^BackUpBtnClickBlock)(BackUpBtnClickType type);

NS_ASSUME_NONNULL_BEGIN

@interface OKBackUpTipsViewController : BaseViewController
@property (nonatomic,copy)NSString *pwd;
+ (instancetype)backUpTipsViewController:(BackUpBtnClickBlock)blockClick;
@end

NS_ASSUME_NONNULL_END
