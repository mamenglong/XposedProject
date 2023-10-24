import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.time.format.DateTimeFormatter

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
        minSdk = 24
        versionCode = getVersionCode()
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
fun getVersionCode(): Int {
    val base = "2023-09-03 23:59:59"
    val target = LocalDateTime.parse(base,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    return (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))- target.toEpochSecond(ZoneOffset.of("+8"))).toInt()
}
fun buildTime(): String {
    val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val time = dtf.format(LocalDateTime.now())
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
    compileOnly(files("libs/api-82-sources.jar"))
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
    //https://luckypray.org/DexKit/zh-cn/guide/example.html#%E5%A4%9A%E6%9D%A1%E4%BB%B6%E5%8C%B9%E9%85%8D%E7%B1%BB%E5%A4%9A%E6%9D%A1%E4%BB%B6%E7%94%A8%E6%B3%95%E7%A4%BA%E4%BE%8B%E3%80%82
    implementation(libs.dexkit)
}

kapt {
    correctErrorTypes = true
}
hilt {
    enableAggregatingTask = true
}
lsparanoid {
    //https://github.com/LSPosed/LSParanoid
    seed = 1213
    includeDependencies = true
    global = false
    variantFilter = {
        !it.name.contains("release")
    }
}
protobuf {
    //配置 protoc 编译器
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    //配置生成目录，编译后会在 build 的目录下生成对应的java文件
    generateProtoTasks {
        all().configureEach {
//            builtins {
//                remove(java)
//            }
            builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}