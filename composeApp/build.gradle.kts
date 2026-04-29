import java.util.Properties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.buildkonfig)
}

val secretsFile = rootProject.file("secrets.properties")
val secrets = Properties().apply {
    if (secretsFile.exists()) load(secretsFile.inputStream())
}
val mapsApiKey = secrets.getProperty("MAPS_API_KEY", "")
val versionName = libs.versions.app.version.name.get()
val versionCode = libs.versions.app.version.code.get()

buildkonfig {
    packageName = "pe.breaker.poolstreet"
    objectName = "AppConfigGlobal"
    exposeObjectWithName = "AppConfigGlobal"

//        ./gradlew :composeApp:generateBuildKonfig
    defaultConfigs {
        buildConfigField(STRING, "MAPS_API_KEY", mapsApiKey)
        buildConfigField(STRING, "VERSION_NAME", versionName)
        buildConfigField(INT, "VERSION_CODE", versionCode)
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    cocoapods{
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "15.4"
        name = "composeApp"

        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        extraSpecAttributes["libraries"] = "'sqlite3'"
        extraSpecAttributes["pod_target_xcconfig"] = "{ 'OTHER_LDFLAGS' => '-lsqlite3' }"

        pod("GoogleMaps") {
            version = "8.4.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseCore"){ linkOnly = true }
        pod("FirebaseAuth"){ linkOnly = true }
        pod("FirebaseFirestore"){ linkOnly = true }
        pod("FirebaseRemoteConfig"){ linkOnly = true }
        pod("FirebaseStorage"){ linkOnly = true }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            languageSettings.optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)

            implementation(libs.android.driver)

            implementation(libs.koin.android)

            implementation(libs.maps.compose)
            implementation(libs.maps.compose.utils)
            implementation(libs.play.services.maps)

            implementation(libs.coil.network.okhttp)

            implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
            implementation("com.google.zxing:core:3.5.3")
            implementation("androidx.camera:camera-camera2:1.2.3")
            implementation("androidx.camera:camera-lifecycle:1.2.3")
            implementation("androidx.camera:camera-view:1.2.3")
            implementation("com.google.mlkit:barcode-scanning:17.3.0")
            implementation("com.google.guava:guava:33.5.0-jre")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.logging)

            implementation(libs.sqldelight.coroutines)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.ksafe)
            implementation(libs.ksafe.compose)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.tab)

            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.common)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.config)
            implementation(libs.firebase.messaging)

            implementation(libs.material.icons.extended)

            implementation("dev.icerock.moko:permissions:0.20.1")
            implementation("dev.icerock.moko:permissions-notifications:0.20.1")
            implementation("dev.icerock.moko:permissions-camera:0.20.1")
            implementation("dev.icerock.moko:permissions-compose:0.20.1")

            implementation(libs.imagepickerkmp)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            implementation(libs.qr.kit)

            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.native.driver)
        }
    }
}

android {
    namespace = "pe.breaker.dkaviplay"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "pe.breaker.dkaviplay"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCode
        versionName = versionName
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("pe.breaker.dkaviplay.cache")
        }
    }
    linkSqlite.set(true)
}