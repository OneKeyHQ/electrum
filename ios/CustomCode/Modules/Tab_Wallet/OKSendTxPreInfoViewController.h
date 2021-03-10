//
//  OKSendTxPreInfoViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/12/8.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import <UIKit/UIKit.h>
@class OKSendTxPreModel;
NS_ASSUME_NONNULL_BEGIN
typedef void(^BtnClickBlock)(NSString* str);
typedef void(^CancleClickBlock)(void);
@interface OKSendTxPreInfoViewController : BaseViewController
@property (nonatomic,strong)OKSendTxPreModel *info;
- (void)showOnWindowWithParentViewController:(UIViewController *)viewController block:(BtnClickBlock)block cancle:(CancleClickBlock)cancleBlock;
- (void)closeView;
@end

NS_ASSUME_NONNULL_END
