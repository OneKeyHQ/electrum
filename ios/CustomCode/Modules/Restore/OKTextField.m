//
//  OKTextField.h
//  OneKey
//
//  Created by bixin on 2020/9/28.
//


#import "OKTextField.h"

@implementation OKTextField

-(void)deleteBackward {
    NSInteger length = self.text.length;

    if ([_deleteDelegate respondsToSelector:@selector(deleteBackward)]) {
        [_deleteDelegate deleteBackward];
    }
    
    if (length) {
        [super deleteBackward];
        if ([_deleteDelegate respondsToSelector:@selector(AfterDeleteBackward)]) {
            [_deleteDelegate AfterDeleteBackward];
        }
    }
}

@end
