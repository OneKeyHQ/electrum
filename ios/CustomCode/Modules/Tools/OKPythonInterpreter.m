//
//  OKPythonInterpreter.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKPythonInterpreter.h"

@interface OKPythonInterpreter()
@property (nonatomic,assign) dispatch_queue_t pythonQueue;

@end

@implementation OKPythonInterpreter
- (instancetype)init
{
    self = [super init];
    if (self) {

    }
    return self;
}

- (void)dealloc{
    [self close];
}

- (void)beginTask:(nonnull void (^)(void))task completion:(nullable void (^)(void))completion
{
    OKWeakSelf(self)
    dispatch_async(self.pythonQueue, ^{
        weakself.running = YES;
        task();
          __weak typeof(self) weakSelf = self;
        if (completion){
            dispatch_async(dispatch_get_main_queue(), ^{
                weakSelf.running = NO;
                completion();
            });
        }
    });
}


- (void)close
{
    self.running = NO;
}
#pragma mark - lazy
- (dispatch_queue_t)pythonQueue {
    if (_pythonQueue== nil) {
        _pythonQueue = dispatch_get_global_queue(0, 0);
    }
    return _pythonQueue;
}
@end
