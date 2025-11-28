buildscript {
    extra.apply {
        set("compose_version", "1.2.0")
        set("kotlin_version", "1.9.22") // Ensure this variable matches the plugin below
        set("camerax_version", "1.3.0-beta01")
        set("hilt_version", "2.51.1")
    }
    // ...
    dependencies {
        // ...
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath ("com.android.tools.build:gradle:8.13.1")
        // UPDATE THIS LINE:
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.1" apply false
    id("com.android.library") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
}