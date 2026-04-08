plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.rst.penyakitbawang"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.rst.penyakitbawang"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.tensorflow.lite.gpu)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //tensor flow
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.tensorflow.lite.support)
    implementation(libs.tensorflow.tensorflow.lite.metadata)

    //uCrop
    implementation(libs.ucrop)

    //room
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    //splashscreen
    implementation (libs.androidx.core.splashscreen)

    //glide
    implementation (libs.glide)

    // room database
    implementation (libs.androidx.room.runtime)
    ksp (libs.room.compiler)
    implementation (libs.room.ktx)

    //dagger hilt
    implementation(libs.hilt.android)
    ksp (libs.dagger.hilt.android.compiler)

    implementation(libs.androidx.fragment.ktx)
}