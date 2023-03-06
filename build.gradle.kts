buildscript {
    repositories { mavenCentral() }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.10")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.0" apply false
    id("com.android.library") version "7.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
}

allprojects {
    ext {
        set("kotlin_version", "1.7.10")
        set("compose_version", "1.3.0")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}