//
//  UITableView+OKRoundSection.h
//  OneKey
//
//  Created by zj on 2021/3/10.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UITableView (OKRoundSection)
// 在 tableView:willDisplayCell:forRowAtIndexPath:; 中使用
+ (void)roundSectiontableView:(UITableView *)tableView
              willDisplayCell:(UITableViewCell *)cell
            forRowAtIndexPath:(NSIndexPath *)indexPath
                 cornerRadius:(CGFloat)cornerRadius;
@end

NS_ASSUME_NONNULL_END
