
-dontoptimize
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontshrink

-keepparameternames
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingServic

-keepnames class *
-keepclassmembernames class * { public *; protected *;}

-keep public class com.lakala.core.* { public protected <methods>;}
-keep public interface com.lakala.core.* { public protected <methods>;}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class cn.com.fmsh.cube.driver.* { public *;protected *; }
-keep public class com.lakala.android.swiper.adapter.* { public *; }
-keep public class com.lakala.lphone.** {*;}
-keep public class com.uart.jnilib.** {*;}
-keep public class com.avos.** {*;}
-keep public class com.newland.** {*;}
-keep public class org.apache.commons.codec.** {*;}


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

#sqlcipher
-keep class net.sqlcipher.** {
    *;
}

-keep public class net.sqlcipher.database.** {
    *;
}

# phonegap
-keepattributes *Annotation*
-keep class com.lakala.core.cordova.** { *; }
-keep public class * extends com.lakala.core.cordova.cordova.CordovaPlugin

#zxing
-keep class com.google.zxing.** { *; }

-keep class com.crashlytics.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

#taibao insurance
-keep class cn.newtouch.cpic.policy.** {
    *;
}

-dontwarn com.newland.crypto.*
-dontwarn android.support.**
-dontwarn com.lakala.core.**
