//
//  OKPINInputMethodCell.h
//  OneKey
//
//  Created by zj on 2021/2/2.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKPINInputMethodCellModel : NSObject
@property (nonatomic,copy) NSString* titleStr;
@property (nonatomic,assign) BOOL isSelected;
@property (nonatomic,assign) NSInteger type;
@end

@interface OKPINInputMethodCell : UITableViewCell
@property (nonatomic,strong)OKPINInputMethodCellModel *model;
@end

NS_ASSUME_NONNULL_END
