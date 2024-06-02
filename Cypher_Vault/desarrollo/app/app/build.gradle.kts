plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)

    // BASE DE DATOS ///////////////////////
    kotlin("kapt")
}

android {
    namespace = "com.example.cypher_vault"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cypher_vault"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // BASE DE DATOS ///////////////////////

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.games.activity)
    implementation(libs.androidx.compose.material)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    //////////////////////////////////////////////
    
    // MLKIT ///
    implementation("com.google.mlkit:face-detection:16.1.6")

    // MEDIAPIPE ///
    implementation("com.google.mediapipe:tasks-vision:latest.release")

    // Dependencias estándar
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    //implementation(libs.androidx.ui.graphics)
    implementation("androidx.compose.ui:ui-graphics:1.4.3")
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Asegúrate de que esta sea la versión 1.2.1
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Excluir cualquier versión conflictiva
    implementation("androidx.navigation:navigation-compose:2.7.7") {
        exclude(group = "androidx.compose.material3", module = "material3")
    }
    implementation("androidx.compose.material:material-icons-extended:1.6.6") {
        exclude(group = "androidx.compose.material3", module = "material3")
    }
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") {
        exclude(group = "androidx.compose.material3", module = "material3")
    }

    // Dependencias para pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // TENSORFLOW-LITE

    implementation("org.tensorflow:tensorflow-lite:0.0.0-nightly-SNAPSHOT")
    implementation("org.tensorflow:tensorflow-lite-gpu:0.0.0-nightly-SNAPSHOT")
    implementation("org.tensorflow:tensorflow-lite-support:0.0.0-nightly-SNAPSHOT")



    //RETROFIT

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.okhttp)


    // CameraX core library using the camera2 implementation
    val camerax_version = "1.4.0-alpha05"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    // Mail ///////////////////////

    //////////////////////////////////////////////
}

// Configuración de resolución de dependencias
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.compose.material3" && requested.name == "material3") {
            useVersion("1.2.1")
        }
    }
}