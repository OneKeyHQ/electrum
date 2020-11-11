//
//  AppDelegate.m
//  OneKey
//
//  Created by bixin on 2020/9/28.
//

#import "MainViewController.h"
#import "AppDelegate.h"
#import <CoreBluetooth/CoreBluetooth.h>
#import "OKFirstUseViewController.h"

@interface AppDelegate ()
@property (nonatomic,strong)MainViewController *mainVC;
@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    [OKPyCommandsManager setNetwork];
    
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.window.rootViewController = self.mainVC;
    [self.window makeKeyAndVisible];
    return YES;
}
- (MainViewController *)mainVC {
    if (_mainVC == nil) {
        _mainVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateInitialViewController];
    }
    return _mainVC;
}
@end
