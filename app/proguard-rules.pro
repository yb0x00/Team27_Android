# 기본 Android 클래스 유지
-keep class android.** { *; }
-keep interface android.** { *; }

# WakeLock 관련 유지 규칙
-keepclassmembers class android.os.PowerManager$WakeLock {
    void acquire(long);
    void release(int);
}

# Hidden API 접근 차단 관련
# Android의 제한된 메서드 접근 경고를 무시
-dontwarn android.**

# Dex 최적화 문제 방지
-keepattributes *Annotation*
-keep class **.R$* { *; }  # 리소스 클래스 유지
-keepclassmembers class * { @androidx.annotation.Keep *; }

# Kakao Map 및 SDK 관련 유지 규칙 (기존 규칙 유지)
-keep class com.kakao.vectormap.** { *; }
-keep interface com.kakao.vectormap.**
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter
-keep class com.kakao.sdk.** { *; }
-dontwarn com.kakao.sdk.**

# https://github.com/square/okhttp/pull/6792
# OkHttp와 관련된 규칙
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

# Google Play Services 관련 유지 규칙 (기존 규칙 유지)
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# OkHttp 및 Okio 관련 유지 규칙
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn okio.**

# OkHttp ProGuard 설정 (Gson TypeAdapter와 함께 사용할 경우 추가)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson과 함께 사용하는 경우
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.apiguardian.api.API$Status
-dontwarn org.apiguardian.api.API

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn kotlin.Unit

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Retrofit 인터페이스 유지
-keep interface retrofit2.** { *; }

# Retrofit API 인스턴스의 메서드와 매개변수 주석 유지
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retrofit에서 사용되는 @Headers 주석 보존
-keepattributes *Annotation*

# CallAdapter 및 Converter 팩토리의 구체적인 형식을 유지
-keep class retrofit2.**$* { *; }
-keep class retrofit2.converter.gson.** { *; }

# OkHttp와 Okio 관련 경고 무시
-dontwarn okhttp3.**
-dontwarn okio.**

# Retrofit에서 사용하는 Kotlin 확장 관련 경고 무시
-dontwarn retrofit2.KotlinExtensions
-dontwarn kotlin.Unit

# Gson의 SerializedName 필드 유지
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# JsonAdapter를 사용하는 클래스 유지
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.TypeAdapterFactory

-keep class com.example.togetherpet.MyApplication { *; }
-keep class com.example.togetherpet.fragment.LocationService { *; }