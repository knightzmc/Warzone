// Mostly borrowed from https://github.com/IntellectualSites/FastAsyncWorldEdit/blob/main/buildSrc/build.gradle.kts
import java.util.*

plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

repositories {
    gradlePluginPortal()
    maven("https://ajoberstar.org/bintray-backup/")
}

dependencies {
    implementation(gradleApi())
    implementation("org.ajoberstar.grgit:grgit-gradle:4.1.0")
}
