//
//  OKCheckWordView.h
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class OKCheckWordView;
@protocol   OKCheckWordViewDelegate <NSObject>

@optional
- (void)checkWordView:(OKCheckWordView *)checkView delegateBtnClick:(NSString *)word;

@end


@interface OKCheckWordView : UIView
- (void)configureData:(NSArray *)data delegate:(id)delegate;
- (void)clearStatus;
@end

NS_ASSUME_NONNULL_END
