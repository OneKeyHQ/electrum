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

# Guide Main Screen
-keep class zendesk.support.HelpCenterSettings { *; }
-keep class zendesk.support.HelpResponse { *; }
-keep class zendesk.support.ArticlesListResponse { *; }
-keep class zendesk.support.CategoryItem { *; }
-keep class zendesk.support.SectionItem { *; }
-keep class zendesk.support.ArticleItem { *; }
-keep class zendesk.support.SeeAllArticlesItem { *; }
-keep class zendesk.support.guide.HelpCenterActivity { *; }

# Guide Search Results
-keep class zendesk.support.guide.HelpSearchFragment { *; }
-keep class zendesk.support.ArticlesSearchResponse { *; }
-keep class zendesk.support.SearchArticle { *; }
-keep class zendesk.support.guide.HelpSearchRecyclerViewAdapter { *; }
-keep class zendesk.support.HelpCenterSearch { *; }
-keep class zendesk.support.Category { *; }
-keep class zendesk.support.Section { *; }
-keep class zendesk.support.Article { *; }

# Guide View Article
-keep class zendesk.support.guide.ArticleViewModel { *; }
-keep class zendesk.support.guide.ArticleConfiguration { *; }
-keep class zendesk.support.guide.ViewArticleActivity { *; }
-keep class zendesk.support.ArticleResponse { *; }
-keep class zendesk.support.ArticleVote { *; }
-keep class zendesk.support.ArticleVoteResponse { *; }
-keep class zendesk.support.ZendeskArticleVoteStorage { *; }
-keep class zendesk.support.AttachmentResponse { *; }
-keep class zendesk.support.HelpCenterAttachment { *; }

# Support Requests (Create, Update, List)
-keep class zendesk.support.request.** { *; }
-keep class zendesk.support.requestlist.** { *; }
-keep class zendesk.support.SupportSdkSettings { *; }
-keep class zendesk.support.Request { *; }
-keep class zendesk.support.CreateRequest { *; }
-keep class zendesk.support.Comment { *; }
-keep class zendesk.support.CommentResponse { *; }
-keep class zendesk.support.CommentsResponse { *; }
-keep class zendesk.support.EndUserComment { *; }
-keep class zendesk.support.ZendeskRequestStorage { *; }
-keep class zendesk.support.ZendeskRequestProvider { *; }
-keep class zendesk.support.CreateRequestWrapper { *; }
-keep class zendesk.support.UpdateRequestWrapper { *; }
-keep class zendesk.support.RequestsResponse { *; }
-keep class zendesk.support.RequestResponse { *; }

# Support Attachments
-keep class zendesk.support.UploadResponse { *; }
-keep class zendesk.support.UploadResponseWrapper { *; }
-keep class zendesk.support.ZendeskUploadProvider { *; }
-keep class zendesk.support.Attachment { *; }
# Support SDK
-keepnames class zendesk.support.** { *; }
# Core SDK
-keep class zendesk.core.** { *; }
