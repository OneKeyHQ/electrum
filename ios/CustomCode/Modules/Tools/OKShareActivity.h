//
//  OKShareActivity.h
//  OneKey
//
//  Created by umi on 2021/2/28.
//  Copyright © 2021 Onekey. All rights reserved.
//

@import Foundation;
@import UIKit;
#import <LinkPresentation/LPLinkMetadata.h>

@interface OKShareActivity : NSObject<UIActivityItemSource>
@property (nonnull, nonatomic, copy) UIImage* image;
@property (nonnull, nonatomic, copy) NSString* shareTitle;
- (id _Nonnull )initWithImage:(UIImage*_Nonnull)image andShareTitle:(NSString*_Nonnull)shareTitle;
@end
