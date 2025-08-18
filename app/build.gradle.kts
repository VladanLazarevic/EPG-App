plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.epg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.epg"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Učitaj API_KEY iz local.properties
        val apiKey: String = project.rootProject.file("local.properties")
            .reader()
            .useLines { lines ->
                lines.firstOrNull { it.startsWith("API_KEY=") }
                    ?.substringAfter("=")
                    ?: ""
            }
        buildConfigField("String", "API_KEY", "\"$apiKey\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // Omogućava generisanje BuildConfig klase
    }
}

dependencies {
    //dodaci//
    // Osnovna kotlinx.coroutines biblioteka
    /*implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Android-specifična verzija (za Dispatchers.Main i ViewModelScope)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Converter za JSON (najčešće se koristi Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // (Opcionalno) Ako želiš da koristiš OkHttp za logovanje i druge funkcionalnosti
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("io.coil-kt:coil-compose:2.3.0")
    //dodaci
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)*/
    // ************************************************************************** //

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Osnovna kotlinx.coroutines biblioteka
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Android-specifična verzija (za Dispatchers.Main i ViewModelScope)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Converter za JSON (najčešće se koristi Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // (Opcionalno) Ako želiš da koristiš OkHttp za logovanje i druge funkcionalnosti
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("io.coil-kt:coil-compose:2.3.0")
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
    // TV COMPOSE
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)

    implementation(libs.play.services.ads.identifier)
    implementation(libs.androidx.media3.common)
    implementation("androidx.activity:activity-ktx:1.8.0")

    //EXO PLAYER
    // androidx.media3 je najnovija verzija ExoPlayer-a
    //implementation("androidx.media3:media3-exoplayer:1.8.0")
    //implementation("androidx.media3:media3-ui:1.8.0")
    //implementation("androidx.media3:media3-exoplayer-hls:1.8.0")
    //implementation("androidx.media3:media3-ui-compose:1.3.1")

    //LOTTIE ANIMACIJE//
    implementation(libs.lottie.compose)
    //implementation(libs.androidx.media3.ui) // Proveri najnoviju verziju (možda je sada 6.3.0 ili novija)
    // Sada koristimo reference iz toml fajla
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.ui)
    implementation(libs.androidx.ui.tooling.preview)
    //implementation(libs.media3.ui.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //okhttp//
    //implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
}