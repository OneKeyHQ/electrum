//
//  OKButton.h
//  OneKey
//
//  Created by xiaoliang on 2020/10/16.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import <UIKit/UIKit.h>
typedef NS_ENUM(NSInteger, OKButtonStatusType) {
    OKButtonStatusEnabled = 0,
    OKButtonStatusDisabled = 1,
};

NS_ASSUME_NONNULL_BEGIN

@interface OKButton : UIButton

- (void)status:(OKButtonStatusType)type;
@end

NS_ASSUME_NONNULL_END
