import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

val ktorVersion = "1.4.1"

repositories {
    mavenCentral()
}

dependencies {
    //implementation(project(":shared"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation( "io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation( "org.springframework:spring-jdbc:5.2.9.RELEASE")
    implementation( "com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("io.github.microutils:kotlin-logging:2.0.3")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.nield:kotlin-statistics:1.2.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.4.RELEASE") {
        exclude("org.junit.vintage","junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc:2.3.4.RELEASE")
    testImplementation("org.postgresql:postgresql:42.2.16")
    testImplementation("com.zaxxer:HikariCP:3.2.0")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "14"
}

tasks.withType<Test>().all{
    useJUnitPlatform()
}
