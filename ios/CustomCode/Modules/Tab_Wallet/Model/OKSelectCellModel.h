//
//  OKSelectCellModel.h
//  Electron-Cash
//
//  Created by bixin on 2020/10/9.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
typedef enum {
    OKSelectCellTypeCreateHD,  //创建钱包
    OKSelectCellTypeRestoreHD, //恢复钱包
    OKSelectCellTypeMatchHD    //配对硬件钱包
    
}OKSelectCellType;

@interface OKSelectCellModel : NSObject
@property (nonatomic,copy) NSString* descStr;
@property (nonatomic,copy) NSString* titleStr;
@property (nonatomic,copy) NSString* imageStr;
@property (nonatomic,copy) NSString* descStrL;
@property (nonatomic,assign)OKSelectCellType type;
@end
NS_ASSUME_NONNULL_END
