// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    /*repositories {
        mavenCentral()
    }*/
    dependencies {
        // Dodaj buildscript zavisnosti ako je potrebno (npr. Gradle plugin)
    }
}

allprojects {
    // Ne dodaj repozitorijume ovde, oni su sada u settings.gradle.kts
    /* repositories {
         mavenCentral()
     }*/
}