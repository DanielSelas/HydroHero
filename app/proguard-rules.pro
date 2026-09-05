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
# ── Keep readable crash reports ─────────────────────────────────────────
# Crashlytics needs line numbers + the original source file name to
# de-obfuscate stack traces against the uploaded mapping file.
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# ── Firebase / Crashlytics ──────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
# Crashlytics NDK / native symbol lookup
-keep class com.google.android.gms.common.** { *; }

# ── Google Mobile Ads (AdMob) ───────────────────────────────────────────
# The Ads SDK loads parts of itself reflectively and pulls in optional
# mediation classes that are not on our classpath.
-keep class com.google.android.gms.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
-keep class com.google.android.gms.internal.ads.** { *; }

# ── App model classes ───────────────────────────────────────────────────
# Data classes persisted / logged by name; keeping them avoids surprises in
# analytics payloads and SharedPreferences round-trips.
-keep class com.danielsela.hydrohero.data.** { *; }

# ── Kotlin ──────────────────────────────────────────────────────────────
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
