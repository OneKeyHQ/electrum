//
//  OKSystemShareView.h
//  OneKey
//
//  Created by bixin on 2020/10/19.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>


typedef void(^cancelBlock)(void);
typedef void(^shareCompletionBlock)(void);

NS_ASSUME_NONNULL_BEGIN

@interface OKSystemShareView : NSObject

+(UIActivityViewController *)showSystemShareViewWithActivityItems:(NSArray *)items parentVc:(UIViewController *)vc cancelBlock:(cancelBlock)cancelBlock shareCompletionBlock:(shareCompletionBlock)shareCompletion;

@end

NS_ASSUME_NONNULL_END
