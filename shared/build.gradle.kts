plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "org.aklimov.fall_analytics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    js {
        browser()
        dependencies {
            commonTestImplementation("org.jetbrains.kotlin:kotlin-test-js")
        }
        binaries.executable()
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
                implementation(kotlin("test-junit5"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<Test> {
    testLogging.showStandardStreams = true
}
tasks.withType<org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest> {
    useJUnitPlatform()
}
