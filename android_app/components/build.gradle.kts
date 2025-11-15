import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

val camerax_version = "1.0.1"


android {
    namespace = "com.js_loop_erp.components"
    compileSdk = 34
    val DEBUG_ONLY_BUILD = true

    val currentDate =  Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    val VERSION_NAME = "Rumatec_Vetcare${dateFormat.format(currentDate)}"


    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "DEBUG_ONLY_BUILD", DEBUG_ONLY_BUILD.toString())
            buildConfigField("String", "VERSION_NAME", "\"$VERSION_NAME\"")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "DEBUG_ONLY_BUILD", DEBUG_ONLY_BUILD.toString())
            buildConfigField("String", "VERSION_NAME", "\"$VERSION_NAME\"")
        }

    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(project(":mediator_common"))
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

/*
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx") {
        exclude(group = "com.google.android.gms", module = "play-services-ads-identifier")
    }
*/


    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


    implementation ("androidx.camera:camera-core:$camerax_version")
    implementation ("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    implementation ("androidx.camera:camera-view:1.0.0-alpha27")
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha27")
    implementation ("com.google.android.material:material:1.9.0") // For TabLayout
    implementation ("androidx.viewpager:viewpager:1.0.0") // For ViewPager


    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.airbnb.android:lottie:6.0.0")
}