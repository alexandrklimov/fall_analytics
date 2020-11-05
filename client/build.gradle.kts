plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

group = "org.aklimov.fall_analytics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
    implementation("io.ktor:ktor-client-js:1.4.1")
    implementation("io.ktor:ktor-client-serialization:1.4.1")
    implementation("kscience.plotlykt:plotlykt-core-js:0.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.0")
    implementation(npm("plotly.js", "1.54.6"))

}

kotlin {
    js {
        browser {
            webpackTask {
                cssSupport.enabled = true
            }

            runTask {
                cssSupport.enabled = true
            }

            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }

        }
        binaries.executable()
    }
}
