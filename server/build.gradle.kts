plugins {
    kotlin("jvm")
}

group = "org.aklimov.fall_analytics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-netty:1.4.1")
}
