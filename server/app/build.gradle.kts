import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.20"
}

android {
    namespace = "com.example.androidrpctest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidrpctest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
}

androidComponents.onVariants { variant ->
//    return@onVariants
    val target =
        if (variant.buildType == "release") {
//        listOf("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
            listOf("x86_64")
        } else {
            listOf("x86_64")
        }

    val source = Path(projectDir.absolutePath, "src", "main", "rust")

    val cmd =
        mutableListOf("cargo", "ndk").apply {
            add("-o")
            add(Path(projectDir.absolutePath, "src", "main", "jniLibs").absolutePathString())
            add("-p")
            add(
                android.defaultConfig.minSdkVersion!!
                    .apiLevel
                    .toString(),
            )
            target.forEach {
                add("-t")
                add(it)
            }

            add("build")
            if (variant.buildType == "release") {
                add("--release")
            }
        }

    val variantName =
        variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val cargoTask =
        task<Exec>("cargo$variantName") {
            workingDir(source)
            commandLine(cmd)
        }

    project.afterEvaluate {
        val mergeTask = project.tasks.getByName("merge${variantName}JniLibFolders")
        mergeTask.dependsOn(cargoTask)
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

