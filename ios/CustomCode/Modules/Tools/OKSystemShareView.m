//
//  OKSystemShareView.m
//  OneKey
//
//  Created by xiaoliang on 2020/10/19.
//  Copyright © 2020 OneKey. All rights reserved..
//

#import "OKSystemShareView.h"

@implementation OKSystemShareView

+(UIActivityViewController *)showSystemShareViewWithActivityItems:(NSArray *)items parentVc:(UIViewController *)vc cancelBlock:(cancelBlock)cancelBlock shareCompletionBlock:(shareCompletionBlock)shareCompletion;
{
    UIActivityViewController *activityVC = [[UIActivityViewController alloc] initWithActivityItems:items applicationActivities:nil];
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
    [vc presentViewController:activityVC animated:YES completion:nil];
    return activityVC;
}

@end
