import java.util.Date
import java.text.SimpleDateFormat

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
}

val packageName = "com.js_loop_erp.rumatec_vetcare_erp"
val camerax_version = "1.0.1"

android {
    val currentDate =  Date()
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy_HH_mm_ss")
    val VERSION_NAME = "Rumatec_Vetcare${dateFormat.format(currentDate)}"

    namespace = "com.js_loop_erp.rumatec_vetcare_erp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.js_loop_erp.rumatec_vetcare_erp"
        minSdk = 26
        targetSdk = 35
        versionCode = 21
        versionName = "Rumatec_Vetcare${dateFormat.format(currentDate)}_v0_${versionCode}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APPLICATION_ID",  "\"com.js_loop_erp.rumatec_vetcare_erp\"")
        setProperty("archivesBaseName", "$versionName")
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            /*firebaseCrashlytics {
                mappingFileUploadEnabled = true
            }*/
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ""
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:3.12.0")

    implementation ("com.google.code.gson:gson:2.8.6")

    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.2")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.2")
    implementation ("androidx.activity:activity-ktx:1.7.2")
    implementation ( "androidx.fragment:fragment-ktx:1.6.0")

    implementation (platform("com.google.firebase:firebase-bom:33.1.2")) // or latest
    implementation ("com.google.firebase:firebase-crashlytics")
    implementation ("com.google.firebase:firebase-analytics")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("androidx.camera:camera-core:$camerax_version")
    implementation ("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    implementation ("androidx.camera:camera-view:1.0.0-alpha27")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha27")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation (project(":components"))
    implementation(project(":mediator_common"))

    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}