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
