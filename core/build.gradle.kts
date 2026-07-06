plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
//    alias(libs.plugins.android.lint)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    val xcfName = "coreKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                // --- SQLDelight ---
                implementation(libs.sqldelight.coroutines)

                // --- Ktor Client (Red) ---
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.logging)

                // --- Koin (Inyección de Dependencias de datos) ---
                implementation(libs.koin.core)

                // --- Firebase (Lógica de datos compartida) ---
                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.common)
                implementation(libs.firebase.storage)
                implementation(libs.firebase.config)
                implementation(libs.firebase.messaging)

                // --- KSafe (Manejo de errores y seguridad) ---
                implementation(libs.ksafe)
                implementation(libs.ksafe.compose)

                // --- Compose (UI compartida) ---
                implementation(libs.compose.runtime)
                implementation(libs.compose.components.resources)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.android)

                implementation(libs.android.driver)

                implementation(libs.koin.android)

                implementation(libs.coil.network.okhttp)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
                implementation(libs.native.driver)
            }
        }
    }
}

android {
    namespace = "pe.breaker.dkaviplay.core"
    // Sincronizamos con la versión usada en composeApp usando tu libs.versions
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("pe.breaker.dkaviplay.cache")
        }
    }
    linkSqlite.set(true)
}