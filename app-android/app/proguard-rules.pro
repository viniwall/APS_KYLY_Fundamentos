# Retrofit — keep all interface methods and annotations
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson — keep all DTOs in the dto package
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class br.com.kollectaops.collector.data.remote.dto.** { *; }

# Room — keep entities and DAOs
-keep class br.com.kollectaops.collector.data.local.entity.** { *; }
-keep class br.com.kollectaops.collector.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Hilt — keep generated entry points
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# WorkManager + HiltWorker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep @androidx.hilt.work.HiltWorker class * { *; }

# Keep BuildConfig
-keep class br.com.kollectaops.collector.BuildConfig { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**
