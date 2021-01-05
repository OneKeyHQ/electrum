//
//  NSTimer+TimerBlock.m
//  Electron-Cash
//
//  Created by xiaoliang on 2020/9/28.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "NSTimer+TimerBlock.h"

@implementation NSTimer (TimerBlock)

+ (NSTimer *)scheduledTimerWithTimeInterval:(NSTimeInterval)ti TimerDo:(void (^)())timerBlock {
    NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:ti target:self selector:@selector(timerBlcokDo:) userInfo:[timerBlock copy] repeats:YES];
    return timer;
}

+ (void)timerBlcokDo:(NSTimer *)timer {
    void(^timerBlock)() = timer.userInfo;
    if (timerBlock) {
         timerBlock();
    }
}

+ (dispatch_source_t)scheduledGCDTimerWithInterval:(NSTimeInterval)ti eventHandler:(void (^)())event_handler cancelHandler:(void(^)())cancel_handler {
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_source_t timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue);
    dispatch_source_set_timer(timer, DISPATCH_TIME_NOW, ti * NSEC_PER_SEC, 0 * NSEC_PER_SEC);
    dispatch_source_set_event_handler(timer, [event_handler copy]);
    dispatch_source_set_cancel_handler(timer, [cancel_handler copy]);
    return timer;
}

- (void)resume {
    if (self && self.valid) {
        [self setFireDate:[NSDate date]];
    }
}

- (void)standBy {
    if (self && self.valid) {
        [self setFireDate:[NSDate distantFuture]];
    }
}

- (void)stop {
    if (self && self.valid) {
        [self invalidate];
    }
}

@end
