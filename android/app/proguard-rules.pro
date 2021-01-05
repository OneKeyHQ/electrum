# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class no.nordicsemi.android.dfu.** { *; }

# Bean
-keepclassmembers,includecode class org.haobtc.onekey.bean.** { <fields>; }
-keepclassmembers,includecode class org.haobtc.onekey.bean.**$** { <fields>; }

# ViewModel
-keepnames class androidx.lifecycle.ViewModel
-keepclassmembers public class * extends androidx.lifecycle.ViewModel { public <init>(...); }

# Python Runtime
-keep class cn.com.heaton.blelibrary.** { *; }
-keep class android.hardware.usb.* { *; }
-keep class org.haobtc.onekey.utils.Daemon { public void onCallback(java.lang.String); }

# AspectJ Aop
-keep class org.haobtc.onekey.aop.SingleClick
-keep class org.haobtc.onekey.aop.CheckConnection
-keepclassmembers class * {
    @org.haobtc.onekey.aop.SingleClick <methods>;
}
-keepclassmembers class * {
    @org.haobtc.onekey.aop.CheckConnection <methods>;
}

##---------------Begin: proguard configuration for EventBus  ----------
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
##---------------Begin: proguard configuration for EventBus  ----------


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for Bugly  ----------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
##---------------End: proguard configuration for Bugly  ----------


##---------------Begin: proguard configuration for Rxjava  ----------
-dontwarn java.util.concurrent.Flow*
##---------------End: proguard configuration for Rxjava  ----------


##---------------Begin: proguard configuration for XPopup  ----------
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}
##---------------Begin: proguard configuration for XPopup  ----------
