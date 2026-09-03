# Retrofit
-dontwarn retrofit2.**
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep class kotlin.coroutines.Continuation

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * extends kotlinx.serialization.internal.GeneratedSerializer {
    *;
}
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *;
}
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    *;
}

# Firebase
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

# Room
-dontwarn androidx.room.**
-keep class androidx.room.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
