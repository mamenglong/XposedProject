import java.time.LocalDateTime
import java.util.Random

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.lsparanoid)
}

android {
    compileSdk = 33
    namespace = "com.ml.xposedproject"
    defaultConfig {
        applicationId = namespace
        versionName = "1.0"
        versionCode = 1
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "buildTime", "\"${buildTime()}\"")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    kotlin {
        jvmToolchain(8)
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            keyPassword = "menglong"
            storeFile = file("../keystore.keystore")
            storePassword = "menglong"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    flavorDimensions.add("app")
    productFlavors {
        create("normal") {

        }
        create("taichi") {
            applicationId = "com.mx.vpnjc"
        }
    }
}

fun buildTime(): String {
    val time = LocalDateTime.now().toString()
    return time
}
dependencies {
    // implementation fileTree (dir: "libs", include: ["*.jar"])
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)

    //https://api.xposed.info/reference/de/robv/android/xposed/XposedHelpers.html
    compileOnly(libs.xposed)
    //compileOnly 'de.robv.android.xposed:api:82:sources'
    //compileOnly(name("api-82-sources") , ext("jar"))
    //compileOnly(name: 'api-82', ext: 'jar')

    implementation(libs.google.protobuf.protoc)
    implementation(libs.google.protobuf.java)
    implementation(libs.google.protobuf.java.util)
    implementation(libs.androidx.datastore)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.tencent.mmkv)
    implementation(libs.google.auto.service)
    annotationProcessor(libs.google.auto.service)
}

kapt {
    correctErrorTypes = true
}
hilt {
    enableAggregatingTask = true
}
protobuf {

}
lsparanoid{
    seed = 1213
    includeDependencies = true
    global = true
    variantFilter = {
        !it.name.contains("release")
    }
}