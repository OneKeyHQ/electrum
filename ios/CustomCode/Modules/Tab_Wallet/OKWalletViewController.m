//
//  OKWalletViewController.m
//  Electron-Cash
//
//  Created by bixin on 2020/9/28.
//  Copyright © 2020 Calin Culianu. All rights reserved.
//

#import "OKWalletViewController.h"
#import <OKPythonExecute.h>
#import "OKSelectCellModel.h"
#import "OKSelectTableViewCell.h"
#import "OKCreateHDCell.h"
#import "OKFirstUseViewController.h"
#import "OKPwdViewController.h"
#import "OKWordImportVC.h"


@interface OKWalletViewController ()<UITableViewDelegate,UITableViewDataSource,UINavigationControllerDelegate>
@property (weak, nonatomic) IBOutlet UIView *topView;
@property (weak, nonatomic) IBOutlet UIView *topLeftBgView;
@property (weak, nonatomic) IBOutlet UIView *bottomView;
@property (weak, nonatomic) IBOutlet UIButton *coinImage;
@property (weak, nonatomic) IBOutlet UILabel *walletName;
@property (weak, nonatomic) IBOutlet UITableView *selectCreateTableView;
@property (weak, nonatomic) IBOutlet UIButton *scanBtn;

@property (nonatomic,strong) OKPythonExecute * execute;
@property (nonatomic,strong) OKPythonExecute * execute1;

@property (weak, nonatomic) IBOutlet UIView *leftView;
@property (weak, nonatomic) IBOutlet UIView *leftViewBg;

@property (nonatomic,strong)NSArray *allData;
@end

@implementation OKWalletViewController

+ (instancetype)walletViewController
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Tab_Wallet" bundle:nil];
    return [sb instantiateViewControllerWithIdentifier:@"OKWalletViewController"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self stupUI];
    //[self showFirstUse];
}


- (void)stupUI
{
    [self.topView setLayerRadius:20];
    [self.bottomView setLayerRadius:20];
    [self.leftViewBg setLayerRadius:14];
    [self.scanBtn addTarget:self action:@selector(scanBtnClick) forControlEvents:UIControlEventTouchUpInside];
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapGestureClick)];
    [self.leftView addGestureRecognizer:tapGesture];
    self.navigationController.delegate = self;
}

- (void)showFirstUse
{
    OKFirstUseViewController *firstUseVc = [OKFirstUseViewController firstUseViewController];
    BaseNavigationController *navVc = [[BaseNavigationController alloc]initWithRootViewController:firstUseVc];
    navVc.modalPresentationStyle = UIModalPresentationFullScreen;
    [self.navigationController presentViewController:navVc animated:NO completion:nil];
}


#pragma mark - 切换钱包
- (void)tapGestureClick
{
    
//    PyGILState_STATE state =  PyGILState_Ensure();
//
//    PyObject *pModule = PyImport_ImportModule([@"onekey.testxx" UTF8String]);//导入模块
//
//    PyObject *pyClass = PyObject_GetAttrString(pModule, [@"OCToPY" UTF8String]);//获取类
//
//    PyObject *pyInstance = PyInstanceMethod_New(pyClass); //创建实例
//
//    NSMutableDictionary *params = [NSMutableDictionary new];
//    [params setObject:@"123" forKey:@"access_key"];
//    [params setObject:@"456" forKey:@"secret_key"];
//    [params setObject:@"jake" forKey:@"bucket_name"];
//    [params setObject:@"pic" forKey:@"file_name"];
//
//    NSError *error = nil;
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:params options:NSJSONWritingPrettyPrinted error:&error];
//    NSString *paramterJsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    //PyObject_CallMethod(self.pyInstance, [methodName UTF8String], "(N,s)",self.pyClass,[paramterJsonString UTF8String]);
//    PyObject *result = NULL;
//    result = PyObject_CallMethod(pyInstance, [@"testoctopy" UTF8String], "(N,s)",pyClass, [paramterJsonString UTF8String] );
//
//    char * resultCString = NULL;
//    PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
//
//    PyGILState_Release(state);
//
//    NSLog(@"%s", resultCString);
    
    
    NSLog(@"切换钱包");
        OKPythonExecute *pythonExecute = [[OKPythonExecute alloc] initWithModuleDirName:@"electrum" moduleName:@"bip32"];
        _execute1 = pythonExecute;
        NSDictionary *params = @{@"kkkk":@"helo"};
        [pythonExecute executeWithClass:@"OCToPY" methodName:@"testoctopy" parameter:params success:^(id result) {

            NSLog(@"===resutl = %@",result);
        } fail:^(NSError *error) {
            NSLog(@"====error = %@",error.domain);
        }];
}
- (void)scanBtnClick
{
        OKPythonExecute *pythonExecute = [[OKPythonExecute alloc] initWithModuleDirName:@"onekey" moduleName:@"testbx"];
        _execute = pythonExecute;
    
        NSDictionary *params = @{@"xxx":@"hello"};
        [pythonExecute executeWithClass:@"TokenForiOS" methodName:@"createtoken" parameter:params success:^(id result) {
    
            NSLog(@"===resutl = %@",result);
        } fail:^(NSError *error) {
            NSLog(@"====error = %@",error.domain);
        }];

    NSLog(@"扫描二维码");
}
#pragma mark - selectCreateTableView
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return  self.allData.count;
    
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        static NSString *ID = @"OKCreateHDCell";
        OKCreateHDCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        if (cell == nil) {
            cell = [[OKCreateHDCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        }
        cell.model = [self.allData firstObject];
        return cell;
    }else{
        static NSString *ID = @"OKSelectTableViewCell";
        OKSelectTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
        if (cell == nil) {
            cell = [[OKSelectTableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
        }
        OKSelectCellModel *model = self.allData[indexPath.row];
        cell.model = model;
        return cell;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    OKWeakSelf(self)
    OKSelectCellModel *model = self.allData[indexPath.row];
    if (model.type == OKSelectCellTypeCreateHD) { //创建
        OKPwdViewController *pwdVc = [OKPwdViewController pwdViewController];
        BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:pwdVc];
        [weakself.view.window.rootViewController presentViewController:baseVc animated:YES completion:nil];
    }else if (model.type == OKSelectCellTypeRestoreHD){ //恢复
        OKWordImportVC *wordImport = [OKWordImportVC initViewController];
        BaseNavigationController *baseVc = [[BaseNavigationController alloc]initWithRootViewController:wordImport];
        [weakself.view.window.rootViewController presentViewController:baseVc animated:YES completion:nil];
    }else if (model.type == OKSelectCellTypeMatchHD){ //匹配硬件
        NSLog(@"匹配硬件钱包");
        
    }
}

- (IBAction)testClick:(UIButton *)sender {
    
    PyGILState_STATE state =  PyGILState_Ensure();
    PyObject *pModule = PyImport_ImportModule([@"electroncash.testbx" UTF8String]);//导入模块
    PyObject *pyClass = PyObject_GetAttrString(pModule, [@"TokenForiOS" UTF8String]);//获取类
    PyObject *pyInstance = PyInstanceMethod_New(pyClass); //创建实例
    NSMutableDictionary *params = [NSMutableDictionary new];
    [params setObject:@"123" forKey:@"access_key"];
    [params setObject:@"456" forKey:@"secret_key"];
    [params setObject:@"jake" forKey:@"bucket_name"];
    [params setObject:@"pic" forKey:@"file_name"];
        
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:params options:NSJSONWritingPrettyPrinted error:&error];
    NSString *paramterJsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    //PyObject_CallMethod(self.pyInstance, [methodName UTF8String], "(N,s)",self.pyClass,[paramterJsonString UTF8String]);
    PyObject *result = NULL;
    result = PyObject_CallMethod(pyInstance, [@"createtoken" UTF8String], "(N,s)",pyClass, [paramterJsonString UTF8String] );

    char * resultCString = NULL;
    PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
     
    PyGILState_Release(state);
    
    NSLog(@"%s", resultCString);
    
    
//    OKPythonExecute *pythonExecute = [[OKPythonExecute alloc] initWithModuleDirName:@"electroncash" moduleName:@"testbx"];
//    _execute = pythonExecute;
//
//    NSDictionary *params = @{@"xxx":@"hello"};
//    [pythonExecute executeWithClass:@"TokenForiOS" methodName:@"createtoken" parameter:params success:^(id result) {
//
//        NSLog(@"===resutl = %@",result);
//    } fail:^(NSError *error) {
//        NSLog(@"====error = %@",error.domain);
//    }];
}
- (IBAction)test:(UIButton *)sender {
    
    PyGILState_STATE state =  PyGILState_Ensure();
    
    PyObject *pModule = PyImport_ImportModule([@"electroncash.testxx" UTF8String]);//导入模块
        
    PyObject *pyClass = PyObject_GetAttrString(pModule, [@"OCToPY" UTF8String]);//获取类
        
    PyObject *pyInstance = PyInstanceMethod_New(pyClass); //创建实例
    
    NSMutableDictionary *params = [NSMutableDictionary new];
    [params setObject:@"123" forKey:@"access_key"];
    [params setObject:@"456" forKey:@"secret_key"];
    [params setObject:@"jake" forKey:@"bucket_name"];
    [params setObject:@"pic" forKey:@"file_name"];
        
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:params options:NSJSONWritingPrettyPrinted error:&error];
    NSString *paramterJsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    //PyObject_CallMethod(self.pyInstance, [methodName UTF8String], "(N,s)",self.pyClass,[paramterJsonString UTF8String]);
    PyObject *result = NULL;
    result = PyObject_CallMethod(pyInstance, [@"testoctopy" UTF8String], "(N,s)",pyClass, [paramterJsonString UTF8String] );

    char * resultCString = NULL;
    PyArg_Parse(result, "s", &resultCString); //将python类型的返回值转换为c
     
    PyGILState_Release(state);
    
    NSLog(@"%s", resultCString);

    
    
//    if ([_execute1 isRuning]) {
//        return;
//    }
//    OKPythonExecute *pythonExecute = [[OKPythonExecute alloc] initWithModuleDirName:@"electroncash" moduleName:@"testxx"];
//    _execute1 = pythonExecute;
//    NSDictionary *params = @{@"kkkk":@"helo"};
//    [pythonExecute executeWithClass:@"OCToPY" methodName:@"testoctopy" parameter:params success:^(id result) {
//
//        NSLog(@"===resutl = %@",result);
//    } fail:^(NSError *error) {
//        NSLog(@"====error = %@",error.domain);
//    }];
}

- (NSArray *)allData
{
    if (!_allData) {
        _allData = [NSArray array];
        OKSelectCellModel *model1 = [OKSelectCellModel new];
        model1.titleStr = MyLocalizedString(@"Create HD Wallet", nil);
        model1.descStr = MyLocalizedString(@"Completely free and unlimited quantity", nil);
        model1.imageStr = @"add";
        model1.descStrL = MyLocalizedString(@"It takes just a few minutes to create a wallet for free and quickly, and then you're free to send and receive assets, make transactions, and explore the blockchain world", nil);
        model1.type = OKSelectCellTypeCreateHD;
        
        OKSelectCellModel *model2 = [OKSelectCellModel new];
        model2.titleStr = MyLocalizedString(@"Recover HD Wallet", nil);
        model2.descStr = MyLocalizedString(@"Recovery by mnemonic", nil);
        model2.imageStr = @"import";
        model2.descStrL = @"";
        model2.type = OKSelectCellTypeRestoreHD;
        
        OKSelectCellModel *model3 = [OKSelectCellModel new];
        model3.titleStr = MyLocalizedString(@"Paired hardware wallet", nil);
        model3.descStr = MyLocalizedString(@"Support BixinKey", nil);
        model3.imageStr = @"match_hardware";
        model3.descStrL = @"";
        model3.type = OKSelectCellTypeMatchHD;
        
        _allData = @[model1,model2,model3];
    }
    return  _allData;
}



#pragma mark - UINavigationControllerDelegate
// 将要显示控制器
- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    BOOL isShowHomePage = [viewController isKindOfClass:[self class]];
    [self.navigationController setNavigationBarHidden:isShowHomePage animated:YES];
}
@end
