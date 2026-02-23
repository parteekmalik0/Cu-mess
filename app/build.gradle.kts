plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") // Google services plugin for Firebase
}

android {
    namespace = "com.malikparteek.cumess"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.malikparteek.cumess"
        minSdk = 29
        targetSdk = 35
        versionCode = 29
        versionName = "24.02.26"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Match with Compose version you're using
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Use Compose BOM for consistent Compose versions
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose.android)

    // Lottie animation
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // Firebase BoM and dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")   // Realtime Database
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore

    // Compose + LiveData
    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")
    //
    implementation ("androidx.compose.material:material-icons-extended")

    //in-app update
    implementation ("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.gms:play-services-ads:24.4.0") //for ad integration purpose
    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.0") //firerstore integration
    implementation ("com.google.android.play:review-ktx:2.0.1")  //for app review

    //inmobi
    implementation ("com.inmobi.monetization:inmobi-ads-kotlin:10.8.7")




    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
