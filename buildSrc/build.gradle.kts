import org.gradle.kotlin.dsl.`kotlin-dsl`


 plugins {
    `kotlin-dsl`
     kotlin("jvm") version "1.7.10"
 }

repositories {
    mavenCentral()
}

dependencies {
 implementation(kotlin("stdlib")) // Add Kotlin standard library
}

// Configure the Kotlin compile tasks
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
 kotlinOptions {
     jvmTarget = "17" // Set the JVM target
 }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}