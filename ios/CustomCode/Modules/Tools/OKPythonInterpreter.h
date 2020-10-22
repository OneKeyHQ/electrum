//
//  OKPythonInterpreter.h
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Python.h>
NS_ASSUME_NONNULL_BEGIN

@interface OKPythonInterpreter : NSObject
@property (nonatomic,assign) BOOL running;
- (void)beginTask:(nonnull void (^)(void))task completion:(nullable void (^)(void))completion;
@end

NS_ASSUME_NONNULL_END
