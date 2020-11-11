//
//  OKBackUpTipsViewController.h
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef enum{
    BackUpBtnClickTypeClose,
    BackUpBtnClickTypeBackUp
}BackUpBtnClickType;

typedef void(^BackUpBtnClickBlock)(BackUpBtnClickType type);

NS_ASSUME_NONNULL_BEGIN

@interface OKBackUpTipsViewController : BaseViewController
+ (instancetype)backUpTipsViewController:(BackUpBtnClickBlock)blockClick;
@end

NS_ASSUME_NONNULL_END
