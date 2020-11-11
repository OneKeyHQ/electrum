//
//  OKTextField.h
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol OKDeleteTextFieldDeleteProtocol <NSObject>
-(void)deleteBackward;
-(void)AfterDeleteBackward;
@end

@interface OKDeleteTextField : UITextField
@property (nonatomic,assign)BOOL mark;
@property (weak) id<OKDeleteTextFieldDeleteProtocol>deleteDelegate;

@end

NS_ASSUME_NONNULL_END
