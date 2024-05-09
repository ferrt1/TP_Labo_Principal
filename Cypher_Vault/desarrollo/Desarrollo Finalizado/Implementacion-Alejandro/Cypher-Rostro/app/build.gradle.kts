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
    implementation(libs.androidx.compose.foundation)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("com.google.code.gson:gson:2.8.8")
    //////////////////////////////////////////////

    // MlKit Face Detection ///////////////
    implementation ("com.google.mlkit:face-detection:16.1.6")
    /////////////////////////////////////////////////

    // CameraX /////////////////////////////////////
    // Camerax implementation
    val cameraxVersion = "1.3.1"
    implementation ("androidx.camera:camera-core:${cameraxVersion}")
    implementation ("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation ("androidx.camera:camera-view:${cameraxVersion}")
    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")

    // Camerax implementation

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
/*
    //// TestJunit
    val androidXTestVersion = "1.1.0"
    androidTestImplementation("androidx.test:core:$androidXTestVersion")
    androidTestImplementation("androidx.test:core-ktx:1.1.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.0")
    androidTestImplementation("androidx.test.ext:truth:1.1.0")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestUtil("androidx.test:orchestrator:1.1.1")
    // Espresso dependencies
    val espressoVersion = "3.1.1"
    androidTestImplementation( "androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso:espresso-contrib:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso:espresso-intents:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso:espresso-accessibility:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso:espresso-web:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso.idling:idling-concurrent:$espressoVersion")
    androidTestImplementation( "androidx.test.espresso:espresso-idling-resource:$espressoVersion")
*/

    // To use the androidx.test.core APIs
    androidTestImplementation("androidx.test:core:1.5.0")
    // Kotlin extensions for androidx.test.core
    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    // To use the androidx.test.espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // To use the JUnit Extension APIs
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Kotlin extensions for androidx.test.ext.junit
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    // To use the Truth Extension APIs
    androidTestImplementation("androidx.test.ext:truth:1.5.0")

    // To use the androidx.test.runner APIs
    androidTestImplementation("androidx.test:runner:1.5.2")

    // To use android test orchestrator
    androidTestUtil("androidx.test:orchestrator:1.4.2")
}
