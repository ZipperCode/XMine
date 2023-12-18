plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")

}

android {
    namespace = BuildConfig.namespace
    compileSdk = 33

    defaultConfig {
        applicationId = BuildConfig.packageName
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        buildConfig = true
        aidl = true
        viewBinding = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    compileOnly(Xposed.API)
    implementation(DexKit.dexkit)
    implementation(AndroidX.core)
    implementation(AndroidX.appcompat)
    implementation(AndroidX.constraintLayout)
    implementation(Material.material)
    implementation(LibSu.core)
    implementation(LibSu.service)
    implementation(LibSu.io)
    implementation(LibSu.nio)
}
