/**
 * LIBRARY
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    id("com.android.library")
    `maven-publish`
}

group = "de.visualdigits"
version = "1.2.0-SNAPSHOT"

kotlin {
    androidTarget {
        withSourcesJar()
        publishLibraryVariants("release", "debug")

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    jvm("desktop") {
        withSourcesJar()
    }

    jvmToolchain(21)

    sourceSets {
        val androidMain by getting {
            dependencies {
                // android
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // android tv
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2026.03.00"))
                implementation(libs.androidx.tv.material)
                implementation(libs.androidx.ui.tooling)
                implementation(libs.androidx.ui.tooling.preview)
            }
        }

        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(libs.bundles.compose)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.ksoup.core)
            implementation(libs.kermit)

            implementation(libs.compose.colorpicker)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.engine)
            implementation(libs.junit.platform.launcher)
            implementation(libs.jackson.core)
            implementation(libs.jackson.module.kotlin)
            implementation(libs.kotlinx.coroutines.test)
        }

        val jvmMain by creating {
            dependsOn(commonMain.get())
        }
        val desktopMain by getting {
            dependsOn(jvmMain)

            dependencies {
                implementation("org.jetbrains.compose.foundation:foundation:1.10.3")
            }
        }

        val jvmTest by creating {
            dependsOn(commonTest.get())
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.junit.jupiter.api)
                implementation(libs.junit.jupiter.engine)
                implementation(libs.junit.platform.launcher)
            }
        }

        val desktopTest by getting {
            dependsOn(jvmTest)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

android {
    namespace = "de.visualdigits.essence"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    // Das ersetzt withJava() im klassischen Plugin
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    publications {
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sknull/essence")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
