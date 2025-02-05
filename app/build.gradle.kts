plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.scmanager"
    compileSdk = 35


    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.scmanager"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuração da chave da API no BuildConfig
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }



        packaging {
            resources {
                excludes.add("META-INF/DEPENDENCIES")
            }
        }

}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.4inodev:Neomorphic-FrameLayout-Android:1.03")
    implementation ("androidx.core:core-splashscreen:1.0.0")
    implementation ("com.google.code.gson:gson:2.8.8")  // Para deserializar o JSON
    implementation ("com.google.cloud:libraries-bom:26.1.4")
    implementation ("com.google.http-client:google-http-client-gson:1.34.0")
    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")
    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
