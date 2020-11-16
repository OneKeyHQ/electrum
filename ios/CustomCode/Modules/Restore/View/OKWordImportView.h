//
//  OKWordImportView.h
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKWordImportView : UIView

@property (nonatomic, strong, readonly) NSArray *wordsArr;
@property (nonatomic, copy) void(^completed)(BOOL isCompleted);

- (void)configureData:(NSArray <NSString *> *)data;

@end

NS_ASSUME_NONNULL_END
