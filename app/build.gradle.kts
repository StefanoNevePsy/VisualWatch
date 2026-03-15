plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.visualwatch.timer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.visualwatch.timer"
        // Wear OS 3.0 = API 30 (Galaxy Watch 4 minimum)
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Compose for Wear OS
    val wearComposeVersion = "1.3.0"
    implementation("androidx.wear.compose:compose-material:$wearComposeVersion")
    implementation("androidx.wear.compose:compose-foundation:$wearComposeVersion")
    implementation("androidx.wear.compose:compose-navigation:$wearComposeVersion")

    // Core Compose
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.compose.runtime:runtime:1.6.1")
    implementation("androidx.compose.foundation:foundation:1.6.1")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

    // DataStore for preferences persistence
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Wear OS specific
    implementation("androidx.wear:wear:1.3.0")

    // Ambient mode (Always-On Display) support
    implementation("androidx.wear:wear-ongoing:1.0.0")

    // Core KTX
    implementation("androidx.core:core-ktx:1.12.0")

    // JSON serialization for presets
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
}
