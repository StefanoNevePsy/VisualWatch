# Keep serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.visualwatch.timer.**$$serializer { *; }
-keepclassmembers class com.visualwatch.timer.** {
    *** Companion;
}
-keepclasseswithmembers class com.visualwatch.timer.** {
    kotlinx.serialization.KSerializer serializer(...);
}
