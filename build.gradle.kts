plugins {
    kotlin("jvm") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
    id("io.freefair.lombok") version "8.6"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.flawcra.cc/repository/maven-mirrors/")
}

val minecraftVersion = "1.21.4"
val includedDependencies = mutableListOf<String>()

fun Dependency?.deliver() = this?.apply {
    val computedVersion = version ?: kotlin.coreLibrariesVersion
    includedDependencies += "${group}:${name}:${computedVersion}"
}

val deliverDependencies = listOf(
    "de.tr7zw:item-nbt-api:2.14.1",
    "net.wesjd:anvilgui:1.6.6-SNAPSHOT",
    "com.googlecode.json-simple:json-simple:1.1.1",
    "org.apache.commons:commons-text:1.10.0",
    "org.jetbrains:annotations:13.0",
    "com.mojang:authlib:1.5.25",
)

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    // BukkitOfUtils
    compileOnly("com.github.Streamline-Essentials:BukkitOfUtils:master-SNAPSHOT")
    annotationProcessor("com.github.Streamline-Essentials:BukkitOfUtils:master-SNAPSHOT")

    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.6")

    deliverDependencies.forEach { dependency ->
        api(dependency).deliver()
    }
}

group = "net.seanomik"
version = "0.7.9-SNAPSHOT"
description = "EnergeticStorage"

tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<ProcessResources> {
        expand(
            "deliverdeps" to includedDependencies.joinToString("\n"),
        )
    }

    register<JavaCompile>("compileMain") {
        source = fileTree("src/main/java")
        classpath = files(configurations.runtimeClasspath)
        destinationDirectory.set(file("build/classes/java/main"))
        options.release.set(21)
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/java")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        freeCompilerArgs.addAll(
            listOf(
                "-opt-in=kotlin.RequiresOptIn"
            )
        )
    }
}