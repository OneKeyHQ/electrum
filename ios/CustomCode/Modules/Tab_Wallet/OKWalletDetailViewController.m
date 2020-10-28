//
//  OKWalletDetailViewController.m
//  OneKey
//
//  Created by bixin on 2020/10/15.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWalletDetailViewController.h"

@interface OKWalletDetailViewController ()

@end

@implementation OKWalletDetailViewController

+ (instancetype)walletDetailViewController
{
    return [[UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil]instantiateViewControllerWithIdentifier:@"OKWalletDetailViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setNavigationBarBackgroundColorWithClearColor];
    [self stupUI];
    // Do any additional setup after loading the view.
}

- (void)stupUI
{
    self.title = MyLocalizedString(@"Wallet Detail", nil);
    
}

@end
