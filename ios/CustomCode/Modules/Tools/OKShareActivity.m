//
//  OKShareActivity.m
//  OneKey
//
//  Created by umi on 2021/2/28.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import "OKShareActivity.h"

@implementation OKShareActivity

- (id _Nonnull )initWithImage:(UIImage*_Nonnull)image andShareTitle:(NSString*_Nonnull)shareTitle {
    self = [super init];
    if (self) {
        self.image = image;
        self.shareTitle = shareTitle;
    }
    return self;
}


- (nullable id)activityViewController:(nonnull UIActivityViewController *)activityViewController itemForActivityType:(nullable UIActivityType)activityType {
    return self.image;
}

- (nonnull id)activityViewControllerPlaceholderItem:(nonnull UIActivityViewController *)activityViewController {
    return self.image;
}
- (UIImage *)activityViewController:(UIActivityViewController *)activityViewController thumbnailImageForActivityType:(UIActivityType)activityType suggestedSize:(CGSize)size{
    return self.image;
}

- (LPLinkMetadata *)activityViewControllerLinkMetadata:(UIActivityViewController *)activityViewController API_AVAILABLE(ios(13.0)){
    LPLinkMetadata* metaData = [[LPLinkMetadata alloc]init];
    metaData.title = self.shareTitle;
    metaData.imageProvider = [[NSItemProvider alloc]initWithObject:self.image];
    metaData.originalURL = [[NSURL alloc]initFileURLWithPath:@"OneKey"];
    return metaData;
}

@end
