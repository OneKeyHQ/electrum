//
//  OKSystemShareView.m
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKSystemShareView.h"

@implementation OKSystemShareView

+(UIActivityViewController *)showSystemShareViewWithActivityItems:(NSArray *)items parentVc:(UIViewController *)vc cancelBlock:(cancelBlock)cancelBlock shareCompletionBlock:(shareCompletionBlock)shareCompletion;
{
    UIActivityViewController *activityVC = [[UIActivityViewController alloc] initWithActivityItems:items applicationActivities:nil];
    if ([UIDevice currentDevice].systemVersion.floatValue >= 8.0) {
        UIActivityViewControllerCompletionWithItemsHandler itemsBlock = ^(UIActivityType __nullable activityType, BOOL completed, NSArray * __nullable returnedItems, NSError * __nullable activityError){
            [vc dismissViewControllerAnimated:YES completion:nil];
            
            if (completed) {
                if (shareCompletion) {
                    shareCompletion();
                }
            }else{
                if (cancelBlock) {
                    cancelBlock();
                }
            }
        };
        activityVC.completionWithItemsHandler = itemsBlock;
    }else{
        UIActivityViewControllerCompletionHandler handlerBlock = ^(UIActivityType __nullable activityType, BOOL completed){
            [vc dismissViewControllerAnimated:YES completion:nil];
            if (completed) {
                if (shareCompletion) {
                    shareCompletion();
                }
            }else{
                if (cancelBlock) {
                    cancelBlock();
                }
            }
        };
        activityVC.completionHandler = handlerBlock;
    }
    
    [vc presentViewController:activityVC animated:YES completion:nil];
    
    return activityVC;
}

@end
