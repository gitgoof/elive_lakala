
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

-keep public interface com.lakala.library.* { public protected <methods>;}
-keep public class com.lakala.library.* { public protected <methods>;}
-keep public class com.lakala.library.encryption.* { public protected <methods>;}
-keep public class com.lakala.library.exception.* { public protected <methods>;}
-keep public class com.lakala.library.jni.* { public protected <methods>; }
-keep public class com.lakala.library.net.* { public protected <methods>;}
-keep public class com.lakala.library.util.* { public protected <methods>;}

-keep public class com.loopj.lakala.http.* { *;}
-keep public interface com.loopj.lakala.http.* { *;}
-keep public enum com.loopj.lakala.http.* { *;}

-keep public class org.apache.commons.codec.* {*;}
-keep public class com.google.common.* {*;}
-keep public class example.* {*;}
#sqlcipher
-keep class net.sqlcipher.** {
    *;
}

-keep public class net.sqlcipher.database.** {
    *;
}

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

-keepclassmembers class **.R$* {
    public static <fields>;
}

-dontwarn android.support.**
-dontwarn com.loopj.lakala.http.**
-dontwarn com.lakala.library.**
-dontwarn net.sqlcipher.**
-dontwarn com.google.**
