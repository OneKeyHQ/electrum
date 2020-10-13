//
//  OKTextField.h
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol OKTextFieldDeleteProtocol <NSObject>
-(void)deleteBackward;
-(void)AfterDeleteBackward;
@end

@interface OKTextField : UITextField

@property (weak) id<OKTextFieldDeleteProtocol>deleteDelegate;

@end

NS_ASSUME_NONNULL_END
