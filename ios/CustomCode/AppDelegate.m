//
//  AppDelegate.m
//  OneKey
//
//  Created by xiaoliang on 2020/9/28.
//

#import "MainViewController.h"
#import "AppDelegate.h"
#import "OKFirstUseViewController.h"
#import "JPUSHService.h"
#import <UserNotifications/UserNotifications.h>
#import <AdSupport/AdSupport.h>
@import SupportSDK;
@import ZendeskCoreSDK;

@interface AppDelegate () <JPUSHRegisterDelegate>
@property (nonatomic,strong)MainViewController *mainVC;
@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
#ifdef DEBUG
        [OKPyCommandsManager setNetwork];
#endif

    [self setupLanague];
    [self setupJPUSH:launchOptions];
    [self setupZendesk];

    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.window.rootViewController = self.mainVC;
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
  [JPUSHService registerDeviceToken:deviceToken];
}

- (void)setupLanague
{
    if (![[NSUserDefaults standardUserDefaults] objectForKey:kOnekey_language]) {
        //默认设为跟随系统
        [[NSUserDefaults standardUserDefaults] setObject:kOnekey_languageSys  forKey:kOnekey_language];
    }
}

- (MainViewController *)mainVC {
    if (_mainVC == nil) {
        _mainVC = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateInitialViewController];
    }
    return _mainVC;
}
- (void)resetMainVCRootViewControllerSelectSetingVc:(BOOL)isSettingVC
{
    self.window.rootViewController = nil;
    self.mainVC = nil;
    self.mainVC.isTabSettingVC = isSettingVC;
    self.window.rootViewController = self.mainVC;
}

- (void)setupZendesk {
    [ZDKZendesk initializeWithAppId:@"7e7239e265d6bcb1017a4ab16f18faafcca77abb0e2fd03f" clientId:@"mobile_sdk_client_1ddf0a8fce788502dd31" zendeskUrl:@"https://onekey.zendesk.com"];
    [ZDKSupport initializeWithZendesk:[ZDKZendesk instance]];
    id<ZDKObjCIdentity> userIdentity = [[ZDKObjCAnonymous alloc] initWithName:nil email:nil];
    [[ZDKZendesk instance] setIdentity:userIdentity];
}

- (void)setupJPUSH:(NSDictionary *)launchOptions {

    JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
    if (@available(iOS 12.0, *)) {
        entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound|JPAuthorizationOptionProvidesAppNotificationSettings;
    } else {
        entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound;
    }
    [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];

    NSString *advertisingId = [[[ASIdentifierManager sharedManager] advertisingIdentifier] UUIDString];
    [JPUSHService setupWithOption:launchOptions
                             appKey:@"8deab53d077ef22770b7521a"
                            channel:@"NA"
                   apsForProduction:NO
              advertisingIdentifier:advertisingId];
}
#pragma mark- JPUSHRegisterDelegate

// iOS 12 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification{
  if (notification && [notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    //从通知界面直接进入应用
  }else{
    //从通知设置界面进入应用
  }
}

// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler {
  // Required
  NSDictionary * userInfo = notification.request.content.userInfo;
  if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    [JPUSHService handleRemoteNotification:userInfo];
  }
  completionHandler(UNNotificationPresentationOptionAlert); // 需要执行这个方法，选择是否提醒用户，有 Badge、Sound、Alert 三种类型可以选择设置
}

// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler {
  // Required
  NSDictionary * userInfo = response.notification.request.content.userInfo;
  if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    [JPUSHService handleRemoteNotification:userInfo];
  }
  completionHandler();  // 系统要求执行这个方法
}

- (void)jpushNotificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary *)info {
}


- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {

  // Required, iOS 7 Support
  [JPUSHService handleRemoteNotification:userInfo];
  completionHandler(UIBackgroundFetchResultNewData);
}


@end
