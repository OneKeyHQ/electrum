//
//  OKBackUpViewController.m
//  OneKey
//
//  Created by xiaoliang on 2020/11/7.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKBackUpViewController.h"
#import "OKWordImportView.h"
#import "OKWordCheckViewController.h"

@interface OKBackUpViewController ()

@property (weak, nonatomic) IBOutlet UIButton *nextBtn;

@property (weak, nonatomic) IBOutlet OKWordImportView *wordInputView;

@end

@implementation OKBackUpViewController

+ (instancetype)backUpViewController
{
    OKBackUpViewController *backupVc =  [[UIStoryboard storyboardWithName:@"importWords" bundle:nil]instantiateViewControllerWithIdentifier:@"OKBackUpViewController"];
    return backupVc;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = MyLocalizedString(@"Backup the purse", nil);
    self.wordInputView.userInteractionEnabled = NO;
    [self.wordInputView configureData:self.words];
}

- (IBAction)next:(id)sender {
    OKWordCheckViewController *wordVc = [OKWordCheckViewController wordCheckViewController];
    wordVc.words = self.words;
    [self.navigationController pushViewController:wordVc animated:YES];
}
@end
