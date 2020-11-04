plugins {
    kotlin("jvm")
}

group = "org.aklimov.fall_analytics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:1.4.1")
}
