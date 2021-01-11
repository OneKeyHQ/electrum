//
//  OKVerifySignatureResultController.h
//  OneKey
//
//  Created by xiaoliang on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

typedef enum{
    OKVerifySignatureTypeSuccess,
    OKVerifySignatureTypeFailure
} OKVerifySignatureType;

#import "BaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface OKVerifySignatureResultController : BaseViewController

@property (nonatomic,assign)OKVerifySignatureType type;

@end

NS_ASSUME_NONNULL_END
