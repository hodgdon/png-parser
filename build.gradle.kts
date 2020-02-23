plugins {
    kotlin("multiplatform") version "1.3.61"
}

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
}

kotlin {
    jvm()
    js {
        browser
    }
    macosX64("macos") {
        compilations["main"].enableEndorsedLibs = true
        binaries {
            executable("parsePng") {
                entryPoint = "com.example.png.main"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.squareup.okio:okio-multiplatform:2.4.3")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api(kotlin("stdlib-jdk8"))
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        js().compilations["main"].defaultSourceSet {
            dependencies {
                api(kotlin("stdlib-js"))
            }
        }
        js().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    println(this.name)
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xuse-experimental=kotlin.Experimental",
        "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
        "-Xuse-experimental=kotlinx.cli.ExperimentalCli",
        "-XXLanguage:+InlineClasses",
        "-Xmulti-platform",
        "-Xuse-experimental=kotlinx.cli.ExperimentalCli",
        "-Xuse-experimental=kotlin.ExperimentalMultiplatform"
    )
}
