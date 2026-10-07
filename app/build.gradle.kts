plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gameboost.optimizer"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.gameboost.optimizer"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isCrunchPngs = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    androidResources {
        noCompress += listOf("png")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = true
      buildConfig = false
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

base {
    archivesName.set("GameBoost")
}

abstract class CopyGameBoostApkTask : DefaultTask() {
    @get:InputDirectory
    abstract val apkDir: org.gradle.api.file.DirectoryProperty

    @TaskAction
    fun copyApk() {
        val src = apkDir.file("GameBoost-debug.apk").get().asFile
        if (src.exists()) {
            val dst = apkDir.file("GameBoost.apk").get().asFile
            src.copyTo(dst, overwrite = true)
        }
    }
}

val copyGameBoostApk = tasks.register<CopyGameBoostApkTask>("copyGameBoostApk") {
    apkDir.set(layout.buildDirectory.dir("outputs/apk/debug"))
}

tasks.matching { it.name == "assembleDebug" }.configureEach {
    finalizedBy(copyGameBoostApk)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  // Shizuku
  implementation(libs.shizuku.api)
  implementation(libs.shizuku.provider)
  implementation(libs.shizuku.aidl)

  // DataStore
  implementation(libs.androidx.datastore.preferences)

  // Material Icons Extended
  implementation(libs.androidx.compose.material.icons.extended)

  // Lifecycle Service
  implementation(libs.androidx.lifecycle.service)
}

