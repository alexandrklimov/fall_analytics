plugins {
    id("com.github.node-gradle.node") version "3.0.0-rc3"
}

group = "org.aklimov.fall_analytics"
version = "1.0-SNAPSHOT"

tasks.register<com.github.gradle.node.npm.task.NpxTask>("build"){
    dependsOn("npmInstall")
    command.set("ng")
    args.addAll("build")
}
