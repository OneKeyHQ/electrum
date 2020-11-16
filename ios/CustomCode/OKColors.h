//
//  OKColors.h
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#ifndef OKColors_h
#define OKColors_h

//颜色
// rgb颜色转换（16进制->10进制）
#define HexColor(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

//颜色
// rgb颜色转换（16进制->10进制）
#define HexColorA(rgbValue,aValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:aValue]

/**
 *  2.返回一个RGBA格式的UIColor对象
 */
#define RGBA(r, g, b, a) [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:a]
/**
 *  3.返回一个RGB格式的UIColor对象
 */
#define RGB(r, g, b) RGBA(r, g, b, 1.0f)


#define         UIColorFromRGB(rgbValue) \
[UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

#define         UIColorFromRGBALPHA(RGB, ALPHA) \
[UIColor colorWithRed:((float)((RGB & 0xFF0000) >> 16)) / 255.0 green:((float)((RGB & 0xFF00) >> 8)) / 255.0 blue:((float)(RGB & 0xFF)) / 255.0 alpha:ALPHA]

#define RGB_PARTING_LINE_GRAY       0xEDEDED    // 分割线灰色
#define RGB_PARTING_LINE_BACK       0x142A3B    // 返回箭头颜色
#define RBG_TAB_UNSELECTED_COLOR    0xC8C8C8    // TAB未选中颜色
#define RGB_PARTING_LINE_GRAY       0xEDEDED    // 分割线灰色
#define RGB_USUAL_TEXT_YELLOW       0x1EA1F2    // 常用黄色
#define RGB_USUAL_TEXT_RED          0xF16060    // 常用红色
#define RGB_MAIN_TEXT_BLACK         0x1A1A1A    // 主要字体
#define RGB_MAIN_TEXT_BLACK2        0x333333    // 主要字体
#define RGB_SUB_TEXT_GRAY           0xB3B3B3    // 次要字体
#define RGB_TEXT_RED                0xF85555    // 交易记录红色
#define RGB_TEXT_GRAY               0x656565    // 交易记录灰色
#define RGB_TEXT_ORANGE             0xFFA817    // 橙色
#define RGB_HOME_TEXT_GRAY          0x9A9A9A    // 首页灰色字体
#define RGB_THEME_GREEN             0x00BA12    // 主题绿色
#define RGB_NAVI_BAR_TEXT_BLACK     0x2F2F33    // 导航栏字体颜色
#define RGB_INTERVAL_LINE_GRAY      0xEEEEEE    // 分割线颜色
#define APP_MAIN_TITLE_COLOR        0x3C3F44    // 主字体颜色
#define APP_MAIN_BLACK_COLOR        0x142A3B    // 主题黑色
#endif /* OKColors_h */
