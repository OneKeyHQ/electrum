//
//  BaseTableViewController.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/30.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface BaseTableViewController : UITableViewController
@property (nonatomic) BOOL isRoot;

+ (__kindof BaseTableViewController *)initWithStoryboardName:(NSString *)storyboardName identifier:(NSString *)identifier;

+ (__kindof BaseTableViewController *)initViewControllerWithStoryboardName:(NSString *)name;

/**
 *将TableView多余的cell隐藏
 */
- (void)AT_setExtraCellHide:(UITableView *)aTableView;

- (void)backToPrevious;

- (void)setNavigationBarBackgroundColorWithClearColor;
@end

NS_ASSUME_NONNULL_END
