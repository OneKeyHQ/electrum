//
//  NSTimer+TimerBlock.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSTimer (TimerBlock)

/**
 *  timer执行
 *
 *  @param ti         执行的频率,按秒计算
 *  @param timerBlock 执行的操作
 *
 *  @return timer
 */
+ (NSTimer *)scheduledTimerWithTimeInterval:(NSTimeInterval)ti TimerDo:(void(^)())timerBlock;

+ (dispatch_source_t)scheduledGCDTimerWithInterval:(NSTimeInterval)ti eventHandler:(void (^)())event_handler cancelHandler:(void(^)())cancel_handler;

- (void)resume; // 开始
- (void)standBy; // 暂停
- (void)stop; // 停止

@end
